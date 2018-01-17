package be.houbrechtsit.goserverless;

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

/**
 * @author IHoubr
 */
public class DynamoTestUtils {
    private final DynamoDB dynamoDB;
    private final DynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;

    private static int port;
    private static boolean started = false;

    public static DynamoTestUtils getInstance() {
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty(LambdaEnvironment.TABLE_REGION_PROPERTY, "eu-west-1");
        System.setProperty(LambdaEnvironment.TABLE_NAME_PREFIX_PROPERTY, "test_");
        System.setProperty(LambdaEnvironment.DYNAMO_ENDPOINT_URL_PROPERTY, "http://localhost:" + port);

        CharacterRepository characterRepository = CharacterRepository.geInstance();
        return new DynamoTestUtils(characterRepository.getAmazonDynamoDB(), characterRepository.getDynamoDBMapper());
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

    private DynamoTestUtils(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        this.dynamoDB = new DynamoDB(amazonDynamoDB);
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    public void createTable(Class persistentClass) {
        TableUtils.createTableIfNotExists(amazonDynamoDB, createTableRequest(persistentClass));
        //dynamoDB.createTable(createTableRequest(persistentClass));
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
