package cy.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
public class SwaggerConfiguration {
    //Set
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public-api")
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()))
                .ignoredParameterTypes(Pageable.class)
                .apiInfo(apiInfo())
                .select()
                .paths(postPaths())
                .build();
    }

    private Predicate<String> postPaths() {
        return regex("/.*");
    }


    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(
                new SecurityReference("JWT", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("cy API")
                .description("cy API reference for developers")
                .termsOfServiceUrl("http://cyglobal.net")
                .contact(new Contact("haunv@cy.co", "https://www.cyglobal.net/", "CYinfo@CYglobal.net")).license("Cy Viet Nam")
                .licenseUrl("not have").version("1.0").build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}
