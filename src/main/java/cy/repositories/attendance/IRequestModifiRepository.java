package cy.repositories.attendance;

import cy.entities.attendance.RequestModifiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IRequestModifiRepository extends JpaRepository<RequestModifiEntity,Long> {
    @Query(value = "SELECT * FROM `tbl_request_modifi` \n " +
            "WHERE assign_id = ?1 \n " +
            "and created_date between ?2 and ?3 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestModifiEntity> getAllRequestSendMe(Long id, String startTime, String endTime, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_modifi` \n " +
            "WHERE user_id = ?1 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestModifiEntity> getAllRequestCreateByMe(Long id, Pageable pageable);
}
