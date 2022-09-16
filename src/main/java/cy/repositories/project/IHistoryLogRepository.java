package cy.repositories.project;

import cy.entities.project.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IHistoryLogRepository extends JpaRepository<HistoryEntity, Long>, JpaSpecificationExecutor<HistoryEntity> {
}
