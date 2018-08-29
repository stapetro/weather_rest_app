package weather.data.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import weather.Weather;
import weather.data.repo.WeatherRepository;

import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final String PROP_TEMP_AVG = "temp_avg";

    private final ElasticsearchTemplate esTemplate;
    private final WeatherRepository weatherRepo;

    @Autowired
    public WeatherServiceImpl(WeatherRepository weatherRepo, ElasticsearchTemplate esTemplate) {
        this.weatherRepo = weatherRepo;
        this.esTemplate = esTemplate;
    }

    @Override
    public Page<Weather> getLastTemperatures(Optional<Integer> numberOfElems) {
        int pageSize = numberOfElems.isPresent() ? numberOfElems.get() : 100;
        return weatherRepo.findAll(getPageConf(pageSize));
    }

    private PageRequest getPageConf(int pageSize) {
        return PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, Weather.PROP_TIMESTAMP));
    }

    @Override
    public double getAverageTemp(Optional<Integer> numberOfElems) {
        final NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        if (numberOfElems.isPresent()) {
            queryBuilder.withQuery(newTopWeatherQuery(numberOfElems));
        } else {
            queryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }
        final SearchQuery query = queryBuilder.withSearchType(SearchType.DEFAULT).withIndices(Weather.ES_INDEX_NAME)
                .withTypes(Weather.ES_TYPE_NAME)
                .addAggregation(AggregationBuilders.avg(PROP_TEMP_AVG).field(Weather.PROP_TEMPERATURE)).build();
        final Aggregations res = esTemplate.query(query, SearchResponse::getAggregations);
        final InternalAvg avgAgg = res.get(PROP_TEMP_AVG);
        return avgAgg.getValue();
    }

    @Override
    public WeatherRepository getWeatherRepo() {
        return weatherRepo;
    }

    private BoolQueryBuilder newTopWeatherQuery(Optional<Integer> numberOfElems) {
        final Page<Weather> lastTemps = getLastTemperatures(numberOfElems);
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().minimumShouldMatch(1);
        lastTemps.map(weather -> QueryBuilders.matchQuery("id", weather.getId())).stream().forEach((boolQueryBuilder::should));
        return boolQueryBuilder;
    }
}
