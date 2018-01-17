package be.houbrechtsit.goserverless;

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
@DynamoDBTable(tableName = "persons")
@ToString()
public class Character {

    @DynamoDBHashKey
    private String name;

    private String series;
}
