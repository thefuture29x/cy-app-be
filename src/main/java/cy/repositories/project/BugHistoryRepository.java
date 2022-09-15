package cy.repositories.project;

import cy.entities.project.BugHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugHistoryRepository extends JpaRepository<BugHistoryEntity, Long> {

}
