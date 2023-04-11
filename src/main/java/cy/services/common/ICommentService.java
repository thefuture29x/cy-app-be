package cy.services.common;


import cy.dtos.common.CommentDto;
import cy.entities.common.CommentEntity;
import cy.models.common.CommentModel;
import cy.services.common.IBaseService;
import cy.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ICommentService extends IBaseService<CommentEntity, CommentDto, CommentModel, Long> {

    Page<CommentDto> findAllByCategoryAndObjectId(Pageable pageable, Const.tableName category, Long objectId);

    List<CommentDto> findAllChildByParentId(Pageable pageable, Long idParent);
}
