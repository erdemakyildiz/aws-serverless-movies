package be.houbrechtsit.awsserverless;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

import java.io.IOException;
import java.net.ServerSocket;

import static be.houbrechtsit.awsserverless.MovieRepository.DYNAMO_ENDPOINT_URL_ENV_VARIABLE;

/**
 * @author IHoubr
 */
public class LocalDynamoUtils {
    private final DynamoDB dynamoDB;
    private final DynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;

    private static LocalDynamoUtils instance;
    private static int port;
    private static boolean started = false;

    public static LocalDynamoUtils getInstance() {
        if (instance != null) {
            return instance;
        }

        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty(DYNAMO_ENDPOINT_URL_ENV_VARIABLE, "http://localhost:" + port);

        MovieRepository movieRepository = MovieRepository.geInstance();
        instance = new LocalDynamoUtils(movieRepository.getAmazonDynamoDB(), movieRepository.getDynamoDBMapper());
        return instance;
    }

    public void startDynamo() {
        if (!started) {
            try {
                ServerRunner.main(new String[]{"-inMemory", "-port", String.valueOf(port)});
                started = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private LocalDynamoUtils(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        this.dynamoDB = new DynamoDB(amazonDynamoDB);
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public void createTable(Class persistentClass) {
        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest(persistentClass));
        dynamoDB.createTable(createTableRequest(persistentClass));
    }

    private CreateTableRequest createTableRequest(Class persistentClass) {
        return dynamoDBMapper.generateCreateTableRequest(persistentClass)
                .withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));
    }


    @SuppressWarnings("unchecked")
    public void clearTable(Class persistentClass) {
        dynamoDBMapper.scan(persistentClass, new DynamoDBScanExpression()).forEach(dynamoDBMapper::delete);
    }
}
