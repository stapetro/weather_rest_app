package weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import weather.data.repo.WeatherRepository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TempDataLoader implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TempDataLoader.class);
    private final int maxNumberOfRuns;
    private static final ParameterizedTypeReference<Map<String, Object>> TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };
    private final String openWeatherAppId;
    private final WeatherRepository weatherRepo;
    private AtomicInteger currRunNumber;
    private ScheduledTask future;
    private final WebClient webClient;

    public TempDataLoader(int maxNumberOfRuns, String openWeatherAppId, WeatherRepository weatherRepo) {
        currRunNumber = new AtomicInteger(0);
        this.webClient = WebClient.builder().baseUrl("https://api.openweathermap.org")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.maxNumberOfRuns = maxNumberOfRuns;
        this.openWeatherAppId = openWeatherAppId;
        this.weatherRepo = weatherRepo;
    }

    @Override
    public void run() {
        if (currRunNumber.incrementAndGet() > maxNumberOfRuns) {
            if (future != null) {
                future.cancel();
                LOGGER.info("Cancel temperature data loader");
            } else {
                LOGGER.warn("Cannot cancel future because it's null");
            }
            return;
        }
        LOGGER.info("Retrieve temperature data...");
        callOpenWeatherApi();
    }


    public void setFuture(ScheduledTask future) {
        this.future = future;
    }

    private void callOpenWeatherApi() {
        // https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/
        // https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-webclient.html
        // https://www.baeldung.com/spring-5-webclient
        final Mono<Map<String, Object>> res = webClient.get().uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
                .queryParam("q", "Sofia,bg")
                .queryParam("units", "metric")
                .queryParam("appid", this.openWeatherAppId)
                .build()).retrieve().bodyToMono(TYPE_REFERENCE);
        final Map<String, Object> resData = res.block();
        if (resData != null) {
            LOGGER.info(resData.toString());
            final Weather weather = new Weather((int) ((Map<String, Object>) resData.get("main")).get("temp"));
            weatherRepo.save(weather);
        } else {
            LOGGER.warn("Open weather API is unavailable!");
        }
    }
}
