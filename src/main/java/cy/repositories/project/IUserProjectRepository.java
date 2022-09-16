package cy.repositories.project;

import cy.entities.project.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserProjectRepository extends JpaRepository<UserProjectEntity, Long> {
}
