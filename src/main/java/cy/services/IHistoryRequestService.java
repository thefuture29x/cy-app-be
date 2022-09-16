package cy.services;


import cy.dtos.attendance.HistoryRequestDto;

import cy.entities.attendance.HistoryRequestEntity;

import cy.models.attendance.HistoryRequestModel;

public interface IHistoryRequestService extends IBaseService<HistoryRequestEntity,HistoryRequestDto,HistoryRequestModel,Long>{
    HistoryRequestDto saveOrUpdate(HistoryRequestModel historyRequestModel);
    HistoryRequestDto findById(Long id);
}
