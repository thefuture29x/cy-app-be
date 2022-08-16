package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.HistoryRequestDto;
import cy.dtos.ResponseDto;
import cy.models.HistoryRequestModel;
import cy.services.IHistoryRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API+"/history_request/")
public class HistoryRequestResource {
    @Autowired
    IHistoryRequestService iHistoryRequestService;
    @GetMapping("deleteById/{id}")
    @Secured(value = {"ROLE_ADMINISTRATOR","ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE","ROLE_LEADER"})
    public ResponseDto deleteById(@PathVariable("id") Long id){
        Boolean delete = iHistoryRequestService.deleteById(id);
        if (delete == true){
            return ResponseDto.of(delete);
        }else {
            return ResponseDto.of(161,delete);
        }
    }
    @GetMapping("findById/{id}")
    @Secured(value = {"ROLE_ADMINISTRATOR","ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE","ROLE_LEADER"})
    public ResponseDto findById(@PathVariable("id") Long id){
        HistoryRequestDto historyRequestDto = iHistoryRequestService.findById(id);
        if (historyRequestDto != null){
            return ResponseDto.of(historyRequestDto);
        }else {
            return ResponseDto.of(162,historyRequestDto);
        }
    }
    @PostMapping("add")
    @Secured(value = {"ROLE_ADMINISTRATOR","ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE","ROLE_LEADER"})
    public ResponseDto add(@RequestBody HistoryRequestModel historyRequestModel) throws IOException {
        HistoryRequestDto historyRequestDto = iHistoryRequestService.add(historyRequestModel);
        if (historyRequestDto != null){
            return ResponseDto.of(historyRequestDto);
        }else {
            return ResponseDto.of(163,historyRequestDto);
        }
    }
    @PostMapping("update")
    @Secured(value = {"ROLE_ADMINISTRATOR","ROLE_ADMIN","ROLE_MANAGER","ROLE_EMPLOYEE","ROLE_LEADER"})
    public ResponseDto update(@RequestBody HistoryRequestModel historyRequestModel) throws IOException {
        HistoryRequestDto historyRequestDto = iHistoryRequestService.update(historyRequestModel);
        if (historyRequestDto != null){
            return ResponseDto.of(historyRequestDto);
        }else {
            return ResponseDto.of(163,historyRequestDto);
        }
    }
}
