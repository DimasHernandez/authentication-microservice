package co.com.pragma.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths.auth")
public class AuthPath {

    private String login;
}
