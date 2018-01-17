package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import be.houbrechtsit.goserverless.exception.LambdaRoutingException;
import be.houbrechtsit.goserverless.util.GsonFactory;
import com.nike.troa.lambda.rest.ApiGatewayProxyLambda;
import com.nike.troa.lambda.rest.ApiGatewayRequest;
import com.nike.troa.lambda.rest.ProxyResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterAPILambda extends ApiGatewayProxyLambda {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterAPILambda.class);
    public static final String SHIPMENT_REFERENCE_KEY = "shipmentref";
    public static final String CREATION_DATETIME_KEY = "creationdate";

    private Character character;
    private Gson gson = GsonFactory.getInstance().getGson();
    private CharacterRepository characterRepository = CharacterRepository.geInstance();

    @Override
    protected ProxyResponse handleApiGatewayRequest(final ApiGatewayRequest proxyRequest, final Context context) {
        logEnvironment();

        ProxyResponse response = invoke(proxyRequest);

        logRequest(proxyRequest);

        Map<String, Object> headers = new HashMap<>();
        return response.setHeaders(headers);
    }

    public ProxyResponse invoke(final ApiGatewayRequest proxyRequest) {
        int statusCode = HttpStatus.SC_OK;
        try {
            this.character = extractRebooking(proxyRequest);
        } catch (Throwable t) {
            LOGGER.error("Problem during extraction of Character from proxyRequest", t);
            return new ProxyResponse(HttpStatus.SC_BAD_REQUEST).setBody(t.toString());
        }
        switch (proxyRequest.getHttpMethod()) {
            case "POST":
                String retentionDays = LambdaEnvironment.getRetentionDays();
                Instant instant = Instant.now().plus(Duration.ofDays(Long.parseLong(retentionDays)));
                this.character.setExpirationDate(Date.from(instant));
                characterRepository.saveOrUpdate(this.character);
                break;
            case "GET":
                if (this.character.getCreationDateTime() != null) {
                    this.character = characterRepository.load(this.character);
                    if (this.character == null) {
                        statusCode = HttpStatus.SC_NO_CONTENT;
                    }
                } else {
                    List<Character> characterList = characterRepository.load(this.character.getShipmentReference());
                    return new ProxyResponse(characterList.size() > 0 ? statusCode : HttpStatus.SC_NO_CONTENT).setBody(gson.toJson(characterList));
                }
                break;
            case "DELETE":
                characterRepository.delete(this.character);
                statusCode = HttpStatus.SC_NO_CONTENT;
                break;
            default:
                throw new LambdaRoutingException("Unsupported method");
        }
        return new ProxyResponse(statusCode).setBody(gson.toJson(this.character));
    }

    private Character extractRebooking(final ApiGatewayRequest proxyRequest) {
        String body = gson.toJson(proxyRequest.getBody());
        Character incomingCharacter;
        if (StringUtils.isNullOrEmpty(body)) {
            incomingCharacter = new Character();
        } else {
            incomingCharacter = gson.fromJson(body, Character.class);
        }
        Map<String, Object> pathParameters = proxyRequest.getPathParameters();
        incomingCharacter.setShipmentReference((String)pathParameters.get(SHIPMENT_REFERENCE_KEY));
        Object creationDateTimeObject = pathParameters.get(CREATION_DATETIME_KEY);
        if (creationDateTimeObject != null) {
            incomingCharacter.setCreationDateTime(Date.from(Instant.ofEpochMilli(Long.parseLong((String) creationDateTimeObject))));
        }
        return incomingCharacter;
    }

    private void logEnvironment() {
        LOGGER.info("AWS Region: {}", LambdaEnvironment.getAwsRegion());
        LOGGER.info("DynamoDB table name prefix: {}", LambdaEnvironment.getTableNamePrefix());
    }

    private void logRequest(final ApiGatewayRequest proxyRequest) {
        if (proxyRequest != null) {
            LOGGER.info("Request model is: " + proxyRequest.getRequestModel().toString());
            LOGGER.info(proxyRequest.getBody().toString());
            LOGGER.info(proxyRequest.getResourceName());
            LOGGER.info(proxyRequest.getPath());
            LOGGER.info(proxyRequest.getHttpMethod());
            LOGGER.info(proxyRequest.getHeaders().toString());
            LOGGER.info(proxyRequest.getQueryStringParameters().toString());
            LOGGER.info(proxyRequest.getPathParameters().toString());
            LOGGER.info(proxyRequest.getRequestId());
            LOGGER.info(proxyRequest.getApiId());
            LOGGER.info(proxyRequest.getIdentity().toString());
            LOGGER.info(proxyRequest.getRequestModel().toString());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class RebookingIdentifier {
        private String shipmentReference;
        private Date creationDate;
    }
}