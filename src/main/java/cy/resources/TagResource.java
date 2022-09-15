package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.dtos.TagDto;
import cy.models.project.TagModel;
import cy.services.project.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "tag/")
public class TagResource {
    @Autowired
    ITagService iTagService;

    @PostMapping("add")
    public ResponseDto add(@RequestBody TagModel tagModel){
        TagDto tagDto = iTagService.add(tagModel);
        if(tagDto== null ){
            return ResponseDto.of(181,tagDto);
        }
        return ResponseDto.of(tagDto);
    }
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable("id") Long id){
         TagDto tagDto = iTagService.findById(id);
        if (tagDto == null){
            return ResponseDto.of(181,tagDto);
        }
        return ResponseDto.of(tagDto);
    }

    @PostMapping("update")
    public ResponseDto update(@RequestBody TagModel tagModel){
        TagDto tagDto = iTagService.update(tagModel);
        if(tagDto== null ){
            return ResponseDto.of(181,tagDto);
        }
        return ResponseDto.of(tagDto);
    }

    @DeleteMapping("{id}")
    public ResponseDto deleteById(@PathVariable("id") Long id){
        if (iTagService.deleteById(id) == false){
            return ResponseDto.of(181,id);
        }
        return ResponseDto.of(true);
    }
    @PostMapping("/findByPage")
    public ResponseDto findByPage(@RequestBody Pageable pageable){
        Page<TagDto> tagDtos = iTagService.findAll(pageable);
        return ResponseDto.of(tagDtos);
    }
}