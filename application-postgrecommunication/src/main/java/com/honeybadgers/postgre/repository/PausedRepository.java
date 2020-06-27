package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.Paused;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PausedRepository extends JpaRepository<Paused, String> {
}
