package cy.repositories.common;

import cy.dtos.project.DataSearchTag;
import cy.entities.common.TagRelationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository

public interface ITagRelationRepository extends JpaRepository<TagRelationEntity, Long> {
    @Query("select tr from TagRelationEntity tr where tr.category = ?1 and tr.objectId = ?2")
    List<TagRelationEntity> getByCategoryAndObjectId(String category, Long objectId);

    @Query("select t.name from TagRelationEntity tr inner join TagEntity t on tr.idTag = t.id where tr.category = ?1 and tr.objectId = ?2")
    List<String> getNameTagByCategoryAndObjectId(String category, Long objectId);

    @Query(value = "select * from tbl_tag_relations where category LIKE ?1 and object_id = ?2 and tag_id = ?3", nativeQuery = true)
    List<TagRelationEntity> getByCategoryAndObjectIdAAndIdTag(String category, Long objectId, Long tagId);

    @Modifying
    @Transactional
    @Query("delete from TagRelationEntity t where t.idTag = ?1")
    void deleteAllByTag(Long id);

    @Query("select t from TagRelationEntity t where t.objectId = ?1 and t.idTag = ?2 and t.category = ?3")
    TagRelationEntity checkIsEmpty(Long idObject, Long idTag, String category);

    @Query("select distinct new cy.dtos.project.DataSearchTag(p.id,p.name,p.createBy.fullName,p.startDate,p.endDate,p.status,t.category) from TagRelationEntity t inner join ProjectEntity p on t.objectId = p.id inner join TagEntity tag on tag.id = t.idTag where tag.name = ?1")
    Page<DataSearchTag> findAllByTag(String id, Pageable pageable);

    @Transactional
    @Query(value = "DELETE FROM tbl_tag_relations WHERE id = ?1", nativeQuery = true)
    void deleteByIdNative(Long id);

    TagRelationEntity findByIdTag(Long id);
}
