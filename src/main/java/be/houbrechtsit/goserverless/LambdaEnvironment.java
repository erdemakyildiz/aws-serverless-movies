package be.houbrechtsit.goserverless;

import com.amazonaws.regions.Regions;

/**
 * @author IHoubr
 */
public final class LambdaEnvironment {

    public static final String REGION_PROPERTY = "REGION";
    public static final String TABLE_NAME_PREFIX_PROPERTY = "TABLE_NAME_PREFIX";
    public static final String DYNAMO_ENDPOINT_URL_PROPERTY = "DYNAMO_ENDPOINT_URL";


    public static Regions getAwsRegion() {
        return Regions.fromName(getAwsRegionName());
    }

    public static String getAwsRegionName() {
        return System.getenv(REGION_PROPERTY);
    }

    public static String getTableNamePrefix() {
        return System.getenv(TABLE_NAME_PREFIX_PROPERTY);
    }

    public static String getDynamoEndpointUrl() {
        return System.getenv(DYNAMO_ENDPOINT_URL_PROPERTY);
    }
}