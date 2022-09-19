package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.dtos.project.DataSearchTag;
import cy.dtos.project.TagRelationDto;
import cy.models.project.SubTaskModel;
import cy.models.project.TagRelationModel;
import cy.services.project.ISubTaskService;
import cy.services.project.ITagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = FrontendConfiguration.PREFIX_API + "/tag_relation/")
public class TagRelationResources {
    @Autowired
    ITagRelationService iTagRelationService;
    @PostMapping(value = "/add")
    public Object add(@RequestBody TagRelationModel tagRelationModel) {
        TagRelationDto tagRelationDto = iTagRelationService.add(tagRelationModel);
        if (tagRelationDto == null){
            return ResponseDto.of(183,tagRelationDto);
        }
       return ResponseDto.of(tagRelationDto);
    }

    @GetMapping(value = "/findAllByTag/{pageIndex}/{pageSize}")
    public Object findAllByTag(@PathVariable("pageIndex") Integer pageIndex,@PathVariable("pageSize") Integer pageSize,@RequestParam("search") String search) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Page<DataSearchTag> dataSearchTags = iTagRelationService.findAllByTag(search,pageable);
        return ResponseDto.of(dataSearchTags);
    }
}
