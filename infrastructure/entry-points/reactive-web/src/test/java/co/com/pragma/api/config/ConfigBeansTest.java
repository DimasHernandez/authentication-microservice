package co.com.pragma.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ConfigBeansTest {

    @Bean
    @Primary
    public UserPath userPathBeanTest() {
        UserPath userPath = new UserPath();
        userPath.setUsers("/api/v1/users");
        return userPath;
    }
}
