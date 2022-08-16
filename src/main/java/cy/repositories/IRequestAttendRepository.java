package cy.repositories;

import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long> {
}
