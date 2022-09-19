package cy.repositories.project;

import cy.entities.project.BugEntity;
import cy.entities.project.BugHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBugHistoryRepository extends JpaRepository<BugHistoryEntity, Long> {
List<BugHistoryEntity> findAllByBugId(BugEntity bugId);
}
