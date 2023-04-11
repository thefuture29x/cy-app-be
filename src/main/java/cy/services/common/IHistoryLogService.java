package cy.services.common;

import cy.dtos.common.HistoryLogDto;
import cy.entities.common.UserEntity;
import cy.entities.common.HistoryEntity;
import cy.services.common.IBaseService;
import cy.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IHistoryLogService extends IBaseService<HistoryEntity, HistoryLogDto, HistoryEntity, Long> {

    void logChangedTeamInProject(Long objectId, Object o, List<List<UserEntity>> oL, List<List<UserEntity>> nList, Const.tableName category);

    void logCreate(Long objectId, Object object, Const.tableName category, String nameObject);

    boolean logUpdate(Long objectId, Object original, Object newObj, Const.tableName category);

    void logDelete(Long objectId, Object object, Const.tableName category, String nameObject);

    void log(Long objectId, String content, Const.tableName category);

    Page<HistoryLogDto> getAllHistoryCreateObject(Const.tableName category,Pageable pageable);
    Page<HistoryLogDto> getAllHistoryOfBug(Long idProject,Pageable pageable);

    Page<HistoryLogDto> getAllHistory(Long idObject, Const.tableName category,Pageable pageable);


}
