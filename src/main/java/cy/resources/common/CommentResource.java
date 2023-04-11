package cy.resources.common;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.common.CommentModel;
import cy.repositories.project.specification.CommentSpecification;
import cy.services.common.ICommentService;
import cy.utils.Const;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "comment")
public class CommentResource {


    private final ICommentService commentService;

    public CommentResource(ICommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("{object_id}")
    public ResponseDto getComments(@PathVariable(name = "object_id") Long objectId, @RequestParam Const.tableName category, Pageable page) {
        return ResponseDto.of(this.commentService.findAllByCategoryAndObjectId(page, category, objectId));
    }

    @GetMapping("get_by_parent/{parent_id}")
    public ResponseDto getCommentsByParent(@PathVariable(name = "parent_id") Long parentId, Pageable page) {
        return ResponseDto.of(this.commentService.filter(page, Specification.where(CommentSpecification.byParentId(parentId))));
    }

    @PostMapping
    public ResponseDto addComment(CommentModel commentModel) {
        commentModel.setId(null);
        return ResponseDto.of(this.commentService.add(commentModel));
    }

    @PutMapping("{id}")
    public ResponseDto updateComment(@PathVariable(name = "id") Long id, CommentModel commentModel) {
        commentModel.setId(id);
        return ResponseDto.of(this.commentService.update(commentModel));
    }

    @DeleteMapping("{id}")
    public ResponseDto deleteComment(@PathVariable Long id) {
        return ResponseDto.of(this.commentService.deleteById(id));
    }
}
