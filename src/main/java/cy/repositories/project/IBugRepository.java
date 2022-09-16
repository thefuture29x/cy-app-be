package cy.repositories.project;

import cy.entities.project.BugEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBugRepository extends JpaRepository<BugEntity, Long> {

}
