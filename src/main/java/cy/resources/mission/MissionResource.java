package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import cy.dtos.common.ResponseDto;
import cy.models.project.ProjectModel;
import cy.services.project.IProjectService;
import cy.services.project.IUserViewProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "mission")
@RestController
public class MissionResource {

}
