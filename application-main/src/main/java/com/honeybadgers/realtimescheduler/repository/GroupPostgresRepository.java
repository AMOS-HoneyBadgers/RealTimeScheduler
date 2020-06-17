package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupPostgresRepository extends JpaRepository<Group, String> {
}
