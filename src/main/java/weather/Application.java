package weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static final String OPENWEATHER_APPID = "openweather.appid";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
