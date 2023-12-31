package cy.resources.common;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.dtos.common.TagDto;
import cy.models.common.TagModel;
import cy.services.common.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            return ResponseDto.of(183,tagDto);
        }
        return ResponseDto.of(tagDto);
    }
    @GetMapping("{id}")
    public ResponseDto findById(@PathVariable("id") Long id){
        return ResponseDto.of(iTagService.findById(id));
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
        return ResponseDto.of(iTagService.deleteById(id));
    }
    @PostMapping("/findByPage/{pageIndex}/{pageSize}")
    public ResponseDto findByPage(@PathVariable("pageIndex") Integer pageIndex,@PathVariable("pageSize") Integer pageSize){
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Page<TagDto> tagDtos = iTagService.findAll(pageable);
        return ResponseDto.of(tagDtos);
    }
    @GetMapping("/findPageByName/{pageIndex}/{pageSize}")
    public ResponseDto findPageByName(@PathVariable("pageIndex") Integer pageIndex,@PathVariable("pageSize") Integer pageSize,@RequestParam("search") String search){
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        search = "#"+search;
        Page<TagDto> tagDtos = iTagService.findPageByName(pageable,search);
        return ResponseDto.of(tagDtos);
    }

}
