package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.PayRollDto;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "payroll")
public class PayRollResource {
    @Autowired
    IUserService iUserService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN})
    @GetMapping("/work-date-of-month")
    public ResponseDto calculateDate(Pageable pageable,
                                     @RequestParam(value = "startMonth") String startMonth,
                                     @RequestParam(value = "startYear") String startYear) {
        List<PayRollDto> payRollDtos = iUserService.calculatePayRoll(pageable,Integer.parseInt(startMonth), Integer.parseInt(startYear));

        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min((start + pageable.getPageSize()), payRollDtos.size());
        List<PayRollDto> productDTOSubList = payRollDtos.subList(start, end);
        return ResponseDto.of(new PageImpl<>(productDTOSubList, pageable, payRollDtos.size()));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN})
    @GetMapping("/searchUserPayRoll")
    public ResponseDto searchUserPayRoll(Pageable pageable,
                                         @RequestParam(value = "startMonth") String startMonth,
                                         @RequestParam(value = "startYear") String startYear,
                                         @RequestParam(value = "keyword") String keyword) {
        List<PayRollDto> payRollDtos = iUserService.searchUserPayRoll(pageable,Integer.parseInt(startMonth), Integer.parseInt(startYear), keyword);

        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min((start + pageable.getPageSize()), payRollDtos.size());
        List<PayRollDto> productDTOSubList = payRollDtos.subList(start, end);
        return ResponseDto.of(new PageImpl<>(productDTOSubList, pageable, payRollDtos.size()));
    }

    @GetMapping("/exportExcel")
    public void exportToExcel(HttpServletResponse response, Pageable pageable, @RequestParam(value = "startMonth") int startMonth, @RequestParam(value = "startYear") int startYear) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_"  + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<PayRollDto> payRollDtos = iUserService.calculatePayRoll(pageable,startMonth, startYear);

      //  PayRollExcelExporter excelExporter = new PayRollExcelExporter(payRollDtos);

      //  excelExporter.export(response);
    }

}
