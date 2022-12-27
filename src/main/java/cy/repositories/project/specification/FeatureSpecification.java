package cy.repositories.project.specification;

import cy.entities.UserEntity;
import cy.entities.UserEntity_;
import cy.entities.project.FeatureEntity;
import cy.entities.project.FeatureEntity_;
import cy.entities.project.ProjectEntity;
import cy.entities.project.ProjectEntity_;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.utils.Const;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.domain.Specification;
import org.yaml.snakeyaml.util.EnumUtils;

import javax.persistence.criteria.Join;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static Specification<FeatureEntity> byProjectId(Long id) {
        return ((root, query, cb) -> {
            Join<FeatureEntity, ProjectEntity> createByRoot =root.join(FeatureEntity_.PROJECT);
            return cb.equal(createByRoot.get(ProjectEntity_.ID),id);
        });
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
    public static Specification<FeatureEntity> byFeatureDate(String minDate, String maxDate){
        return ((root, query, criteriaBuilder) -> {
            if (maxDate != null) {
//                Instant instant = maxDate.toInstant();
//                instant = instant.plus(1, ChronoUnit.DAYS);
//                Instant maxInstant = instant;
//                Timestamp maxTimestamp = new Timestamp(instant.toEpochMilli());
                if (minDate != null){
                    return criteriaBuilder.between(root.get(FeatureEntity_.START_DATE), convertDate(minDate+".000"), convertDate(maxDate+".000"));
                }
                else
                    return criteriaBuilder.lessThanOrEqualTo(root.get(FeatureEntity_.END_DATE), convertDate(maxDate+".000"));
            } else if (minDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.START_DATE), convertDate(minDate+".000"));
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
        if (filterModel.getProjectId() != null){
            specificationList.add(byProjectId(filterModel.getProjectId()));
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

    public static java.sql.Timestamp convertDate(String date) {
        java.sql.Timestamp result = null;
        SimpleDateFormat localeIta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date parsedDate = localeIta.parse(date);
            result = new Timestamp(parsedDate.getTime());
            result.setHours(0);
            result.setMinutes(0);
            result.setSeconds(0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

}
