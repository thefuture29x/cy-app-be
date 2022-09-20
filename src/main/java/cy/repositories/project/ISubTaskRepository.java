package cy.repositories.project;

import cy.entities.project.SubTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ISubTaskRepository extends JpaRepository<SubTaskEntity, Long> {
    @Modifying
    @Transactional
    // find record had isDelete = true and timeDelete > 12h
    @Query(value = "select * from tbl_sub_tasks where( is_deleted and (updated_date < DATE_SUB(DATE_ADD(NOW(), INTERVAL 7 HOUR), INTERVAL 12 HOUR)))", nativeQuery = true)
    List<SubTaskEntity> checkSubTasksDelete();

    //    @Query(value = "SELECT s FROM SubTaskEntity s WHERE CONCAT(s.id, '') LIKE CONCAT('%', ?1, '%') OR s.createBy.userName LIKE CONCAT('%', '?1', '%') " +
//            "OR s.createBy.email LIKE ?1 OR s.createBy.fullName LIKE ?1 OR s.startDate = ?1 OR s.endDate = ?1 " +
//            "OR s.status = ?1 OR s.name LIKE ?1 OR s.description LIKE ?1 OR s.priority = ?1 OR s.assignTo.userName = ?1 " +
//            "OR s.assignTo.email LIKE ?1 OR s.assignTo.email LIKE ?1 OR s.assignToTester.userName LIKE ?1 " +
//            "OR s.assignToTester.email LIKE ?1 OR s.assignToTester.fullName LIKE ?1")
    @Query(value = "SELECT s FROM SubTaskEntity s WHERE s.name LIKE CONCAT('%', ?1, '%')")
    Page<SubTaskEntity> findByKeyword(String keyword, Pageable pageable);


    @Query(value = "select * from tbl_sub_tasks where task_id = ?1", nativeQuery = true)
    List<SubTaskEntity> findByTaskId(Long id);
}
