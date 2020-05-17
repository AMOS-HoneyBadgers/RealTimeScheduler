package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupPostgresRepository extends JpaRepository<Group, String> {

}
