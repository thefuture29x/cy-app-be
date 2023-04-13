package cy.repositories.mission;

import cy.entities.mission.AssignEntity;
import cy.entities.mission.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAssignRepository extends JpaRepository<AssignEntity, Long> {
    List<AssignEntity> getAllByNameAndIsDeleted(String name, Boolean isDeleted);
}
