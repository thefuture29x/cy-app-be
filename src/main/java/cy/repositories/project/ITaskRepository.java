package cy.repositories.project;

import cy.entities.project.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ITaskRepository extends JpaRepository<TaskEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "select * from tbl_tasks where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<TaskEntity> checkTasksDelete();

    @Query(value = "select * from tbl_tasks where feature_id = ?1", nativeQuery = true)
    List<TaskEntity> findByFeatureId(Long id);
}
