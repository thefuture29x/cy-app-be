package cy.repositories;

import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;


public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long> {
    @Query(value = "SELECT r FROM RequestAttendEntity r WHERE r.createBy.userId = ?1 AND r.dateRequestAttend = ?2")
    RequestAttendEntity userAlreadyRequest(Long user_id, Date date);
}
