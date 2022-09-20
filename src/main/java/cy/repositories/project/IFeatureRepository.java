package cy.repositories.project;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IFeatureRepository extends JpaRepository<FeatureEntity,Long>, JpaSpecificationExecutor<FeatureEntity> {

    Page<FeatureEntity> findAll(Specification<FeatureEntity> specification, Pageable pageable);

}
