package cy.services.project;

import cy.dtos.project.FeatureDto;
import cy.entities.project.FeatureEntity;
import cy.models.project.FeatureModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFeatureService extends IBaseService<FeatureEntity, FeatureDto, FeatureModel,Long> {
    boolean changIsDeleteById(Long id);

    Page<FeatureDto> findAllByProjectId (Long id, Pageable pageable);
}
