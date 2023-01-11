package cy.repositories.project;

import cy.dtos.project.SubTaskDto;
import cy.entities.project.SubTaskEntity;
import cy.entities.project.TaskEntity;
import cy.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface ISubTaskRepository extends JpaRepository<SubTaskEntity, Long> {
    @Modifying
    @Transactional
    // find record had isDelete = true and timeDelete > 12h
    @Query(value = "select * from tbl_sub_tasks where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<SubTaskEntity> checkSubTasksDelete();

    @Query(value = "select * from tbl_sub_tasks where task_id = ?1", nativeQuery = true)
    List<SubTaskEntity> findByTaskId(Long id);

    @Query(value = "SELECT st FROM SubTaskEntity st WHERE st.task.id = :taskId AND st.name LIKE CONCAT('%',:keyword,'%')")
    Page<SubTaskEntity> findByTaskIdWithPaging(@Param("taskId") Long taskId, @Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_sub_tasks SET `status` = ?2 WHERE id = ?1",nativeQuery = true)
    void updateStatusSubTask(Long id,String status);

    @Query(value = "SELECT sub.* FROM `tbl_projects` pro \n" +
            "JOIN `tbl_features` fea ON pro.id = fea.project_id\n" +
            "JOIN `tbl_tasks` tas ON fea.id = tas.feature_id\n" +
            "JOIN `tbl_sub_tasks` sub ON tas.id = sub.task_id\n" +
            "WHERE pro.id = ?1",nativeQuery = true)
    Page<SubTaskEntity> findAllByProjectId(Long id,Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE `tbl_sub_tasks` sub\n" +
            "JOIN tbl_bugs bug ON sub.id = bug.sub_task_id\n" +
            "SET sub.status = 'DONE' \n" +
            "WHERE sub.id = ?1 \n" +
            "AND 'TO_DO' NOT IN (\n" +
            "\tSELECT `status` FROM tbl_bugs\n" +
            "\tWHERE sub_task_id = ?1\n" +
            ")\n" +
            "AND'IN_PROGRESS' NOT IN (\n" +
            "\tSELECT `status` FROM tbl_bugs\n" +
            "\tWHERE sub_task_id = ?1\n" +
            ")\n" +
            "AND'IN_REVIEW' NOT IN (\n" +
            "\tSELECT `status` FROM tbl_bugs\n" +
            "\tWHERE sub_task_id = ?1\n" +
            ")\n" +
            "AND'FIX_BUG' NOT IN (\n" +
            "\tSELECT `status` FROM tbl_bugs\n" +
            "\tWHERE sub_task_id = ?1\n" +
            ")",nativeQuery = true)
    void updateStatusSubTaskAfterAllBugDone(Long id);
}
