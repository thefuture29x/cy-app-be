package cy.services.project;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import cy.models.project.FeatureModel;
import cy.services.IBaseService;

public interface IFeatureService extends IBaseService<FeatureEntity, FeatureDto, FeatureModel,Long> {
    boolean changIsDeleteById(Long id);
}
