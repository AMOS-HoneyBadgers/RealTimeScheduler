do $$
begin
for r in 1..2 loop
	INSERT INTO task(
		id,
		group_id,
		priority,
		active_times,
		working_days,
		status,
		type_flag,
		mode,
		retries,
		force,
		total_priority,
		history
	) VALUES(
		(md5(random()::text || clock_timestamp()::text)::uuid)::character varying,
		'TestGroupRunAlwaysNoLimit',
		900,
		'[
    		{
        		"from": "12:00:00",
	        	"to": "15:00:00"
	    	},
	    	{
	        	"from": "17:00:00",
	        	"to": "20:00:00"
	    	}
		]'::jsonb,
		ARRAY[1,1,1,1,1,1,1]::integer[],
		'Scheduled',
		'Batch',
		'Parallel',
		0,
		false,
		900,
		'[{
	        "status": "Waiting",
	        "timestamp": 1594728713567
	    }]'::jsonb
	);
end loop;
end;
$$;