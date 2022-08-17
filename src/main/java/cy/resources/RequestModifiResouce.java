package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.RequestAttendDto;
import cy.dtos.RequestModifiDto;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.NotificationModel;
import cy.models.RequestAll;
import cy.models.RequestModifiModel;
import cy.services.INotificationService;
import cy.services.IResquestModifiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_modifi/")
public class RequestModifiResouce {
    @Autowired
    IResquestModifiService iResquestModifiService;
    @Autowired
    INotificationService notificationService;

    /*
    * @author: HaiPhong
    * @since: 16/08/2022 2:19 CH
    * @description-VN:  Lấy danh sách tất cả yêu cầu chỉnh sửa
    * @description-EN:  Get all request modifi
    * @param: pageable
    * @return:
    *
    * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all request modifi with Pageable")
    @GetMapping
    public ResponseDto getAllRequestModifi(Pageable pageable){
        return ResponseDto.of(iResquestModifiService.findAll(pageable));
    }

    /*
    * @author: HaiPhong
    * @since: 16/08/2022 2:20 CH
    * @description-VN:  Tạo mới một yêu cầu chỉnh sửa
    * @description-EN:  Add new request modifi
    * @param: RequestModifiModel
    * @return:
    *
    * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Add new request modifi")
    @PostMapping
    public ResponseDto addRequestModifi(RequestModifiModel model) throws IOException {
        return ResponseDto.of(iResquestModifiService.add(model));
    }

    /*
    * @author: HaiPhong
    * @since: 16/08/2022 2:21 CH
    * @description-VN:  Cập nhật yêu cầu chỉnh sửa chấm công
    * @description-EN:  Update request modifi
    * @param: RequestModifiModel
    * @return:
    *
    * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Update request modifi")
    @PutMapping
    public ResponseDto updateRequestModifi(RequestModifiModel model) throws IOException {
        return ResponseDto.of(iResquestModifiService.update(model));
    }

    /*
    * @author: HaiPhong
    * @since: 16/08/2022 2:23 CH
    * @description-VN:  Xóa yêu cầu chỉnh sửa chấm công
    * @description-EN:  Delete request modifi
    * @param: id
    * @return:
    *
    * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER})
    @Operation(summary = "Delete request modifi")
    @DeleteMapping
    public ResponseDto deleteRequestModifi(Long id){
        return ResponseDto.of(iResquestModifiService.deleteById(id));
    }


    /*
    * @author: HaiPhong
    * @since: 16/08/2022 2:24 CH
    * @description-VN:  Tìm kiếm một yêu cầu chỉnh sửa chấm công bằng id
    * @description-EN:  Find request modifi by id
    * @param: id
    * @return:
    *
    * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Find request modifi by id")
    @GetMapping("find-by-id")
    public ResponseDto findRequestModifi(Long id){
        return ResponseDto.of(iResquestModifiService.findById(id));
    }
    /*
     * @author: Huu Quang
     * @since: 16/08/2022 3:30 CH
     * @description-VN:  Gửi yêu cầu thay đổi yêu cầu chấm công
     * @description-EN:
     * @:
     * @return:
     *
     * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all request modifi with Pageable")
    @PostMapping("/sendRequestModifi")
    public ResponseDto sendRequestModifi(@ModelAttribute RequestModifiModel requestModifiModel){
        RequestModifiDto requestModifiDto = iResquestModifiService.sendResquestModifi(requestModifiModel);
        if (requestModifiDto == null){

            return ResponseDto.of(165,requestModifiDto);
        }
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setRequestModifiId(requestModifiDto.getId());
        notificationModel.setContent("Yêu cầu thay đổi ngày chấm công đã được giử");
        notificationModel.setTitle("Yêu cầu sửa đổi bảng chấm công");
        try{
            notificationService.add(notificationModel);
            return ResponseDto.of(requestModifiDto);
        }catch (Exception e){
            return ResponseDto.of(165,requestModifiDto);
        }
    }
    /*
     * @author: Huu Quang
     * @since: 16/08/2022 3:30 CH
     * @description-VN:  Gửi yêu cầu thay đổi yêu cầu chấm công
     * @description-EN:
     * @:
     * @return:
     *
     * */
    @RolesAllowed({RoleEntity.ADMINISTRATOR,RoleEntity.ADMIN,RoleEntity.MANAGER,RoleEntity.LEADER,RoleEntity.EMPLOYEE})
    @Operation(summary = "Get all request modifi with Pageable")
    @PostMapping("/checkAttend")
    public ResponseDto checkAttend(@RequestBody RequestAll requestAll ){
        if (requestAll.getIdUser() == null || requestAll.getDateCheckAttend() == null){
            return ResponseDto.of(165,requestAll);
        }
        RequestAttendDto requestAttendDto = iResquestModifiService.checkAttend(requestAll.getDateCheckAttend(),requestAll.getIdUser());
        if (requestAttendDto == null){
            return ResponseDto.of(165,requestAttendDto);
        }
        return ResponseDto.of(requestAttendDto);
    }
}
