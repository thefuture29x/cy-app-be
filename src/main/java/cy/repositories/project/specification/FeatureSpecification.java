package cy.repositories.project.specification;

import cy.entities.UserEntity;
import cy.entities.UserEntity_;
import cy.entities.project.FeatureEntity;
import cy.entities.project.FeatureEntity_;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.utils.Const;
import org.springframework.data.jpa.domain.Specification;
import org.yaml.snakeyaml.util.EnumUtils;

import javax.persistence.criteria.Join;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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
    public static Specification<FeatureEntity> byCreatorName(String name){
        return ((root, query, cb) -> {
            Join<FeatureEntity, UserEntity> createByRoot =root.join(FeatureEntity_.CREATE_BY);
            return createByRoot.on(cb.like(createByRoot.get(UserEntity_.FULL_NAME), "%" + name + "%"))
                    .getOn();
        });
    }
    public static Specification<FeatureEntity> byFeatureDate(Date minDate, Date maxDate){
        return ((root, query, criteriaBuilder) -> {
            if (maxDate != null) {
                Instant instant = maxDate.toInstant();
                instant = instant.plus(1, ChronoUnit.DAYS);
                Instant maxInstant = instant;
                if (minDate != null)
                    return criteriaBuilder.between(root.get(FeatureEntity_.START_DATE), minDate, maxDate);
                else
                    return criteriaBuilder.lessThanOrEqualTo(root.get(FeatureEntity_.END_DATE), Date.from(maxInstant));
            } else if (minDate != null) {
                Instant minstant = minDate.toInstant();
                return criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.START_DATE),Date.from(minstant));
            } else {
                return null;
            }
        });
    }

    public static Specification<FeatureEntity> filterAndSearch(FeatureFilterModel filterModel) {
        List<Specification<FeatureEntity>> specificationList = new ArrayList<>();
        Specification<FeatureEntity> finalSpecs = null;
        if (filterModel.getSearchField() != null) {
            specificationList.add(byName(filterModel.getSearchField()));
            specificationList.add(byDescription(filterModel.getSearchField()));
            specificationList.add(byCreatorName(filterModel.getSearchField()));
        }
        if(filterModel.getMaxDate()!= null || filterModel.getMinDate()!=null){
            specificationList.add(byFeatureDate(filterModel.getMinDate(),filterModel.getMaxDate()));
        }
        for (Specification<FeatureEntity> spec : specificationList) {
            if(finalSpecs == null) {
                finalSpecs = spec;
            } else {
                finalSpecs = finalSpecs.or(spec);
            }
        }
        return finalSpecs;
    }

}
