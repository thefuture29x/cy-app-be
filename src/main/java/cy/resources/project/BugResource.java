package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.BugModel;
import cy.services.project.impl.BugServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "bug")
@RestController
public class BugResource {
    @Autowired
    BugServiceImpl bugService;
    @PostMapping(value = "/create")
    public ResponseDto create( BugModel bugModel) {
        return ResponseDto.of(bugService.add(bugModel));
    }
    @GetMapping(value = "/get")
    public ResponseDto get(@RequestParam Long id) {
        return ResponseDto.of(bugService.findById(id));
    }
    @PostMapping(value = "/update")
    public ResponseDto update( BugModel bugModel) {
        return ResponseDto.of(bugService.update(bugModel));
    }
    @DeleteMapping(value = "/delete")
    public ResponseDto delete(@RequestParam(name = "id")Long id) {
        return ResponseDto.of(bugService.deleteById(id));
    }

    @PutMapping(value = "/updateStatusSubTaskToBug")
    public ResponseDto updateStatusSubTaskToBug(@RequestParam(name = "id")Long id,@RequestParam(name = "status")int status) {
        return ResponseDto.of(bugService.updateStatusSubTaskToBug(id,status));
    }
    @PutMapping(value = "/updateStatusTaskToBug")
    public ResponseDto updateStatusTaskToBug(@RequestParam(name = "id")Long id,@RequestParam(name = "status")int status) {
        return ResponseDto.of(bugService.updateStatusTaskToBug(id,status));
    }
    @GetMapping(value = "findAllBugOfProject")
    public ResponseDto findAllBugOfProject(@RequestParam(name = "id")Long idProject ,Pageable pageable){
        return ResponseDto.of(bugService.findAllBugOfProject(idProject,pageable));
    }
    @PostMapping(value = "/findBypage")
    public ResponseDto findBypage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize, @RequestBody BugModel bugModel) {
        return ResponseDto.of(bugService.findByPage(pageIndex,pageSize,bugModel));
    }

}
