package cy.repositories;

import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long> {
    @Query("select r from RequestAttendEntity r where r.dateRequestAttend = ?1 and r.createBy.userId = ?2")
    RequestAttendEntity checkAttend(Date date,Long id);
}
