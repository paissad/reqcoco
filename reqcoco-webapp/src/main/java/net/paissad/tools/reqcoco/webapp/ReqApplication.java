package net.paissad.tools.reqcoco.webapp;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ReqApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReqApplication.class, args);
    }

    @Bean
    public Docket swaggerSettings() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(ReqApplication.class.getPackage().getName() + ".controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metadata());
    }

    private ApiInfo metadata() {
        final Contact contact = new Contact(
                ReqApplicationConstants.APPLICATION_CONTACT_NAME,
                ReqApplicationConstants.APPLICATION_URL,
                ReqApplicationConstants.APPLICATION_CONTACT_EMAIL);
        return new ApiInfo(
                ReqApplicationConstants.APPLICATION_NAME,
                ReqApplicationConstants.APPLICATION_DESCRIPTION,
                ReqApplicationConstants.getAppVersion(),
                ReqApplicationConstants.TERM_OF_SERVICE_URL,
                contact,
                ReqApplicationConstants.LICENSE,
                ReqApplicationConstants.LICENSE_URL,
                Collections.emptyList());
    }
}
