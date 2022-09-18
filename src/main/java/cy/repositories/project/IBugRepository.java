package cy.repositories.project;

import cy.entities.project.BugEntity;
import cy.entities.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface IBugRepository extends JpaRepository<BugEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "select * from tbl_bugs where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 5 MINUTE)))",nativeQuery = true)
    List<BugEntity> checkBugDelete();
}
