package cy.services;

import cy.dtos.OptionDto;
import cy.entities.OptionEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IOptionService extends IBaseService<OptionEntity, OptionDto, OptionDto, Long> {
    OptionDto findByKey(String key);

    List<OptionDto> findAllByKeys(List<String> keys);
}
