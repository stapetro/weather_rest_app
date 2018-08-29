package weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import weather.data.repo.WeatherRepository;

@Configuration
@PropertySource(value = "classpath:application.properties")
public class TempDataScheduleConfigurer implements SchedulingConfigurer {
    @Autowired
    private Environment env;
    @Autowired
    private WeatherRepository weatherRepo;

    public static void configureTempDataTask(ScheduledTaskRegistrar taskRegistrar, ThreadPoolTaskScheduler taskScheduler,
                                             String openWeatherAppId, WeatherRepository weatherRepo) {
        taskRegistrar.setTaskScheduler(taskScheduler);
        final TempDataLoader dataLoader = new TempDataLoader(3, openWeatherAppId, weatherRepo);
        final FixedDelayTask task = new FixedDelayTask(dataLoader, 30000, 1000);
        final ScheduledTask future = taskRegistrar.scheduleFixedDelayTask(task);
        dataLoader.setFuture(future);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        final ThreadPoolTaskScheduler taskScheduler = poolScheduler();
        TempDataScheduleConfigurer.configureTempDataTask(taskRegistrar, taskScheduler,
                env.getRequiredProperty(Application.OPENWEATHER_APPID), weatherRepo);
    }

    @Bean
    public ThreadPoolTaskScheduler poolScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("poolScheduler");
        scheduler.setPoolSize(3);
        scheduler.setThreadNamePrefix("weatherLoadScheduler");
        return scheduler;
    }
}
