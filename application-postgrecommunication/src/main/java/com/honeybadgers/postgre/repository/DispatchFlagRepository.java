package com.honeybadgers.postgre.repository;

import com.honeybadgers.models.model.DispatchFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchFlagRepository extends JpaRepository<DispatchFlag, String> {
}
