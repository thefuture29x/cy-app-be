package cy.services.attendance;

import cy.dtos.attendance.TextQrDto;
import cy.entities.attendance.TextQrEntity;
import cy.models.attendance.TextQrModel;
import cy.services.common.IBaseService;

public interface ITextQrService extends IBaseService<TextQrEntity, TextQrDto, TextQrModel, Long> {
}
