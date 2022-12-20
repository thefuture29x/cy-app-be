package cy.services.project;

import cy.dtos.project.DataSearchTag;
import cy.dtos.project.TagRelationDto;
import cy.entities.project.TagRelationEntity;
import cy.models.project.TagRelationModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITagRelationService extends IBaseService<TagRelationEntity, TagRelationDto, TagRelationModel, Long> {
    List<TagRelationEntity> findTagByCategoryAndObject(String category, Long objectId);
    Page<DataSearchTag> findAllByTag(String name, Pageable pageable);
}
