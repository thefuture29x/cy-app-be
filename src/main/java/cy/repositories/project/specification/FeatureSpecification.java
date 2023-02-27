package cy.repositories.project.specification;

import cy.entities.UserEntity;
import cy.entities.UserEntity_;
import cy.entities.project.*;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.utils.Const;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.domain.Specification;
import org.yaml.snakeyaml.util.EnumUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.*;
import javax.swing.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.springframework.data.jpa.domain.Specification.*;

public class FeatureSpecification {

    public static Specification<FeatureEntity> byName(String name) {
        return (root, query, cb) -> cb.like(root.get(FeatureEntity_.NAME), "%" + name + "%");
    }

    public static Specification<FeatureEntity> byHashTag(String hashTag) {
        return (root, query, cb) -> {
            Join<TagRelationEntity, FeatureEntity> table2Table1Join = root.join(TagRelationEntity_.OBJECT_ID, JoinType.INNER);
            Join<TagRelationEntity, TagEntity> table2Table3Join = root.join(TagRelationEntity_.ID_TAG, JoinType.INNER)
                    .join(TagEntity_.ID, JoinType.INNER);

            return cb.and(
                    cb.equal(table2Table3Join.get("condition"), hashTag)
            );
        };
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
        if(eStatus == null){
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.ID),0);
        }
            return (root, query, cb) -> cb.equal(root.get(FeatureEntity_.STATUS.toLowerCase()), eStatus.toLowerCase());
    }
    public static Specification<FeatureEntity> byPriority(String ePriority) {
        return (root, query, cb) -> cb.equal(root.get(FeatureEntity_.PRIORITY.toLowerCase()), ePriority.toLowerCase());
    }
    public static Specification<FeatureEntity> byCreatorName(String name){
        return ((root, query, cb) -> {
            Join<FeatureEntity, UserEntity> createByRoot =root.join(FeatureEntity_.CREATE_BY);
            return cb.or(cb.like(createByRoot.get(UserEntity_.FULL_NAME), "%" + name + "%"));
        });
    }
    public static Specification<FeatureEntity> byFeatureDate(String minDate, String maxDate){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.START_DATE), convertDate(minDate+".000")),criteriaBuilder.lessThanOrEqualTo(root.get(FeatureEntity_.END_DATE), convertDate(maxDate+".000"))));
    }

    public static Specification<FeatureEntity> byFeatureStartAndEndDate(String minDate, String maxDate){
        return ((root, query, criteriaBuilder) -> {
            if (minDate != null ) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.START_DATE), convertDate(minDate+".000"));
            }
            else if(maxDate !=null){
                return criteriaBuilder.greaterThanOrEqualTo(root.get(FeatureEntity_.END_DATE), convertDate(maxDate+".000"));
            }else
                return null;
        });
    }

    public static Specification<FeatureEntity> filterAndSearch(FeatureFilterModel filterModel) {
        List<Specification<FeatureEntity>> specificationList = new ArrayList<>();
        Specification<FeatureEntity> finalSpecs = null;
        Specification<FeatureEntity> firstSpecs = null;
        if (filterModel.getProjectId() != null){
            firstSpecs = byProjectId(filterModel.getProjectId());
        }
        String status = filterModel.getStatus()==null? null : filterModel.getStatus().name();
        if(firstSpecs!=null){
            firstSpecs = firstSpecs.and(byStatus(status));
        }else {
            firstSpecs = byStatus(status);
        }
        if (filterModel.getSearchField() != null) {
            if (filterModel.getSearchField().charAt(0) == '#'){
                specificationList.add(byHashTag(filterModel.getSearchField()));
//            specificationList.add(byDescription(filterModel.getSearchField()));
//            specificationList.add(byCreatorName(filterModel.getSearchField()));
            }else {
                specificationList.add(byName(filterModel.getSearchField()));
            }
        }
        if(filterModel.getMaxDate()!= null && filterModel.getMinDate()!=null){
            specificationList.add(byFeatureDate(filterModel.getMinDate(),filterModel.getMaxDate()));
        }else {
            specificationList.add(byFeatureStartAndEndDate(filterModel.getMinDate(),filterModel.getMaxDate()));
        }
        for (Specification<FeatureEntity> spec : specificationList) {
            if(finalSpecs == null) {
                finalSpecs = spec;
            } else {
                finalSpecs = finalSpecs.and(spec);
            }
        }
        if(firstSpecs!=null){
            return firstSpecs.and(finalSpecs);
        }
        return finalSpecs;
    }

    public static java.sql.Timestamp convertDate(String date) {
        java.sql.Timestamp result = null;
        SimpleDateFormat localeIta = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            Date parsedDate = localeIta.parse(date);
            result = new Timestamp(parsedDate.getTime());
//            result.setHours(0);
//            result.setMinutes(0);
//            result.setSeconds(0);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

}
