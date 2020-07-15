package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupService implements IGroupService {

    @Autowired
    GroupRepository groupRepository;

    @Override
    public Group getGroupById(String groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }
}
