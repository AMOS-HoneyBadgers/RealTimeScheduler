package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockRepository extends JpaRepository<Lock, String> {

    @Query(value = "INSERT INTO lock(id,is_dispatched) VALUES(?1,false) RETURNING *", nativeQuery = true)
    Optional<Lock> insert(String id);
}
