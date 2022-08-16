package cy.repositories;

import cy.entities.RequestDayOffEntity;
import cy.resources.RequestDayOffResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRequestDayOffRepository extends JpaRepository<RequestDayOffEntity,Long> {
    @Query("select r from RequestDayOffEntity r order by r.dateDayOff desc ")
    Page<RequestDayOffEntity> findBypage(Pageable page);
}
