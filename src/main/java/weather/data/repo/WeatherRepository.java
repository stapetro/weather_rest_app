package weather.data.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import weather.Weather;

@Repository
public interface WeatherRepository extends ElasticsearchRepository<Weather, String> {
}
