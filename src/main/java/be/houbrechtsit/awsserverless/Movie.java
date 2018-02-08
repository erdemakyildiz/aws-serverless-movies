package be.houbrechtsit.awsserverless;

import lombok.*;

import java.util.UUID;

/**
 * @author IHoubr
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@DynamoDBTable(tableName = "movies")
@ToString()
public class Movie {

    //    @DynamoDBHashKey
    private String uuid = UUID.randomUUID().toString();
    private String title;
    private String director;
    private int year;
}
