package cy.services.project;

import cy.dtos.project.HistoryLogDto;
import cy.entities.UserEntity;
import cy.entities.project.HistoryEntity;
import cy.services.IBaseService;
import cy.utils.Const;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IHistoryLogService extends IBaseService<HistoryEntity, HistoryLogDto, HistoryEntity, Long> {

    void logChangedTeamInProject(Long objectId, Object o, List<List<UserEntity>> oL, List<List<UserEntity>> nList, Const.tableName category);

    void logCreate(Long objectId, Object object, Const.tableName category, String nameObject);

    boolean logUpdate(Long objectId, Object original, Object newObj, Const.tableName category);

    void logDelete(Long objectId, Object object, Const.tableName category);

    void log(Long objectId, String content, Const.tableName category);

    Page<HistoryLogDto> getAllHistoryCreateObject(Const.tableName category,Pageable pageable);
    Page<HistoryLogDto> getAllHistoryOfBug(Long idProject,Pageable pageable);


}
