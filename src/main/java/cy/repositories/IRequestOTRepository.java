package cy.repositories;

import cy.entities.RequestOTEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IRequestOTRepository extends JpaRepository<RequestOTEntity, Long>, JpaSpecificationExecutor<RequestOTEntity> {
}
