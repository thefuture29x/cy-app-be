package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.dtos.project.TagRelationDto;
import cy.models.project.SubTaskModel;
import cy.models.project.TagRelationModel;
import cy.services.project.ISubTaskService;
import cy.services.project.ITagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "/tag_relation/")
public class TagRelationResources {
    @Autowired
    ITagRelationService iTagRelationService;
    @PostMapping(value = "/add")
    public Object add(@RequestBody TagRelationModel tagRelationModel) {
        TagRelationDto tagRelationDto = iTagRelationService.add(tagRelationModel);
        if (tagRelationDto == null){
            return ResponseDto.of(181,tagRelationDto);
        }
       return ResponseDto.of(tagRelationDto);
    }
}
