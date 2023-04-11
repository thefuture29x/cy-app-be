package cy.repositories.mission;

import cy.entities.mission.MissionEntity;
import cy.entities.project.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface IMissionRepository extends JpaRepository<MissionEntity, Long> {
}
