package com.honeybadgers.realtimescheduler.respository.jpa;

import com.honeybadgers.realtimescheduler.domain.User;
import com.honeybadgers.realtimescheduler.domain.jpa.Role;
import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.jpa.TaskPostgresRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(basePackages = {"com.honeybadgers.realtimescheduler.repository.jpa"})
@EntityScan(basePackageClasses = {Task.class, User.class, Role.class})
@ActiveProfiles("postgre")
public class TaskPostgresRepositoryTest {


    @Autowired
    private TaskPostgresRepository taskPostgresRepository;

    @Test
    public void testFindById() {
        // given
        Task task = new Task(1L, 10, "testTask1", null, new Timestamp(System.currentTimeMillis()), null);
        taskPostgresRepository.save(task);

        // when
        Optional<Task> found = taskPostgresRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getId())
                .isEqualTo(task.getId());
    }
}
