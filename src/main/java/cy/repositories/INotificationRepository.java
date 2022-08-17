package cy.repositories;

import cy.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Page<NotificationEntity> findAllByUserId(Long userId, Pageable pageable);
}
