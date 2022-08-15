package cy.repositories;

import cy.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {
    RoleEntity findRoleEntityByRoleName(String roleName);

    Set<RoleEntity> findAllByRoleIdIn(List<Long> roleIds);
}
