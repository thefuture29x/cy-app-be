package cy.repositories;

import cy.dtos.RequestAttendDto;
import cy.entities.RequestAttendEntity;
import cy.entities.RequestDayOffEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.Date;

public interface IRequestAttendRepository extends JpaRepository<RequestAttendEntity, Long>, JpaSpecificationExecutor<RequestAttendEntity> {
    @Query(value = "SELECT r FROM RequestAttendEntity r WHERE r.createBy.userId = ?1 AND r.dateRequestAttend = ?2")
    RequestAttendEntity userAlreadyRequest(Long user_id, Date date);

    @Query(value = "SELECT NEW cy.dtos.RequestAttendDto(r) FROM RequestAttendEntity r WHERE r.createBy.userId = ?1")
    List<RequestAttendDto> findByUserId(Long userId);

    // request attend by username and date
    @Query(value = "SELECT * FROM tbl_request_attend ra join tbl_user u on ra.user_id = u.user_id where user_name = ?1 and date_request_attend LIKE ?2 order by date_request_attend DESC", nativeQuery = true)
    List<RequestAttendEntity> findByUserNameAndDate(String name,String monthAndYear);

    @Query(value = "SELECT * FROM tbl_request_attend ra join tbl_user u on ra.user_id = u.user_id where user_name = ?1 and (date_request_attend BETWEEN ?3 AND ?2) order by date_request_attend DESC", nativeQuery = true)
    List<RequestAttendEntity> findByUserNameAndDate_new(String name,String monthCurrent, String monthAgo);

    // check if request is exist follow day and user
    @Query(value = "SELECT * FROM tbl_request_attend WHERE date_request_attend = ?1 AND user_id = ?2", nativeQuery = true)
    List<RequestAttendEntity> findByDayAndUser(String day, Long userId);
    @Query("select r from RequestAttendEntity r where r.dateRequestAttend = ?1 and r.createBy.userId = ?2")
    RequestAttendEntity checkAttend(Date date,Long id);
    @Query(value = "SELECT NEW cy.dtos.RequestAttendDto(r) FROM RequestAttendEntity r WHERE r.id = ?1")
    RequestAttendDto findByIdToDto(Long id);

    @Query(value = "SELECT * FROM tbl_request_attend WHERE date_request_attend LIKE ?1 AND user_id = ?2 order by date_request_attend DESC ", nativeQuery = true)
    List<RequestAttendEntity> findByMonthAndYearAndUser(String monthAndYear, Long userId);
    @Query(value = "SELECT * FROM `tbl_request_attend` \n " +
            "WHERE assign_id = ?1 AND time_check_out is not NULL \n " +
            "and updated_date between ?2 and ?3  ORDER BY updated_date DESC ", nativeQuery = true)
    Page<RequestAttendEntity> getAllRequestSendMe(Long id, String startTime, String endTime, Pageable pageable);
    @Query(value = "SELECT * FROM `tbl_request_attend` \n " +
            "WHERE user_id = ?1 ORDER BY updated_date DESC", nativeQuery = true)
    Page<RequestAttendEntity> getAllRequestCreateByMe(Long id, Pageable pageable);


}
