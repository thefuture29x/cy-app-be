package cy.repositories;

import cy.entities.RequestOTEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRequestOTRepository extends JpaRepository<RequestOTEntity, Long>, JpaSpecificationExecutor<RequestOTEntity> {
    @Query(value = "SELECT * FROM `tbl_request_ot` \n " +
            "WHERE assign_id = ?1 \n " +
            "and created_date between ?2 and ?3 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestOTEntity> getAllRequestSendMe(Long id, String startTime, String endTime,Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_ot` \n " +
            "WHERE user_id = ?1 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestOTEntity> getAllRequestCreateByMe(Long id, Pageable pageable);

    @Query(value = "SELECT * FROM tbl_request_ot WHERE user_id = ?1 AND status = ?2 AND date_ot BETWEEN ?3 AND ?4", nativeQuery = true)
    List<RequestOTEntity> getAllDateOTByMonth(Long userId, Integer status, String startDate, String endDate);
}
