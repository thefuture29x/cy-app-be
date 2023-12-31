package cy.services.project;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IFeatureService extends IBaseService<FeatureEntity, FeatureDto, FeatureModel,Long> {
    boolean changIsDeleteById(Long id);
    boolean updateStatusFeature(Long id,String status);

    Page<FeatureDto> findAllByProjectId (Long id, Pageable pageable);
    Page<FeatureDto> findByPage(FeatureFilterModel featureFilterModel, Pageable pageable);

}
