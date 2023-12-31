package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.project.BugModel;
import cy.models.project.SubTaskUpdateModel;
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

    @DeleteMapping(value = "/deleteFile")
    public ResponseDto deleteFile(@RequestParam(name = "idFile")Long idFile ,@RequestParam(name = "id")Long id) {
        return ResponseDto.of(bugService.deleteOnlyFile(idFile,id));
    }
    @PutMapping(value = "/updateStatusBugOfSubtask")
    public ResponseDto updateStatusBugOfSubtask(@RequestParam(name = "id")Long id,@RequestParam(name = "status") String status) {
        return ResponseDto.of(bugService.updateStatusBugOfSubtask(id,status));
    }
    @PutMapping(value = "/updateStatusBugOfTask")
    public ResponseDto updateStatusBugOfTask(@RequestParam(name = "id")Long id,@RequestParam(name = "status") String status) {
        return ResponseDto.of(bugService.updateStatusBugOfTask(id,status));
    }
    @PutMapping(value = "/updateStatusSubTaskToBug")
    public ResponseDto updateStatusSubTaskToBug(@RequestParam(name = "id")Long id,@RequestParam(name = "status")int status) {
        return ResponseDto.of(bugService.updateStatusSubTaskToBug(id,status));
    }
    @PutMapping(value = "/updateStatusTaskToBug")
    public ResponseDto updateStatusTaskToBug(@RequestParam(name = "id")Long id,@RequestParam(name = "status")int status) {
        return ResponseDto.of(bugService.updateStatusTaskToBug(id,status));
    }
    @PostMapping(value = "filterBug")
    public ResponseDto filterBug(Pageable pageable, @RequestBody BugModel bugModel){
        return ResponseDto.of(bugService.filterBug(pageable, bugModel));
    }
    @GetMapping(value = "findAllBugOfProject")
    public ResponseDto findAllBugOfProject(@RequestParam(name = "id")Long idProject ,Pageable pageable){
        return ResponseDto.of(bugService.findAllBugOfProject(idProject,pageable));
    }
    @GetMapping(value = "findAllBugOfFeature")
    public ResponseDto findAllBugOfFeature(@RequestParam(name = "id")Long idFeature ,Pageable pageable){
        return ResponseDto.of(bugService.findAllBugOfFeature(idFeature,pageable));
    }
    @GetMapping(value = "findAllBugOfTask")
    public ResponseDto findAllBugOfTask(@RequestParam(name = "id")Long idTask,Pageable pageable){
        return ResponseDto.of(bugService.findAllBugOfTask(idTask,pageable));
    }
    @GetMapping(value = "findAllBugOfSubTask")
    public ResponseDto findAllBugOfSubTask(@RequestParam(name = "id")Long idSubTask ,Pageable pageable){
        return ResponseDto.of(bugService.findAllBugOfSubTask(idSubTask,pageable));
    }
    @PostMapping(value = "/findByPage")
    public ResponseDto findByPage(@RequestParam(name = "pageIndex") Integer pageIndex, @RequestParam(name = "pageSize") Integer pageSize, @RequestBody BugModel bugModel) {
        return ResponseDto.of(bugService.findByPage(pageIndex,pageSize,bugModel));
    }
    @GetMapping(value = "getAllBug")
    public ResponseDto getAllBug(@RequestParam(name = "id") Long idProject){
        return ResponseDto.of(bugService.getAllBug(idProject));
    }
    @PostMapping(value = "addReviewerToBug/{idBug}")
    public ResponseDto addReviewerToBug(@PathVariable Long idBug,@RequestBody SubTaskUpdateModel subTaskUpdateModel){
        bugService.addReviewerToBug(idBug,subTaskUpdateModel);
        return ResponseDto.of(null);
    }

}
