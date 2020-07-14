-- ?1 current working day [1,7] due to postgres ARRAY index starting at 1 -,- ()

WITH RECURSIVE group_tree AS (
	SELECT id, 
	ARRAY[]::CHARACTER VARYING[] || id AS ancestors, 
	ARRAY[[]]::INTEGER[][] || ARRAY[working_days] as days,
	ARRAY[]::JSONB[] || active_times as times
	FROM public.group 
	WHERE parent_id IS NULL 
	
	UNION ALL 
	
	SELECT g.id, 
	g.id || t.ancestors, 
	ARRAY[g.working_days] || t.days, 
	g.active_times || t.times
	FROM public.group as g, group_tree as t 
	WHERE g.parent_id = t.id
), wrapped AS ( -- this wrapped is needed due to jsonb_array_elements() otherwise eliminating all tasks which do not have any active_times (-> run always)
	SELECT t.id,
	-- merge task.active_times json with groups.times and remove empty json arrays from jsonb[]
	-- take first element (same reason as for working_days) and unnest inorder to make jsonb[] -> jsonb (jsonb[] only contains 1 entry so no problem)
	unnest((array_remove((t.active_times || gt.times), '[]'))[1:1]) as unnested_active_times,
	--(case when array_dims((ARRAY[t.working_days] || gt.days)[1:1]) IS NULL then null else reduce_dim((ARRAY[t.working_days] || gt.days)[1:1]) end) as first_working_days --not working due to reduce_dim not allowed in case (set returning functions in general)
	-- merge task.working_days with groups.all_working_days and retrieve first element (contains first not null element from bottom (task) to top (topmost group in ancestor tree))
	-- catch case, when no element in the tree (neither the task nor ANY of the groups) have any working_days -> treat as ARRAY[1,1,1,1,1,1,1] -> run always (gt.days = '{}' due to concat of array, more or less meaning {null} (2 dim array))
	-- afterwards remove first dimension of array (ARRAY[][] -> ARRAY[])
    reduce_dim((case when (t.working_days IS NULL AND gt.days = '{}') then ARRAY[ARRAY[1,1,1,1,1,1,1]]::integer[][] else (ARRAY[t.working_days] || gt.days)[1:1] end)) as first_working_days
	FROM task t JOIN group_tree gt ON t.group_id=gt.id
), first_not_null_from_tree AS (
	SELECT t.id, 
	json_elements.value as first_active_times, 
	w.first_working_days
	FROM task t JOIN wrapped w ON t.id=w.id LEFT JOIN jsonb_array_elements(w.unnested_active_times) json_elements ON true
) --SELECT * FROM first_not_null_from_tree
--SELECT * FROM wrapped
-- 4 is used as ?1 for testing

SELECT DISTINCT t.*
FROM task t 
LEFT JOIN first_not_null_from_tree c ON t.id=c.id
WHERE t.status='Scheduled' 
AND c.first_working_days[4]=1 -- not neccessary to check on null due to default case in wrapped
AND (case when c.first_active_times IS NULL then true else TO_TIMESTAMP(c.first_active_times->>'to', 'HH24:MI:SS')::TIME >= CURRENT_TIME end) 
AND (case when c.first_active_times IS NULL then true else TO_TIMESTAMP(c.first_active_times->>'from', 'HH24:MI:SS')::TIME <= CURRENT_TIME end)
