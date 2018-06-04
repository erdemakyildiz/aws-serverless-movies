package be.houbrechts.it.awsserverless;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.amazonaws.regions.Regions.EU_WEST_1;

/**
 * @author IHoubr
 */
public class RatingRepository {
    private static String region;
    private static RatingRepository instance;
    private static final Logger log = LogManager.getLogger(RatingRepository.class);

    @Getter
    private AmazonDynamoDB amazonDynamoDB;
    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public static RatingRepository getInstance() {
        return RatingRepository.getInstance(null);
    }

    public static RatingRepository getInstance(String dynamoEndpointUrl) {
        if (instance != null) {
            return instance;
        }

        if (System.getenv("AWS_REGION") != null) {
            region = System.getenv("AWS_REGION");
        } else {
            region = EU_WEST_1.getName();
        }
        instance = new RatingRepository(dynamoEndpointUrl);
        return instance;
    }

    private RatingRepository(String dynamoEndpointUrl) {
        DynamoDBMapperConfig dbMapperConfig =
                new DynamoDBMapperConfig.Builder().build();

        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        if (dynamoEndpointUrl != null) {
            log.info("dynamoEndpointUrl: {}", dynamoEndpointUrl);
            clientBuilder = clientBuilder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(dynamoEndpointUrl, region));
        } else {
            clientBuilder = clientBuilder.withRegion(region);
        }
        amazonDynamoDB = clientBuilder.build();
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, dbMapperConfig);
    }

    public Rating load(String movieId, String email) {
        log.debug("load {} {}", movieId, email);
        return dynamoDBMapper.load(new Rating(movieId, email, 0));
    }

    public void saveOrUpdate(Rating rating) {
        log.debug("saveOrUpdate {}", rating);
        dynamoDBMapper.save(rating);
    }

    public void delete(Rating rating) {
        log.debug("delete {}", rating);
        dynamoDBMapper.delete(rating);
    }

    public List<Rating> listByMovieId(String movieId) {
        log.debug("list");
        final DynamoDBQueryExpression<Rating> queryExpression = new DynamoDBQueryExpression<Rating>()
                .withHashKeyValues(new Rating(movieId, null, 0));
        return dynamoDBMapper.query(Rating.class, queryExpression);
    }

    public List<Rating> list() {
        log.debug("list");
        return dynamoDBMapper.scan(Rating.class, new DynamoDBScanExpression());
    }
}
