package cy.services;

import cy.dtos.TagDto;
import cy.dtos.UserDto;
import cy.entities.UserEntity;
import cy.entities.project.TagEntity;
import cy.models.TagModel;
import cy.models.UserModel;

public interface ITagService extends IBaseService<TagEntity, TagDto, TagModel, Long>{
}
