package be.houbrechts.it.awsserverless;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.amazonaws.regions.Regions.EU_WEST_1;
import static com.amazonaws.services.dynamodbv2.util.TableUtils.createTableIfNotExists;

/**
 * @author IHoubr
 */
public class MovieRepository {
    private static String region;
    private static MovieRepository instance;
    private static final Logger log = LogManager.getLogger(MovieRepository.class);

    @Getter
    private AmazonDynamoDB amazonDynamoDB;
    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public static MovieRepository getInstance() {
        return MovieRepository.getInstance(null);
    }

    public static MovieRepository getInstance(String dynamoEndpointUrl) {
        if (instance != null) {
            return instance;
        }


        if (dynamoEndpointUrl == null) {
            dynamoEndpointUrl = System.getenv("DYNAMO_ENDPOINT_URL");
        }
        log.info("dynamoEndpointUrl: {}", dynamoEndpointUrl);
        if (System.getenv("AWS_REGION") != null) {
            region = System.getenv("AWS_REGION");
        } else {
            region = EU_WEST_1.getName();
        }
        instance = new MovieRepository(dynamoEndpointUrl);
        return instance;
    }

    private MovieRepository(String dynamoEndpointUrl) {
        if (dynamoEndpointUrl != null) {
            initLocalDynamoDb(dynamoEndpointUrl);
        } else {
            initDynamoDb();
        }
    }

    private void initDynamoDb() {
        log.debug("initDynamoDb");
        DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder().build();
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(region).build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dbMapperConfig);
    }

    private void initLocalDynamoDb(String dynamoEndpointUrl) {
        log.debug("initLocalDynamoDb");
        DynamoDBMapperConfig dbMapperConfig = new DynamoDBMapperConfig.Builder().build();
        amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, region)).build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dbMapperConfig);
        log.debug("creating Movie table for local Dynamo DB");
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(Movie.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));
        createTableIfNotExists(amazonDynamoDB, createTableRequest);
    }

    public Movie load(String id) {
        log.debug("load {}", id);
        return dynamoDBMapper.load(new Movie(id, null, null, 0));
    }

    public void saveOrUpdate(Movie movie) {
        log.debug("saveOrUpdate {}", movie);
        dynamoDBMapper.save(movie);
    }

    public void delete(Movie movie) {
        log.debug("delete {}", movie);
        dynamoDBMapper.delete(movie);
    }

    public List<Movie> list() {
        log.debug("list");
        return dynamoDBMapper.scan(Movie.class, new DynamoDBScanExpression());
    }
}
