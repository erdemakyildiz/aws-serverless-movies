package be.houbrechtsit.goserverless;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;

import java.util.List;

import static com.amazonaws.regions.Regions.US_WEST_1;

/**
 * @author IHoubr
 */
public class MovieRepository {
    public static final String DYNAMO_ENDPOINT_URL_ENV_VARIABLE = "DYNAMO_ENDPOINT_URL";
    private static String region = US_WEST_1.getName();
    private static MovieRepository instance;

    @Getter
    private AmazonDynamoDB amazonDynamoDB;
    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public static MovieRepository geInstance() {
        if (instance != null) {
            return instance;
        }

        if (System.getenv("AWS_REGION") != null) {
            region = System.getenv("AWS_REGION");
        }
        instance = new MovieRepository();
        return instance;
    }

    private MovieRepository() {
        DynamoDBMapperConfig dbMapperConfig =
                new DynamoDBMapperConfig.Builder().build();
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        String dynamoEndpointUrl = System.getenv(DYNAMO_ENDPOINT_URL_ENV_VARIABLE);
        if (dynamoEndpointUrl != null) {
            clientBuilder = clientBuilder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, region));
        } else {
            clientBuilder = clientBuilder.withRegion(region);
        }
        amazonDynamoDB = clientBuilder.build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dbMapperConfig);
    }

    public Movie load(String title) {
        DynamoDBQueryExpression<Movie> query = new DynamoDBQueryExpression<>();
        query.setHashKeyValues(new Movie(title, null, 0));
        PaginatedQueryList<Movie> movies = dynamoDBMapper.query(Movie.class, query);
        return movies.isEmpty() ? null : movies.get(0);
    }

    public void saveOrUpdate(Movie movie) {
        dynamoDBMapper.save(movie);
    }

    public void delete(Movie movie) {
        dynamoDBMapper.delete(movie);
    }

    public List<Movie> list() {
        return dynamoDBMapper.scan(Movie.class, new DynamoDBScanExpression());
    }
}
