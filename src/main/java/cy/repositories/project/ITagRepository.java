package cy.repositories.project;

import cy.entities.project.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITagRepository extends JpaRepository<TagEntity,Long> {
    @Query("select t from TagEntity t where t.name = ?1")
    TagEntity findByName(String name);


    @Query("select t from TagEntity t ")
    Page<TagEntity> findAllByPage(Pageable pageable);


    @Query("select t from TagEntity t where t.name like ?1%")
    Page<TagEntity> findPageByName(String search,Pageable pageable);

    @Query(value = "SELECT tg.* FROM tbl_tag_relations tgrl \n" +
            "JOIN tbl_tags tg ON tgrl.tag_id = tg.id\n" +
            "WHERE tgrl.object_id = ?1 AND tgrl.category = ?2",nativeQuery = true)
    List<TagEntity> getAllByObjectIdAndCategory(Long objectId,String category);
}
