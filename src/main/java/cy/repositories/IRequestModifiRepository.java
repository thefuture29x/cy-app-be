package cy.repositories;

import cy.entities.RequestModifiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface IRequestModifiRepository extends JpaRepository<RequestModifiEntity,Long> {
    @Query(value = "SELECT * FROM `tbl_request_modifi` \n " +
            "WHERE user_id = ?1 \n " +
            "and created_date between ?2 and ?3", nativeQuery = true)
    List<RequestModifiEntity> getAllRequestSendMe(Long id, String startTime, String endTime);
}
