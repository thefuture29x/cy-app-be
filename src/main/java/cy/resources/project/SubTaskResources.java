package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.dtos.project.SubTaskDto;
import cy.models.project.SubTaskModel;
import cy.services.project.ISubTaskService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "/subtask")
public class SubTaskResources {
    @Autowired
    ISubTaskService iSubTaskService;

    @ApiOperation(value = "Thêm mới subtask.", notes = "- Thời gian phải được gửi theo định dạng yyyy-MM-dd HH:mm:ss.\n" + "- Bắt buộc phải gửi thuộc tính taskId.")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/add")
    public Object add(@ModelAttribute SubTaskModel subTaskModel) {
        return ResponseDto.of(iSubTaskService.add(subTaskModel));
    }

    @ApiOperation(value = "Cập nhật subtask.", notes = "Muốn xoá tập tin đính kèm thì gửi tên tập tin (API trả về, kèm thêm đoạn số ngẫu nhiên) chứ không phải tên tập tin thực tế.")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PutMapping(value = "/update")
    public Object update(@ModelAttribute SubTaskModel subTaskModel) {
        SubTaskDto updateResult = iSubTaskService.update(subTaskModel);
        return ResponseDto.of(updateResult);
    }

    @ApiOperation(value = "Xóa subtask.")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @DeleteMapping(value = "/delete/{id}")
    public Object delete(@PathVariable Long id) {
        return ResponseDto.of(iSubTaskService.softDeleteById(id));
    }

    @ApiOperation(value = "Tìm subtask theo id.")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/find-by-id/{id}")
    public Object findById(@PathVariable Long id) {
        return ResponseDto.of(iSubTaskService.findById(id));
    }

    @ApiOperation(value = "Tìm tất cả subtask theo project id (có phân trang).")
//    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE","ROLE_LEADER","ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    @GetMapping("/find-all-by-project-id")
    public ResponseDto getAllSubTaskByProjectId(@RequestParam("id") Long id, Pageable pageable) {
        return ResponseDto.of(iSubTaskService.findAllByProjectId(id, pageable));
    }

    @ApiOperation(value = "Tìm tất cả subtask theo task id (có phân trang).", notes = "Nếu muốn tìm kiếm thì gửi kèm từ khoá (keyword).")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @GetMapping(value = "/find-all-by-task-id/{id}")
    public ResponseDto findAllByTaskId(@PathVariable Long id, @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword, Pageable pageable) {
        Page<SubTaskDto> result = iSubTaskService.findAllByTaskId(id, keyword, pageable);
        return ResponseDto.of(result);
    }

    @ApiOperation(value = "Lọc subtask theo trạng thái và/hoặc khoảng thời gian (có phân trang).", notes = "- Có thể không gửi 1 trong 2 thuộc tính thời gian bắt đầu, thời gian kết thúc.\n" + "- Thời gian phải được gửi theo định dạng yyyy-MM-dd HH:mm:ss.\n" + "- Bắt buộc phải gửi thuộc tính taskId.")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_LEADER", "ROLE_MANAGER", "ROLE_ADMINISTRATOR"})
    @PostMapping(value = "/filter")
    public ResponseDto filter(@RequestBody SubTaskModel subTaskModel, Pageable pageable) {
        Page<SubTaskDto> result = iSubTaskService.filter(subTaskModel, pageable);
        return ResponseDto.of(result);
    }
}
