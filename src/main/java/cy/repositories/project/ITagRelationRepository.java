package cy.repositories.project;

import cy.entities.project.TagEntity;
import cy.entities.project.TagRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ITagRelationRepository extends JpaRepository<TagRelationEntity,Long> {
    @Query("select tr from TagRelationEntity tr where tr.category LIKE ?1 and tr.objectId = ?2")
    List<TagRelationEntity> getByCategoryAndObjectId(String category, Long objectId);

    @Query(value = "select * from tbl_tag_relations where category LIKE ?1 and object_id = ?2 and tag_id = ?3", nativeQuery = true)
    List<TagRelationEntity> getByCategoryAndObjectIdAAndIdTag(String category, Long objectId, Long tagId);
}
