package weather.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.sql.DataSource;

@EnableWebSecurity
@PropertySource(value = "classpath:application.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and().csrf().disable() //enable POST requests with basic auth
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(getDataSource())
                .usersByUsernameQuery(
                        "select username,password,true as enabled from `account` where username = ?")
                .authoritiesByUsernameQuery(
                        "select username,role as authority from `account` where username = ?"
                ).rolePrefix("ROLE_")
                .passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }

    private DataSource getDataSource() {
        final DataSource dataSource = DataSourceBuilder
                .create()
                .username(env.getRequiredProperty("spring.datasource.username"))
                .password(env.getRequiredProperty("spring.datasource.password"))
                .url(env.getRequiredProperty("spring.datasource.url"))
                .driverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"))
                .build();
        return dataSource;
    }
}