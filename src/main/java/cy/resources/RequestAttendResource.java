package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.CreateAttendRequest;
import cy.dtos.RequestAttendDto;
import cy.models.RequestAttendModel;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = FrontendConfiguration.PREFIX_API+"request_attend/")
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @PostMapping(value = "create")
    public ResponseDto create(CreateAttendRequest createAttendRequest) {
        RequestAttendModel requestAttendModel = requestAttendService.requestToModel(createAttendRequest);
        RequestAttendDto result = requestAttendService.add(requestAttendModel);
        return ResponseDto.of(result);
    }
}
