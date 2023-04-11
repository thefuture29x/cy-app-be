package cy.services.project;

import cy.dtos.project.BugHistoryDto;
import cy.entities.project.BugHistoryEntity;
import cy.models.project.BugHistoryModel;
import cy.services.common.IBaseService;

public interface IBugHistoryService extends IBaseService<BugHistoryEntity, BugHistoryDto, BugHistoryModel,Long> {}
