package cy.services.attendance;

import cy.dtos.attendance.OptionDto;
import cy.entities.attendance.OptionEntity;
import cy.services.IBaseService;

import java.util.List;

public interface IOptionService extends IBaseService<OptionEntity, OptionDto, OptionDto, Long> {
    OptionDto findByKey(String key);

    List<OptionDto> findAllByKeys(List<String> keys);
}
