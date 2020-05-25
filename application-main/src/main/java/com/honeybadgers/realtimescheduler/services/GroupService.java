package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.Group;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GroupService {

    @Autowired
    GroupPostgresRepository groupPostgresRepository;

    public List<Group> getAllGroups() {
        return this.groupPostgresRepository.findAll();
    }

    public Group getGroupById(String groupId) {
        return groupPostgresRepository.findById(groupId).orElse(null);
    }

    public void uploadGroup(Group grp) {
        this.groupPostgresRepository.save(grp);
    }

    public void deleteGroup(String id) {
        this.groupPostgresRepository.deleteById(id);
    }
}
