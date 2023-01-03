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
    @Query(value = "select * from tbl_bugs where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<BugEntity> checkBugDelete();

    @Query(value = "SELECT bg.* FROM `tbl_bugs` bg \n" +
            "JOIN `tbl_sub_tasks` sts ON bg.sub_task_id = sts.id \n" +
            "JOIN `tbl_tasks` ts ON sts.task_id = ts.id \n" +
            "JOIN `tbl_features` ft ON ts.feature_id = ft.id \n" +
            "JOIN `tbl_projects` pr ON ft.project_id = pr.id \n" +
            "WHERE pr.id = ?1 AND bg.is_deleted=0 \n" +
            "ORDER BY bg.updated_date DESC",nativeQuery = true)
    Page<BugEntity> findAllBugOfProject(Long idProject, Pageable pageable);
    @Query(value = "select * from tbl_bugs where sub_task_id = ?1", nativeQuery = true)
    List<BugEntity> getAllBugBySubTaskId(Long subtaskId);
}
