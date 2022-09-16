package cy.services.project;

import cy.dtos.project.HistoryLogDto;
import cy.entities.UserEntity;
import cy.entities.project.HistoryEntity;
import cy.services.IBaseService;
import cy.utils.Const;

import java.util.List;

public interface IHistoryLogService extends IBaseService<HistoryEntity, HistoryLogDto, HistoryEntity, Long> {

    void logChangedTeamInProject(Long objectId, Object o, List<List<UserEntity>> oL, List<List<UserEntity>> nList, Const.tableName category);

    void logCreate(Long objectId, Object object, Const.tableName category);

    boolean logUpdate(Long objectId, Object original, Object newObj, Const.tableName category);

    void logDelete(Long objectId, Object object, Const.tableName category);

    void log(Long objectId, String content, Const.tableName category);
}
