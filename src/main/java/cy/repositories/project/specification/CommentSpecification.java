package cy.repositories.project.specification;

import cy.entities.project.CommentEntity;
import cy.entities.project.CommentEntity_;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {

    public static Specification<CommentEntity> byCategory(Const.tableName category) {
        return (root, query, cb) -> cb.equal(root.get(CommentEntity_.CATEGORY), category.name());
    }

    public static Specification<CommentEntity> byObjectId(Long objectId) {
        return (root, query, cb) -> cb.equal(root.get(CommentEntity_.OBJECT_ID), objectId);
    }

    public static Specification<CommentEntity> byParentId(Long parentId) {
        return (root, query, cb) -> parentId == null ? cb.isNull(root.get(CommentEntity_.ID_PARENT)) :cb.equal(root.get(CommentEntity_.ID_PARENT), parentId);
    }

    public static Specification<CommentEntity> byCategoryAndObjectId(Const.tableName category, Long objectId) {
        return Specification.where(byCategory(category)).and(byObjectId(objectId));
    }


}
