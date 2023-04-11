package cy.services.common;

import cy.dtos.project.DataSearchTag;
import cy.dtos.common.TagRelationDto;
import cy.entities.common.TagRelationEntity;
import cy.models.common.TagRelationModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITagRelationService extends IBaseService<TagRelationEntity, TagRelationDto, TagRelationModel, Long> {
    List<TagRelationEntity> findTagByCategoryAndObject(String category, Long objectId);
    Page<DataSearchTag> findAllByTag(String name, Pageable pageable);
}
