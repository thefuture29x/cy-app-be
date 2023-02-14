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

    public static Specification<HistoryEntity> byContent(String category) {
        if (category.equals("PROJECT")){
            return (root, query, cb) -> cb.or(
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã thêm mới project%"),
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã cập nhật project%"),
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã xóa project%"));
        }else {
            return (root, query, cb) -> cb.or(
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã thêm mới bug%"),
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã cập nhật bug%"),
                    cb.like(root.get(HistoryEntity_.CONTENT),"%đã xóa bug%"));
        }
    }
    public static Specification<HistoryEntity> byObjectAndCategory(Long objectId, Const.tableName category) {
        return Specification.where(byObjectId(objectId)).and(byCategory(category));
    }

    public static Specification<HistoryEntity> byCategoryAndContent(Const.tableName category) {
        return Specification.where(byCategory(category)).and(byContent(category.name()));
    }

}
