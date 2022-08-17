package cy.repositories;

import cy.entities.RequestDayOffEntity;
import cy.entities.RequestDeviceEntity;
import cy.entities.RequestModifiEntity;
import cy.resources.RequestDayOffResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRequestDayOffRepository extends JpaRepository<RequestDayOffEntity,Long> {
    @Query("select r from RequestDayOffEntity r order by r.dateDayOff desc ")
    Page<RequestDayOffEntity> findBypage(Pageable page);

    @Query(value = "SELECT * FROM `tbl_request_dayoff` \n " +
            "WHERE user_id = ?1 \n " +
            "and created_date between ?2 and ?3", nativeQuery = true)
    Page<RequestDayOffEntity> getAllRequestSendMe(Long id, String startTime, String endTime,Pageable pageable);

    @Query(value = "SELECT * FROM `tbl_request_dayoff` \n " +
            "WHERE assign_id = ?1", nativeQuery = true)
    Page<RequestDayOffEntity> getAllRequestCreateByMe(Long id, Pageable pageable);

}
