package cy.repositories.project;

import cy.entities.project.BugEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IBugRepository extends JpaRepository<BugEntity, Long> {
    @Query(value = "SELECT bg.* FROM tbl_bugs bg \n" +
            "JOIN tbl_sub_tasks sts ON bg.sub_task_id = sts.id \n" +
            "JOIN tbl_tasks ts ON sts.task_id = ts.id \n" +
            "JOIN tbl_features ft ON ts.feature_id = ft.id \n" +
            "JOIN tbl_projects pr ON ft.project_id = pr.id \n" +
            "WHERE pr.id = ?1",nativeQuery = true)
    Page<BugEntity> findAllBugOfProject(Long idProject, Pageable pageable);

}
