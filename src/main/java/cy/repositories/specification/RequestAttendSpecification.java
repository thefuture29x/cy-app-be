package cy.repositories.specification;

import cy.entities.RequestAttendEntity;
import cy.entities.RequestAttendEntity_;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class RequestAttendSpecification {

    public static Specification<RequestAttendEntity> byUserId(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(RequestAttendEntity_.CREATE_BY), userId);
    }

    public static Specification<RequestAttendEntity> betweenDateAttendRequest(Date begin, Date end) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(RequestAttendEntity_.DATE_REQUEST_ATTEND), begin, end);
    }

    public static Specification<RequestAttendEntity> byStatus(Integer status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(RequestAttendEntity_.STATUS), status);
    }
}
