package be.houbrechtsit.goserverless;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;

import java.util.List;

import static com.amazonaws.regions.Regions.US_EAST_1;

/**
 * @author IHoubr
 */
public class MovieRepository {
    private static MovieRepository instance;

    @Getter
    private AmazonDynamoDB amazonDynamoDB;
    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public static MovieRepository geInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new MovieRepository();
        return instance;
    }

    private MovieRepository() {
        DynamoDBMapperConfig dbMapperConfig =
                new DynamoDBMapperConfig.Builder().build();
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        String dynamoEndpointUrl = LambdaEnvironment.getDynamoEndpointUrl();
        if (dynamoEndpointUrl != null) {
            clientBuilder = clientBuilder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, LambdaEnvironment.getAwsRegionName()));
        } else {
            clientBuilder = clientBuilder.withRegion(US_EAST_1.getName());
        }
        amazonDynamoDB = clientBuilder.build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dbMapperConfig);
    }

    public Movie load(String title) {
        DynamoDBQueryExpression<Movie> query = new DynamoDBQueryExpression<Movie>();
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
