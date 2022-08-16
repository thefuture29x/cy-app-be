package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.RequestOTModel;
import cy.services.IRequestOTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"request_ot/")
public class RequestOTResource {
    @Autowired
    private IRequestOTService requestOTService;

    @GetMapping
    public ResponseDto getAllRequestOT(){
        return ResponseDto.of(requestOTService.findAll());
    }

    /*
    * @author: Manh Tran
    * @since: 17/08/2022 6:03 AM
    * @description-VN:
    * @description-EN:
    * @param:
    * @return:
    *
    * */
    @PostMapping(value = "create")
    public ResponseDto createRequestOT(RequestOTModel requestOTModel){
        return ResponseDto.of(requestOTService.add(requestOTModel));
    }
}
