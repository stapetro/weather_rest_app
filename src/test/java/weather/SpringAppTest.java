package weather;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import weather.data.repo.WeatherRepository;
import weather.data.service.WeatherService;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAppTest {
    @Autowired
    private WeatherService weatherSvc;

    @Test
    @Ignore
    public void saveWeatherTemps() {
        WeatherRepository repo = weatherSvc.getWeatherRepo();
        Weather w1 = new Weather(1);
        Weather w2 = new Weather(2);
        Weather w3 = new Weather(7);
        repo.saveAll(List.of(w1, w2, w3));
    }

    @Test
    public void testEsQuery() {
        final int resultSize = 10;
        Page<Weather> res = weatherSvc.getLastTemperatures(Optional.of(resultSize));
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.getSize()).isLessThanOrEqualTo(resultSize);
        res.forEach(weather -> {
            System.out.println("ES: " + weather);
        });
    }

    @Test
    public void testEsAverageOnTopTemperatures() {
        final int topTemperatureNumber = 10;
        double avgTemp = weatherSvc.getAverageTemp(Optional.of(topTemperatureNumber));
        Assertions.assertThat(avgTemp).isPositive();
        System.out.println(String.format("Average temp for top %d temps is %f", topTemperatureNumber, avgTemp));
    }

    @Test
    public void testEsAverageOnAllTemperatures() {
        double avgTemp = weatherSvc.getAverageTemp(Optional.empty());
        Assertions.assertThat(avgTemp).isPositive();
        System.out.println("Average temp over all is " + avgTemp);
    }
}
