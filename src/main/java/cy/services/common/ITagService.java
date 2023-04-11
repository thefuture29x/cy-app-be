package cy.services.common;

import cy.dtos.common.TagDto;
import cy.entities.common.TagEntity;
import cy.models.common.TagModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITagService extends IBaseService<TagEntity, TagDto, TagModel, Long> {

    Page<TagDto> findPageByName(Pageable pageable, String search);

    TagEntity addEntity(TagModel tagModel);
}
