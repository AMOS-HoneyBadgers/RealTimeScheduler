package com.honeybadgers.realtimescheduler.respository.jpa;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.jpa.TaskPostgresRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("postgre")
public class TaskPostgresRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskPostgresRepository taskPostgresRepository;

    @Test
    public void whenFindById_thenReturnTask() {
        // given
        Task task = new Task(1L, 10, "testTask1", null, new Timestamp(System.currentTimeMillis()), null);
        entityManager.persist(task);
        entityManager.flush();

        // when
        Optional<Task> found = taskPostgresRepository.findById(task.getId());

        // then
        assertTrue(found.isPresent());
        assertThat(found.get().getId())
                .isEqualTo(task.getId());
    }
}
