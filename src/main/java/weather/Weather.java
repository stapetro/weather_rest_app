package weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Document(indexName = Weather.ES_INDEX_NAME, type = Weather.ES_TYPE_NAME, shards = 1, replicas = 0, refreshInterval = "-1")
public class Weather {
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_TEMPERATURE = "temp";
    public static final String ES_INDEX_NAME = "weather";
    public static final String ES_TYPE_NAME = "weather";

    @Id
    private final String id;
    private final float temp;
    private final long timestamp;

    public Weather(float temp) {
        this(UUID.randomUUID().toString(), temp, Instant.now().toEpochMilli());
    }

    public Weather(@JsonProperty("id") String id, @JsonProperty("temp") float temp, @JsonProperty("timestamp") long timestamp) {
        this.id = id;
        this.temp = temp;
        this.timestamp = timestamp;
    }

    /**
     * Gets temperature in celsius.
     *
     * @return Temperature value.
     */
    public float getTemp() {
        return temp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public Instant getTimestampPretty() {
        return Instant.ofEpochMilli(timestamp);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weather weather = (Weather) o;
        return temp == weather.temp &&
                timestamp == weather.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temp, timestamp);
    }

    @Override
    public String toString() {
        return "Weather{" +
                "id='" + id + '\'' +
                ", temp=" + temp +
                ", timestamp=" + timestamp +
                ", timestampPretty=" + getTimestampPretty() +
                '}';
    }
}
