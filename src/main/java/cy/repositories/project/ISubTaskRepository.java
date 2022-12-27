package cy.repositories.project;

import cy.entities.project.SubTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ISubTaskRepository extends JpaRepository<SubTaskEntity, Long> {
    @Modifying
    @Transactional
    // find record had isDelete = true and timeDelete > 12h
    @Query(value = "select * from tbl_sub_tasks where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<SubTaskEntity> checkSubTasksDelete();

    @Query(value = "select * from tbl_sub_tasks where task_id = ?1", nativeQuery = true)
    List<SubTaskEntity> findByTaskId(Long id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_sub_tasks SET `status` = ?2 WHERE id = ?1",nativeQuery = true)
    void updateStatusSubTask(Long id,String status);

    Page<SubTaskEntity> findAllByTask_Id(Long id,Pageable pageable);
}
