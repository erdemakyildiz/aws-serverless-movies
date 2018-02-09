package be.houbrechts.it.awsserverless

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput

import static com.amazonaws.services.dynamodbv2.util.TableUtils.createTableIfNotExists

/**
 * @author IHoubr
 */
class LocalDynamoUtils {
    private final DynamoDB dynamoDB
    private final DynamoDBMapper dynamoDBMapper
    private final AmazonDynamoDB amazonDynamoDB

    private static LocalDynamoUtils instance
    private static int port
    private static boolean started = false

    static LocalDynamoUtils getInstance() {
        if (instance != null) {
            return instance
        }

        def socket = new ServerSocket(0)
        port = socket.getLocalPort()
        socket.close()

        def movieRepository = MovieRepository.getInstance("http://localhost:$port")
        instance = new LocalDynamoUtils(movieRepository.amazonDynamoDB, movieRepository.dynamoDBMapper)
        return instance
    }

    void startDynamo() {
        if (!started) {
            try {
                ServerRunner.main("-inMemory", "-port", port.toString())
                started = true
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    private LocalDynamoUtils(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        this.dynamoDB = new DynamoDB(amazonDynamoDB)
        this.dynamoDBMapper = dynamoDBMapper
        this.amazonDynamoDB = amazonDynamoDB
    }

    void createTable(Class persistentClass) {
        createTableIfNotExists(amazonDynamoDB, createTableRequest(persistentClass))
    }

    private CreateTableRequest createTableRequest(Class persistentClass) {
        return dynamoDBMapper.generateCreateTableRequest(persistentClass)
                .withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L))
    }


    @SuppressWarnings("unchecked")
    void clearTable(Class persistentClass) {
        dynamoDBMapper.scan(persistentClass, new DynamoDBScanExpression()).each { dynamoDBMapper.delete(it) }
    }
}
