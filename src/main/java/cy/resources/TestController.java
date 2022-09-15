package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.configs.excel.PayRollExcelExporter;
import cy.dtos.attendance.PayRollDto;
import cy.dtos.attendance.RequestAttendDto;
import cy.dtos.ResponseDto;
import cy.entities.project.*;
import cy.models.attendance.RequestAttendByNameAndYearMonth;
import cy.repositories.IUserRepository;
import cy.services.attendance.IPayRollService;
import cy.services.attendance.IRequestAttendService;
import cy.services.IUserService;
import cy.services.attendance.impl.RequestAttendServiceImpl;
import cy.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        return ResponseDto.otherData(result,iPayRollService.totalWorkingDayEndWorked(data,null));
    }

    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response, Pageable pageable, @RequestParam(value = "startMonth") int startMonth, @RequestParam(value = "startYear") int startYear) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=ChamCong_"+startMonth+"_"+startYear+".xlsx";
        response.setHeader(headerKey, headerValue);

        List<PayRollDto> payRollDtos = iUserService.calculatePayRoll(pageable,startMonth, startYear);

        PayRollExcelExporter excelExporter = new PayRollExcelExporter(payRollDtos, startMonth, startYear);
        excelExporter.export(response);
    }

    @GetMapping("test-enum")
    public String testenum(@RequestParam Const.status status){
        return Const.type.TYPE_DEV.name();
    }
    @GetMapping("test-no-enum")
    public void testenumo(@RequestParam String status){

    }
}
