package cy.repositories;

import cy.entities.RequestDeviceEntity;
import cy.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IRequestDeviceRepository extends JpaRepository<RequestDeviceEntity, Long>, JpaSpecificationExecutor<RequestDeviceEntity> {
}
