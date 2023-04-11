package cy.resources.project;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.entities.common.RoleEntity;
import cy.models.project.FeatureFilterModel;
import cy.models.project.FeatureModel;
import cy.services.project.IFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "feature/")
public class FeatureResource {
    @Autowired
    IFeatureService featureService;

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping("/get-all")
    public ResponseDto getAllFeature(Pageable pageable) {
        return ResponseDto.of(featureService.findAll(pageable));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping("/get-by-id/{id}")
    public ResponseDto getAllFeature(@PathVariable("id") Long id) {
        return ResponseDto.of(featureService.findById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping("/add-feature")
    public ResponseDto addNewFeature(@Valid FeatureModel model) {
        return ResponseDto.of(this.featureService.add(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping("/update-feature")
    public ResponseDto updateFeature(@Valid FeatureModel model) {
        return ResponseDto.of(this.featureService.update(model));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @DeleteMapping("/delete-feature/{id}")
    public ResponseDto deleteFeature(@PathVariable("id") Long id) {
        return ResponseDto.of(this.featureService.deleteById(id));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @PostMapping("/search-feature")
    public ResponseDto searchFeature(@RequestBody @Valid FeatureFilterModel model, Pageable pageable) {
//        return ResponseDto.of(this.featureService.filter(pageable, FeatureSpecification.filterAndSearch(model)));
        return ResponseDto.of(this.featureService.findByPage(model,pageable));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping("/find-all-by-project-id")
    public ResponseDto getAllFeatureByProjectId(@RequestParam("id") Long id, Pageable pageable) {
        return ResponseDto.of(featureService.findAllByProjectId(id, pageable));
    }

    @RolesAllowed({RoleEntity.ADMINISTRATOR, RoleEntity.ADMIN, RoleEntity.MANAGER, RoleEntity.EMPLOYEE, RoleEntity.LEADER})
    @GetMapping("/update-status-feature/{id}/{status}")
    public ResponseDto updateStatusFeature(@PathVariable("id") Long id, @PathVariable("status") String status) {
        return ResponseDto.of(featureService.updateStatusFeature(id,status));
    }


}
