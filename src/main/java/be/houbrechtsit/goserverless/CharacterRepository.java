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
public class CharacterRepository {
    private static CharacterRepository instance;

    @Getter
    private AmazonDynamoDB amazonDynamoDB;
    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public static CharacterRepository geInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new CharacterRepository();
        return instance;
    }

    private CharacterRepository() {
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

    public Character load(String name) {
        DynamoDBQueryExpression<Character> query = new DynamoDBQueryExpression<Character>();
        query.setHashKeyValues(new Character(name, null));
        PaginatedQueryList<Character> characters = dynamoDBMapper.query(Character.class, query);
        return characters.isEmpty() ? null : characters.get(0);
    }

    public void saveOrUpdate(Character character) {
        dynamoDBMapper.save(character);
    }

    public void delete(Character character) {
        dynamoDBMapper.delete(character);
    }

    public List<Character> list() {
        DynamoDBQueryExpression<Character> query = new DynamoDBQueryExpression<Character>();
        return dynamoDBMapper.scan(Character.class, new DynamoDBScanExpression());
    }
}
