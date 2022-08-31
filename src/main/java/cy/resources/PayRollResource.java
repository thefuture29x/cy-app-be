package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.PayRollDto;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.services.IPayRollService;
import cy.services.IUserService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "payroll")
public class PayRollResource {
    @Autowired
    IUserService iUserService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN})
    @GetMapping("/work-date-of-month")
    public ResponseDto calculateDate(Pageable pageable, @RequestParam(value = "startMonth") String startMonth, @RequestParam(value = "startYear") String startYear) {
        List<PayRollDto> payRollDtos = iUserService.calculatePayRoll(pageable,Integer.parseInt(startMonth), Integer.parseInt(startYear));
        Page<PayRollDto> pages = new PageImpl<PayRollDto>(payRollDtos, pageable, payRollDtos.size());
        return ResponseDto.of(pages);
    }

}
