package be.houbrechts.it.awsserverless;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

/**
 * @author IHoubr
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "movies")
public class Movie {

    @DynamoDBHashKey
    private String id;
    private String title;
    private String director;
    private int year;
}
