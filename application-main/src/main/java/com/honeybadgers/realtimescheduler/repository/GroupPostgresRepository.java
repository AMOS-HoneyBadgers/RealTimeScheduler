package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.Group;
import com.honeybadgers.realtimescheduler.model.GroupAncestorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupPostgresRepository extends JpaRepository<Group, String> {
}
