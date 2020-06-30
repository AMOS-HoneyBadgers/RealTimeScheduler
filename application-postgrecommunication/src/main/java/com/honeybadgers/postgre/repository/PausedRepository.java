package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.Paused;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PausedRepository extends JpaRepository<Paused, String> {

    /**
     * Delete all paused elements, whose resume_date is in the past
     * @return list of all deleted elements (for logging)
     */
    @Query(value = "DELETE FROM paused WHERE resume_date < (NOW() AT TIME ZONE 'UTC')::TIMESTAMP RETURNING *", nativeQuery = true)
    List<Paused> deleteAllExpired();
}
