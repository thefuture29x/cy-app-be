package cy.repositories.project;

import cy.entities.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProjectRepository  extends JpaRepository<ProjectEntity, Long> {
}
