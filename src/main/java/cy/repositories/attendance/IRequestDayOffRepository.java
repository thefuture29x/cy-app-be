package cy.repositories.attendance;

import cy.entities.attendance.RequestDayOffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRequestDayOffRepository extends JpaRepository<RequestDayOffEntity,Long> {
    @Query("select r from RequestDayOffEntity r order by r.dateDayOff desc ")
    Page<RequestDayOffEntity> findBypage(Pageable page);

    @Query(value = "SELECT * FROM `tbl_request_dayoff` \n " +
            "WHERE assign_id = ?1 \n " +
            "and created_date between ?2 and ?3 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestDayOffEntity> getAllRequestSendMe(Long id, String startTime, String endTime,Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_dayoff` \n " +
            "WHERE user_id = ?1 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestDayOffEntity> getAllRequestCreateByMe(Long id, Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_dayoff` where user_id = ?1 and date_request_dayoff between ?2 and ?3 and is_legit = ?4 and status = ?5", nativeQuery = true)
    Page<RequestDayOffEntity> getAllDayOfByMonthOfUser(Long id, String DateStart, String DateEnd, boolean isLegit, int status, Pageable pageable);

}
