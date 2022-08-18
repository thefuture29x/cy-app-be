package cy.repositories;

import cy.entities.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IOptionRepository extends JpaRepository<OptionEntity, Long> {
    Optional<OptionEntity> findByOptionKey(String optionKey);


    List<OptionEntity>findAllByOptionKeyIn(List<String> keys);
}
