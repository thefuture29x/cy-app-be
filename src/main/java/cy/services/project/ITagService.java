package cy.services.project;

import cy.dtos.TagDto;
import cy.entities.project.TagEntity;
import cy.models.project.TagModel;
import cy.services.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITagService extends IBaseService<TagEntity, TagDto, TagModel, Long> {

    Page<TagDto> findPageByName(Pageable pageable, String search);
}
