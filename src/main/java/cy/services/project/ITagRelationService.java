package cy.services.project;

import cy.dtos.TagDto;
import cy.dtos.project.TagRelationDto;
import cy.entities.project.TagEntity;
import cy.entities.project.TagRelationEntity;
import cy.models.project.TagModel;
import cy.models.project.TagRelationModel;
import cy.services.IBaseService;

public interface ITagRelationService extends IBaseService<TagRelationEntity, TagRelationDto, TagRelationModel, Long> {
}
