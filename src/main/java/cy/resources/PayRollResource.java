package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.services.IPayRollService;
import cy.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "payroll")
public class PayRollResource {
    @Autowired
    IPayRollService iPayRollService;
    @Autowired
    IUserService iUserService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN})
    @GetMapping("/work-date-of-month")
    public ResponseDto calculateDate(String timeStart, String timeEnd) {
        return ResponseDto.of(iUserService.calculatePayRoll(timeStart, timeEnd));
    }



}
