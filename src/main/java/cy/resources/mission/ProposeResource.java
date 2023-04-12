package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.project.ProjectModel;
import cy.services.mission.IProposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "propose")
@RestController
public class ProposeResource {

}
