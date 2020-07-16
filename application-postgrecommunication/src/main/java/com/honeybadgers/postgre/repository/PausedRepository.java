package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.Paused;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PausedRepository extends JpaRepository<Paused, String> {

    /**
     * Delete all paused elements, whose resume_date is in the past
     * @return list of all deleted elements (for logging)
     */
    @Query(value = "DELETE FROM paused WHERE resume_date < (NOW() AT TIME ZONE 'UTC')\\:\\:TIMESTAMP RETURNING *", nativeQuery = true)
    List<Paused> deleteAllExpired();

    /**
     * Delete paused entity with given id
     * @param id id of paused entity to be deleted
     * @return Optional of deleted paused entity or empty Optional if paused entity with id not found
     */
    @Query(value = "DELETE FROM paused WHERE id=?1 RETURNING *", nativeQuery = true)
    Optional<Paused> deleteByIdCustomQuery(String id);

    /**
     * Insert new paused entity into DB
     * @param id id of new paused entity
     * @param resumeDate resumeDate of new paused entity
     * @return Optional of new Paused entity if successful or Optional.empty()
     */
    @Query(value = "INSERT INTO paused(id,resume_date) VALUES(?1,?2) RETURNING *", nativeQuery = true)
    Optional<Paused> insertCustomQuery(String id, Timestamp resumeDate);

    /**
     * Insert new paused entity into DB without timestamp
     * Written like this due to jpa failing to parse java null to sql null in case of timestamp (java null equals sql type bytea)
     * @param id id of new paused entity
     * @return Optional of new Paused entity if successful or Optional.empty()
     */
    @Query(value = "INSERT INTO paused(id) VALUES(?1) RETURNING *", nativeQuery = true)
    Optional<Paused> insertCustomQueryNoTimestamp(String id);
}
