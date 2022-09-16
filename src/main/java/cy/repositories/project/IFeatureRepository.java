package cy.repositories.project;

import cy.entities.project.FeatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IFeatureRepository extends JpaRepository<FeatureEntity,Long>, JpaSpecificationExecutor<FeatureEntity> {

}
