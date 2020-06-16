package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.realtimescheduler.config.H2TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(H2TestConfig.class)
public class TaskPostgresRepositoryTest {

    @Autowired private EntityManager entityManager;

    @Autowired
    private TaskPostgresRepository taskPostgresRepository;

    Group rootGroup;

    @Before
    public void insertRootGroup() {

        rootGroup = new Group();
        rootGroup.setId("TEST");
        rootGroup.setParallelismDegree(2);

        entityManager.persist(rootGroup);
    }

    @Test
    public void testFindById() {
        // given
        Task task = new Task();
        task.setId("TestUUID");
        task.setGroup(rootGroup);
        task.setModeEnum(ModeEnum.Sequential);
        task.setIndexNumber(1);
        taskPostgresRepository.save(task);

        // when
        Optional<Task> found = taskPostgresRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getId())
                .isEqualTo(task.getId());
    }
}
