package cy.repositories.project;

import cy.entities.project.SubTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ISubTaskRepository extends JpaRepository<SubTaskEntity, Long> {
    @Modifying
    @Transactional
    // find record had isDelete = true and timeDelete > 5'
    @Query(value = "select * from tbl_sub_tasks where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 5 MINUTE)))",nativeQuery = true)
    List<SubTaskEntity> checkSubTasksDelete();

    @Query(value = "select * from tbl_sub_tasks where task_id = ?1", nativeQuery = true)
    List<SubTaskEntity> findByTaskId(Long id);
}
