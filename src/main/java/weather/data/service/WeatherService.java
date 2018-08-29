package weather.data.service;

import org.springframework.data.domain.Page;
import weather.Weather;
import weather.data.repo.WeatherRepository;

import java.util.Optional;

public interface WeatherService {
    /**
     * Get last temperatures.
     *
     * @param numberOfElems Limit size or all if it's empty.
     * @return Weather temperatures ordered descending.
     */
    Page<Weather> getLastTemperatures(Optional<Integer> numberOfElems);

    /**
     * Get average temperature.
     *
     * @param numberOfElems Average for top N weathers or for all if it's empty.
     * @return Average temperature value.
     */
    double getAverageTemp(Optional<Integer> numberOfElems);

    WeatherRepository getWeatherRepo();
}
