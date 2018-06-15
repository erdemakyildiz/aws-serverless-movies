package be.houbrechts.it.awsserverless

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner

/**
 * @author IHoubr
 */
class LocalDynamoUtils {
    private DynamoDB dynamoDB
    private DynamoDBMapper dynamoDBMapper
    private AmazonDynamoDB amazonDynamoDB

    private static LocalDynamoUtils instance
    private static int port
    private static boolean started = false

    static LocalDynamoUtils getInstance() {
        if (instance != null) {
            return instance
        }

        instance = new LocalDynamoUtils()
        return instance
    }

    void init() {
        if (!port) {
            def socket = new ServerSocket(0)
            port = socket.getLocalPort()
            socket.close()
        }

        try {
            ServerRunner.main("-inMemory", "-port", port.toString())
            started = true
        } catch (Exception e) {
            e.printStackTrace()
        }

        def movieRepository = MovieRepository.getInstance("http://localhost:$port")
        this.dynamoDB = new DynamoDB(movieRepository.amazonDynamoDB)
        this.dynamoDBMapper = movieRepository.dynamoDBMapper
        this.amazonDynamoDB = movieRepository.amazonDynamoDB
    }

    private LocalDynamoUtils() {
    }

    @SuppressWarnings("unchecked")
    void clearTable(Class persistentClass) {
        dynamoDBMapper.scan(persistentClass, new DynamoDBScanExpression()).each { dynamoDBMapper.delete(it) }
    }
}
