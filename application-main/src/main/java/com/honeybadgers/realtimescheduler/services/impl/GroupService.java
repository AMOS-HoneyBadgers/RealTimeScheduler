package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupService implements IGroupService {

    @Autowired
    GroupRepository groupPostgresRepository;

    @Override
    public List<Group> getAllGroups() {
        return groupPostgresRepository.findAll();
    }

    @Override
    public Group getGroupById(String groupId) {
        return groupPostgresRepository.findById(groupId).orElse(null);
    }

    @Override
    public void uploadGroup(Group grp) {
        groupPostgresRepository.save(grp);
    }

    @Override
    public void deleteGroup(String id) {
        groupPostgresRepository.deleteById(id);
    }
}
