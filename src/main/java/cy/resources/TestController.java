package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.RequestModifiDto;
import cy.dtos.RequestSendMeDto;
import cy.dtos.ResponseDto;
import cy.repositories.IRequestAttendRepository;
import cy.repositories.IRequestModifiRepository;
import cy.repositories.IUserRepository;
import cy.services.IPayRollService;
import cy.services.IRequestAttendService;
import cy.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "test")
public class TestController {

    @Autowired
    IRequestAttendService iRequestAttendService;
    @Autowired
    IPayRollService iPayRollService;
    @Autowired
    IUserRepository iUserRepository;

    @GetMapping
    public ResponseDto getCurrentTime() {
        return ResponseDto.of(this.iRequestAttendService.totalDayOfAttendInMonth(49L, new Date(122, 6, 22), new Date(122, 7, 23)));
    }

    @GetMapping("/test")
    public ResponseDto calculateDate(String timeStart, String timeEnd) {
        return null;
    }




    @GetMapping("testne")
    public ResponseDto testHashmap(){

        return ResponseDto.of(null);
    }
}
