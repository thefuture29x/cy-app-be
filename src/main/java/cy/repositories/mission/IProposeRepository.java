package cy.repositories.mission;

import cy.entities.mission.MissionEntity;
import cy.entities.mission.ProposeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProposeRepository extends JpaRepository<ProposeEntity, Long> {
    List<ProposeEntity> findAllByCategoryAndObjectId(String category, Long objectId);
}
