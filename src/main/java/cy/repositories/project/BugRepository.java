package cy.repositories.project;

import cy.entities.project.BugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugRepository extends JpaRepository<BugEntity, Long> {

}
