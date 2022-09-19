package cy.services.project;


import cy.dtos.project.CommentDto;
import cy.entities.project.CommentEntity;
import cy.models.project.CommentModel;
import cy.services.IBaseService;
import cy.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ICommentService extends IBaseService<CommentEntity, CommentDto, CommentModel, Long> {

    Page<CommentDto> findAllByCategoryAndObjectId(Pageable pageable, Const.tableName category, Long objectId);

    List<CommentDto> findAllChildByParentId(Pageable pageable, Long idParent);
}
