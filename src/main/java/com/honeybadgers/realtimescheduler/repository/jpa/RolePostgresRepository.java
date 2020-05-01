package com.honeybadgers.realtimescheduler.repository.jpa;

import com.honeybadgers.realtimescheduler.domain.jpa.Role;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"postgre"})
public interface RolePostgresRepository extends JpaRepository<Role, String> {
}
