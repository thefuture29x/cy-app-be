package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.services.IPayRollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "payroll")
public class PayRollResource {
    @Autowired
    IPayRollService iPayRollService;

    @GetMapping("/get-pay-roll-by-month-and-year/{month}/{year}")
    public ResponseDto getPayRollByMonth(@PathVariable(value = "month") int month, @PathVariable(value = "year") int year, Pageable pageable){
        return ResponseDto.of(iPayRollService.getPayRollByMonthAndYear(month, year, pageable));
    }


}
