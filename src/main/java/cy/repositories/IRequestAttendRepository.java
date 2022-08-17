package cy.repositories;

import cy.entities.RequestAttendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.Date;

public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long> {
    // check if request is exist follow day and user
    @Query(value = "SELECT * FROM tbl_request_attend WHERE date_request_attend = ?1 AND user_id = ?2", nativeQuery = true)
    List<RequestAttendEntity> findByDayAndUser(String day, Long userId);
    @Query("select r from RequestAttendEntity r where r.dateRequestAttend = ?1 and r.createBy.userId = ?2")
    RequestAttendEntity checkAttend(Date date,Long id);
}
