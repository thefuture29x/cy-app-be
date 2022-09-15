package cy.services.project;

import cy.dtos.TagDto;
import cy.entities.project.TagEntity;
import cy.models.project.TagModel;
import cy.services.IBaseService;

public interface ITagService extends IBaseService<TagEntity, TagDto, TagModel, Long> {
}
