package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.PayRollDto;
import cy.dtos.RequestAttendDto;
import cy.dtos.ResponseDto;
import cy.models.RequestAttendByNameAndYearMonth;
import cy.repositories.IUserRepository;
import cy.services.IPayRollService;
import cy.services.IRequestAttendService;
import cy.services.IUserService;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;
import java.util.Date;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "test")
public class TestController {

    @Autowired
    IRequestAttendService iRequestAttendService;
    @Autowired
    IPayRollService iPayRollService;
    @Autowired
    IUserRepository iUserRepository;
    @Autowired
    IUserService iUserService;
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @GetMapping
    public ResponseDto getCurrentTime() {
        return ResponseDto.of(this.iRequestAttendService.totalDayOfAttendInMonth(49L, new Date(122, 6, 22), new Date(122, 7, 23)));
    }

    @GetMapping("/test")
    public ResponseDto calculateDate(Pageable pageable, @RequestParam(value = "startMonth") String startMonth, @RequestParam(value = "startYear") String startYear) {
        List<PayRollDto> payRollDtos = iUserService.calculatePayRoll(pageable,Integer.parseInt(startMonth), Integer.parseInt(startYear));
        Page<PayRollDto> pages = new PageImpl<PayRollDto>(payRollDtos, pageable, payRollDtos.size());
        return ResponseDto.of(pages);
    }




    @PostMapping("/testne")
    public ResponseDto findByUserName(RequestAttendByNameAndYearMonth data) throws ParseException {
        List<RequestAttendDto> result = this.requestAttendService.findByUsername(data);
        return ResponseDto.otherData(result,iPayRollService.totalWorkingDayEndWorked(data));
    }
}
