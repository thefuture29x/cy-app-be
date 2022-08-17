package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.RequestModifiDto;
import cy.dtos.RequestSendMeDto;
import cy.dtos.ResponseDto;
import cy.repositories.IRequestModifiRepository;
import cy.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"test")
public class TestController {
    @Autowired
    IRequestModifiRepository iRequestModifiRepository;
    @Autowired
    IUserService iUserService;
    @GetMapping
    public ResponseDto getCurrentTime(Long id){
        return ResponseDto.of(iUserService.getAllRequestSendMe(id));
    }
}
