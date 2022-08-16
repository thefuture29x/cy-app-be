package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.models.CreateAttendRequest;
import cy.dtos.RequestAttendDto;
import cy.services.impl.RequestAttendServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = FrontendConfiguration.PREFIX_API+"request_attend/")
public class RequestAttendResource {
    @Autowired
    private RequestAttendServiceImpl requestAttendService;

    @PostMapping(value = "create")
    public void create(CreateAttendRequest createAttendRequest) {
        RequestAttendDto requestAttendDto = requestAttendService.requestToDto(createAttendRequest);
    }
}
