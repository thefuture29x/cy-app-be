package cy.repositories;

import cy.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<NotificationEntity, Long> {
    @Query(value = "SELECT * FROM tbl_notification WHERE user_id = ?1", nativeQuery = true)
    Page<NotificationEntity> findAllByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM tbl_notification WHERE user_id = ?1", nativeQuery = true)
    List<NotificationEntity> findAllByUserId(Long userId);

    @Query(value = "SELECT * FROM tbl_notification WHERE user_id = ?1 AND (is_read = 0 OR is_read is null) ORDER BY date_noti ASC", nativeQuery = true)
    Page<NotificationEntity> findAllByUserIdNotRead(Long userId, Pageable pageable);
}
