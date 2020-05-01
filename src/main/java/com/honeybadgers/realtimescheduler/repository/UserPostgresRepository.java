package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.realtimescheduler.domain.redis.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"postgre"})
public interface UserPostgresRepository extends JpaRepository<User, String> {
}
