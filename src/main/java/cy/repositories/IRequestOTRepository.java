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
    @Query(value = "select sum(((TIME_TO_SEC(time_end) - TIME_TO_SEC(time_start)) / 60) / 60)\n" +
                    "from tbl_request_ot\n" +
                    "where user_id = :userId and status = :status and type_ot = :typeOt and date_ot between :startDate and :endDate", nativeQuery = true)
    Float totalOTHours(@Param("userId")Long userId, @Param("status")Integer status, @Param("typeOt")Integer typeOt, @Param("startDate")String startDate, @Param("endDate")String endDate);
}
