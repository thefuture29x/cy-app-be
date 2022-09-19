package cy.resources;

import cy.configs.FrontendConfiguration;
import cy.dtos.ResponseDto;
import cy.entities.RoleEntity;
import cy.models.project.FeatureModel;
import cy.repositories.project.IFeatureRepository;
import cy.services.project.IFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
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

    @PostMapping("/add-feature")
    public ResponseDto addNewFeature(@Valid FeatureModel model){
        return ResponseDto.of(this.featureService.add(model));
    }

    @PostMapping("/update-feature")
    public ResponseDto updateFeature(@Valid FeatureModel model){
        return ResponseDto.of(this.featureService.update(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @DeleteMapping("/{id}")
    public ResponseDto deleteFeature(@PathVariable Long id) {
        return ResponseDto.of(this.featureService.changIsDeleteById(id));
    }

}
