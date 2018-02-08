package be.houbrechts.it.awsserverless;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.UUID;

/**
 * @author IHoubr
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@DynamoDBTable(tableName = "movies")
public class Movie {

    @DynamoDBHashKey
    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String director;
    private int year;
}
