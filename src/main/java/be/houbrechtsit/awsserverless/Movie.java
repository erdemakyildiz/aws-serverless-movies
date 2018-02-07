package be.houbrechtsit.awsserverless;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

/**
 * @author IHoubr
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "movies")
@ToString()
public class Movie {

    @DynamoDBHashKey
    private String title;
    private String director;
    private int year;
}
