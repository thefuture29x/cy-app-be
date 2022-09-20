package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.repositories.project.IFeatureRepository;
import cy.repositories.project.specification.FeatureSpecification;
import cy.services.project.IFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API+"feature/")
public class FeatureResource {
    @Autowired
    IFeatureService featureService;
    @GetMapping("/get-all")
    public ResponseDto getAllFeature(Pageable pageable){
        return ResponseDto.of(featureService.findAll(pageable));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseDto getAllFeature(@PathVariable("id") Long id){
        return ResponseDto.of(featureService.findById(id));
    }

    @PostMapping("/add-feature")
    public ResponseDto addNewFeature(@Valid FeatureModel model){
        return ResponseDto.of(this.featureService.add(model));
    }

    @PostMapping("/update-feature")
    public ResponseDto updateFeature(@Valid FeatureModel model){
        return ResponseDto.of(this.featureService.update(model));
    }

    @DeleteMapping("/delete-feature/{id}")
    public ResponseDto deleteFeature(@PathVariable("id") Long id){
        return ResponseDto.of(this.featureService.deleteById(id));
    }

    @PostMapping("/search-feature")
    public ResponseDto searchFeature(@RequestBody @Valid FeatureFilterModel model, Pageable pageable){
        return ResponseDto.of(this.featureService.filter(pageable, FeatureSpecification.filterAndSearch(model)));
    }


}
