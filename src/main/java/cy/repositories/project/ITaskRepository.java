package cy.repositories.project;

import cy.entities.project.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT tas.* FROM `tbl_projects` pro \n" +
            "JOIN `tbl_features` fea ON pro.id = fea.project_id\n" +
            "JOIN `tbl_tasks` tas ON fea.id = tas.feature_id\n" +
            "WHERE pro.id = ?1",nativeQuery = true)
    Page<TaskEntity> findAllByProjectId(Long id, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbl_tasks SET `status` = ?2 WHERE id = ?1",nativeQuery = true)
    void updateStatusTask(Long id,String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE `tbl_tasks` tas \n" +
            "JOIN `tbl_bugs` bug ON tas.id = bug.task_id\n" +
            "SET tas.status = 'DONE' \n" +
            "WHERE tas.id = ?1 \n" +
            "AND 'TO_DO' NOT IN (\n" +
            "\tSELECT `status` FROM `tbl_bugs`\n" +
            "\tWHERE task_id = ?1\n" +
            ")\n" +
            "AND'IN_PROGRESS' NOT IN (\n" +
            "\tSELECT `status` FROM `tbl_bugs`\n" +
            "\tWHERE task_id = ?1\n" +
            ")\n" +
            "AND'IN_REVIEW' NOT IN (\n" +
            "\tSELECT `status` FROM `tbl_bugs`\n" +
            "\tWHERE task_id = ?1\n" +
            ")\n" +
            "AND'FIX_BUG' NOT IN (\n" +
            "\tSELECT `status` FROM `tbl_bugs`\n" +
            "\tWHERE task_id = ?1 \n" +
            ")",nativeQuery = true)
    void updateStatusTaskAfterAllBugDone(Long id);
}
