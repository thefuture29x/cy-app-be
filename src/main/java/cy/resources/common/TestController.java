package cy.resources.common;

import cy.configs.FrontendConfiguration;
import cy.configs.excel.PayRollExcelExporter;
import cy.dtos.attendance.PayRollDto;
import cy.dtos.attendance.RequestAttendDto;
import cy.dtos.common.ResponseDto;
import cy.models.common.UserModel;
import cy.models.attendance.RequestAttendByNameAndYearMonth;
import cy.repositories.common.IFileRepository;
import cy.repositories.common.IUserProjectRepository;
import cy.repositories.common.IUserRepository;
import cy.repositories.project.*;
import cy.services.attendance.IPayRollService;
import cy.services.attendance.IRequestAttendService;
import cy.services.common.IUserService;
import cy.services.attendance.impl.RequestAttendServiceImpl;
import cy.services.project.IFeatureService;
import cy.services.project.ISubTaskService;
import cy.services.project.ITaskService;
import cy.utils.Const;
import cy.utils.FileUploadProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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

    @PostMapping("/register")
    public ResponseDto registerUser(@RequestBody @Valid UserModel model) throws IOException {
        model.setId(null);
        return ResponseDto.of(this.iUserService.add(model));
    }

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

    @Autowired
    FileUploadProvider fileUploadProvider;
    @Autowired
    IFileRepository iFileRepository;
    @Autowired
    IBugRepository iBugRepository;
    @Autowired
    ITaskService iTaskService;
    @Autowired
    ISubTaskRepository iSubTaskRepository;
    @Autowired
    ITaskRepository iTaskRepository;
    @Autowired
    ISubTaskService iSubTaskService;
    @Autowired
    IFeatureRepository iFeatureRepository;
    @Autowired
    IProjectRepository iProjectRepository;
    @Autowired
    IFeatureService iFeatureService;
    @Autowired
    IUserProjectRepository iUserProjectRepository;

    @PostMapping ("change-status")
    public void testUpLoadFile(Long idParent)  {
        List<String> allStatus = iTaskRepository.getAllStatusTaskByFeatureId(idParent);
        int countStatus  = allStatus.size();
        if (countStatus == 1){
            iFeatureRepository.updateStatusFeature(idParent,allStatus.get(0));
            return;
        }else if (countStatus == 2
                && allStatus.stream().anyMatch(Const.status.IN_REVIEW.name()::contains)
                && allStatus.stream().anyMatch(Const.status.DONE.name()::contains)){
            iFeatureRepository.updateStatusFeature(idParent,Const.status.IN_REVIEW.name());
            return;
        }else if(countStatus != 0){
            iFeatureRepository.updateStatusFeature(idParent,Const.status.IN_PROGRESS.name());
            return;
        }
    }
    @GetMapping ("test-nef")
    public ResponseDto testCheckDeleted(@RequestParam(value = "taskId") Long taskId,@RequestParam(name = "idTest") Long idTest)  {

        if (taskId != null){

            System.out.println(taskId);
        }
        if (idTest != null){

            System.out.println(idTest);
        }
        return null;
    }



}
