package cy.resources.publics;

import cy.configs.FrontendConfiguration;
import cy.dtos.CustomHandleException;
import cy.dtos.ResponseDto;
import cy.services.IUserService;
import cy.configs.jwt.JwtLoginResponse;
import cy.configs.jwt.JwtUserLoginModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(FrontendConfiguration.PREFIX_API + "public/test")
public class TestRestController {

    @Autowired
    private IUserService userService;

    @GetMapping("1")
    public ResponseEntity pub(@RequestParam("uid") Long uid) throws CustomHandleException {
        if(uid == 1){
            throw new CustomHandleException(12);
        }
        return ResponseEntity.ok("kok");
    }

    @PostMapping("login")
    public ResponseDto loginUser(@RequestBody @Valid JwtUserLoginModel model) {
        JwtLoginResponse jwtUserLoginModel = userService.logIn(model);
        return ResponseDto.of(jwtUserLoginModel);
    }

}
