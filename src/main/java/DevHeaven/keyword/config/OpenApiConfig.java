package DevHeaven.keyword.config;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;


@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization",
    in = SecuritySchemeIn.HEADER)
@Configuration
public class OpenApiConfig {


}
