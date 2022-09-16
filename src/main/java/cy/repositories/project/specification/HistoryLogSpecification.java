package cy.repositories.project.specification;

import cy.entities.project.HistoryEntity;
import cy.entities.project.HistoryEntity_;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;

public class HistoryLogSpecification {

    public static Specification<HistoryEntity> byObjectId(Long objectId) {
        return (root, query, cb) -> cb.equal(root.get(HistoryEntity_.OBJECT_ID), objectId);
    }

    public static Specification<HistoryEntity> byCategory(Const.tableName category) {
        return (root, query, cb) -> cb.equal(root.get(HistoryEntity_.CATEGORY), category.name());
    }

    public static Specification<HistoryEntity> byObjectAndCategory(Long objectId, Const.tableName category) {
        return Specification.where(byObjectId(objectId)).and(byCategory(category));
    }

}
