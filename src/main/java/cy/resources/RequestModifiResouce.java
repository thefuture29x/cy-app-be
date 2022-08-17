package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.RequestModifiModel;
import cy.services.IResquestModifiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_modifi/")
public class RequestModifiResouce {
    @Autowired
    IResquestModifiService iResquestModifiService;

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

}
