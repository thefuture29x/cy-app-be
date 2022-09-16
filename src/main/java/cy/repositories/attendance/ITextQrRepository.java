package cy.repositories.attendance;

import cy.entities.attendance.TextQrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITextQrRepository extends JpaRepository<TextQrEntity, Long> {
}
