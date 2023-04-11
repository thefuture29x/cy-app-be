package cy.repositories.mission;

import cy.entities.mission.AssignCheckListEntity;
import cy.entities.mission.AssignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAssignCheckListRepository extends JpaRepository<AssignCheckListEntity, Long> {
}
