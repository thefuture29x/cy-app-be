package cy.repositories.project;

import cy.entities.project.SubTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISubTaskRepository extends JpaRepository<SubTaskEntity, Long> {
}
