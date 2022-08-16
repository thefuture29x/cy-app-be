package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.CustomHandleException;
import cy.dtos.ResponseDto;
import cy.models.CreateUpdateRequestAttend;
import cy.dtos.RequestAttendDto;
import cy.models.RequestAttendModel;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = FrontendConfiguration.PREFIX_API+"request_attend/")
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @PostMapping(value = "create")
    public ResponseDto create(CreateUpdateRequestAttend addAttendRequest) {
        RequestAttendModel requestAttendModel = requestAttendService.requestToModel(addAttendRequest, 1);
        RequestAttendDto result = this.requestAttendService.add(requestAttendModel);
        return ResponseDto.of(result);
    }

    @PostMapping(value = "update")
    public ResponseDto update(CreateUpdateRequestAttend updateAttendRequest) {
        RequestAttendModel requestAttendModel = requestAttendService.requestToModel(updateAttendRequest, 2);
        RequestAttendDto result = this.requestAttendService.update(requestAttendModel);
        return ResponseDto.of(result);
    }

    @DeleteMapping(value = "delete")
    public ResponseDto delete(Long id) {
        boolean result = this.requestAttendService.deleteById(id);
        if(!result){
            throw new CustomHandleException(36);
        }
        return ResponseDto.of("Delete request attend by id " + id + " success");
    }
}
