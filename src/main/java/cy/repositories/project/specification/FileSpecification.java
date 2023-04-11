package cy.repositories.project.specification;

import cy.entities.common.FileEntity;
import cy.entities.common.FileEntity_;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FileSpecification {

    public static Specification<FileEntity> byIdIn(List<Long> ids) {
        return (root, query, cb) -> root.get(FileEntity_.ID).in(ids);
    }

    public static Specification<FileEntity> byCategory(Const.tableName category) {
        return (root, query, cb) -> cb.equal(root.get(FileEntity_.CATEGORY), category.name());
    }
}
