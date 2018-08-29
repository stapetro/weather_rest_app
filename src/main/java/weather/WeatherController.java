package weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import weather.data.service.WeatherService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@RestController
@PropertySource(value = "classpath:application.properties")
public class WeatherController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private Environment env;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private WeatherService weatherSvc;

    @RequestMapping(value = "/temp", method = RequestMethod.GET)
    public Weather getAverageTemp() {
        final Instant now = Instant.now();
        final Weather weather = new Weather("timeStampPretty: " + now.toString(), (float) weatherSvc.getAverageTemp(Optional.of(10)), now.toEpochMilli());
        return weather;
    }

    @RequestMapping(value = "/temp/trigger", method = RequestMethod.POST)
    public boolean triggerTempDataLoading() {
        TempDataScheduleConfigurer.configureTempDataTask(new ScheduledTaskRegistrar(), taskScheduler,
                env.getRequiredProperty(Application.OPENWEATHER_APPID), weatherSvc.getWeatherRepo());
        return taskScheduler != null;
    }

    @RequestMapping(value = "/temp/export", method = RequestMethod.GET)
    public void exportTempDataCalls(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        final String csvFileName = "weathers.csv";
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);
        Page<Weather> weatherTemps = weatherSvc.getLastTemperatures(Optional.empty());
        final String[] header = {Weather.PROP_TEMPERATURE, Weather.PROP_TIMESTAMP};
        try (final ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE)) {
            csvWriter.writeHeader(header);
            weatherTemps.forEach((temp) -> {
                try {
                    csvWriter.write(temp, header);
                } catch (IOException ex) {
                    LOGGER.error("Error mapping Bean to CSV", ex);
                }
            });
        }


    }
}
