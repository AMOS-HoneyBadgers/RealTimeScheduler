package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LockRepository extends JpaRepository<Lock, String> {
}
