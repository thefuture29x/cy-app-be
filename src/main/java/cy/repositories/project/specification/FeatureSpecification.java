package cy.repositories.project.specification;

import cy.entities.project.FeatureEntity;
import cy.entities.project.FeatureEntity_;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.jpa.domain.Specification.*;

public class FeatureSpecification {

    public static Specification<FeatureEntity> byName(String name) {
        return (root, query, cb) -> cb.like(root.get(FeatureEntity_.NAME), "%" + name + "%");
    }

    public static Specification<FeatureEntity> byDescription(String description) {
        return (root, query, cb) -> cb.like(root.get(FeatureEntity_.DESCRIPTION), "%" + description + "%");
    }

    public static Specification<FeatureEntity> byStatus(String eStatus) {
        return (root, query, cb) -> cb.equal(root.get(FeatureEntity_.STATUS.toLowerCase()), eStatus.toLowerCase());
    }
    public static Specification<FeatureEntity> byPriority(String ePriority) {
        return (root, query, cb) -> cb.equal(root.get(FeatureEntity_.PRIORITY.toLowerCase()), ePriority.toLowerCase());
    }

    public static Specification<FeatureEntity> filterAndSearch(FeatureFilterModel filterModel) {
        List<Specification<FeatureEntity>> specificationList = new ArrayList<>();
        Specification<FeatureEntity> finalSpecs = null;
        if (filterModel.getSearchField() != null) {
            specificationList.add(byName(filterModel.getSearchField()));
            specificationList.add(byDescription(filterModel.getSearchField()));
            specificationList.add(byStatus(filterModel.getSearchField()));
            specificationList.add(byPriority(filterModel.getSearchField()));
            for (Specification<FeatureEntity> spec : specificationList) {
                if(finalSpecs == null) {
                    finalSpecs = spec;
                } else {
                    finalSpecs = finalSpecs.or(spec);
                }
            }
        }
        return finalSpecs;
    }
    public static boolean checkEnum(String check, String type) {
        if(type.equals("status")){
            for (Const.status c : Const.status.values()) {
                if (c.name().toLowerCase().equals(check.toLowerCase())) {
                    return true;
                }
            }
        } else if (type.equals("priority")) {
            for (Const.priority c : Const.priority.values()) {
                if (c.name().toLowerCase().equals(check.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
