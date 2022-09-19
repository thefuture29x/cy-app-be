package cy.repositories.project;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface IFeatureRepository extends JpaRepository<FeatureEntity,Long>, JpaSpecificationExecutor<FeatureEntity> {
    @Modifying
    @Transactional
    @Query(value = "select * from tbl_features where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))",nativeQuery = true)
    List<FeatureEntity> checkFeatureDelete();


    @Query(value = "select * from tbl_features where project_id = ?1", nativeQuery = true)
    List<FeatureEntity> findByProjectId(Long id);

    Page<FeatureEntity> findAll(Specification<FeatureEntity> specification, Pageable pageable);


}
