package cy.repositories.project.specification;

import cy.entities.project.HistoryEntity;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;

public class HistoryLogSpecification {

    public static Specification<HistoryEntity> byObjectId(Long objectId) {
        return (root, query, cb) -> cb.equal(root.get("ObjectId"), objectId);
    }

    public static Specification<HistoryEntity> byCategory(Const.tableName category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category.name());
    }

    public static Specification<HistoryEntity> byObjectAndCategory(Long objectId, Const.tableName category) {
        return Specification.where(byObjectId(objectId)).and(byCategory(category));
    }

}
