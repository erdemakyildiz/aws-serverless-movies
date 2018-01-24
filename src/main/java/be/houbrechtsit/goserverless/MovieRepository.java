package be.houbrechtsit.goserverless;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Getter;

import java.util.List;

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
        DynamoDBMapperConfig.TableNameOverride tableNameOverride =
                DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(LambdaEnvironment.getTableNamePrefix());
        DynamoDBMapperConfig dbMapperConfig =
                new DynamoDBMapperConfig.Builder().withTableNameOverride(tableNameOverride).build();


        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        String dynamoEndpointUrl = LambdaEnvironment.getDynamoEndpointUrl();
        if (dynamoEndpointUrl != null) {
            clientBuilder = clientBuilder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, LambdaEnvironment.getAwsRegionName()));
        } else {
            clientBuilder = clientBuilder.withRegion(LambdaEnvironment.getAwsRegion());
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
