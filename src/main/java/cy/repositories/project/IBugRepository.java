package cy.repositories.project;

import cy.entities.project.BugEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IBugRepository extends JpaRepository<BugEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "select * from tbl_bugs where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))", nativeQuery = true)
    List<BugEntity> checkBugDelete();

    @Query(value = "SELECT * FROM `tbl_bugs` \n"
            + "WHERE id IN (\n"
            + "\tSELECT bg.id FROM `tbl_bugs` bg  \n"
            + "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id \n"
            + "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n"
            + "\tJOIN `tbl_projects` pr ON ft.project_id = pr.id \n"
            + "\tWHERE pr.id = ?1 AND bg.is_deleted=0 AND ts.is_deleted = 0 AND ft.is_deleted = 0\n"
            + "\t)\n"
            + "OR id IN (\n"
            + "\tSELECT bg.id FROM `tbl_bugs` bg  \n"
            + "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id\n"
            + "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id \n"
            + "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n"
            + "\tJOIN `tbl_projects` pr ON ft.project_id = pr.id \n"
            + "\tWHERE pr.id = ?1 AND bg.is_deleted=0 AND ts.is_deleted = 0 AND sts.is_deleted = 0 AND ft.is_deleted = 0\n"
            + ")\n", nativeQuery = true)
    Page<BugEntity> findAllBugOfProject(Long idProject, Pageable pageable);
    @Query(value = "SELECT DISTINCT * FROM `tbl_bugs`\n" +
            "WHERE id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tWHERE ft.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ") \n" +
            "OR id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id \n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tWHERE ft.id = ?1  AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND sts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ")", nativeQuery = true)
    Page<BugEntity> findAllBugOfFeature(Long idFeature, Pageable pageable);

    @Query(value = "SELECT DISTINCT * FROM `tbl_bugs`\n" +
            "WHERE id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id\n" +
            "\tWHERE ts.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 \n" +
            ") \n" +
            "OR id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id \n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tWHERE ts.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND sts.is_deleted = 0\n" +
            ")",nativeQuery = true)
    Page<BugEntity> findAllByTaskId(Long idTask, Pageable pageable);
    @Query(value = "SELECT * FROM tbl_bugs bg WHERE bg.sub_task_id=?1 AND bg.is_deleted =0",nativeQuery = true)
    Page<BugEntity> findAllBySubTaskId(Long idSubTask, Pageable pageable);
    @Query(value = "select * from tbl_bugs where sub_task_id = ?1 AND is_deleted =0", nativeQuery = true)
    List<BugEntity> getAllBugBySubTaskId(Long subtaskId);

    Integer countAllBySubTask_IdAndIsDeleted(Long id, Boolean isDelete);

    @Query(value = "SELECT COUNT(DISTINCT id) FROM `tbl_bugs` \n" +
            "WHERE id IN (\n" +
            "SELECT bg.id FROM `tbl_bugs` bg  \n" +
            "JOIN `tbl_tasks` ts ON bg.task_id = ts.id \n" +
            "JOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "JOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "WHERE pr.id = ?1 AND bg.is_deleted=0 AND ts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ")\n" +
            "OR id IN (\n" +
            "SELECT bg.id FROM `tbl_bugs` bg  \n" +
            "JOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id\n" +
            "JOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "JOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "JOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "WHERE pr.id = ?1 AND bg.is_deleted= 0 AND ts.is_deleted = 0 AND sts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ")",nativeQuery = true)
    Integer countAllBugOfProjectByProjectId(Long id);
    @Query(value = "SELECT COUNT(DISTINCT id) FROM `tbl_bugs`\n" +
            "WHERE id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tWHERE ft.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ") \n" +
            "OR id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id \n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tJOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "\tWHERE ft.id = ?1  AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND sts.is_deleted = 0 AND ft.is_deleted = 0\n" +
            ")",nativeQuery = true)
    Integer countAllBugOfFeatureByFeatureId(Long id);


    @Query(value = "SELECT COUNT(DISTINCT id) FROM `tbl_bugs`\n" +
            "WHERE id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_tasks` ts ON bg.task_id = ts.id\n" +
            "\tWHERE ts.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 \n" +
            ") \n" +
            "OR id IN (\n" +
            "\tSELECT bg.id FROM `tbl_bugs` bg \n" +
            "\tJOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id \n" +
            "\tJOIN `tbl_tasks` ts ON sts.task_id = ts.id\n" +
            "\tWHERE ts.id = ?1 AND bg.is_deleted = 0 AND ts.is_deleted = 0 AND sts.is_deleted = 0\n" +
            ")",nativeQuery = true)
    Integer countAllBugOfTaskByTaskId(Long id);


}
