package cy.resources.mission;

import cy.configs.FrontendConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = FrontendConfiguration.PREFIX_API + "propose")
@RestController
public class ProposeResource {

}
