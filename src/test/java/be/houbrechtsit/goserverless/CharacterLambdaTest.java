package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.util.IOUtils;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class CharacterLambdaTest {

    private static DynamoTestUtils dynamoTestUtils = DynamoTestUtils.getInstance();
    private static CharacterRepository characterRepository = CharacterRepository.geInstance();
    private static final long DEFAULT_EPOCH_MILLIS = 123;
    private static final long MILLIS_1_DAY = 24*60*60*1000;

    private Gson gson = new Gson();

    private CharacterAPILambda characterAPILambda = new CharacterAPILambda();

    private Context context;

    @BeforeClass
    public static void startDynamo() throws Exception {
        dynamoTestUtils.startDynamo();
        dynamoTestUtils.createTable(Character.class);
    }

    @Before
    public void cleanDB() {
        dynamoTestUtils.clearTable(Character.class);
        apiGatewayRequest = mock(ApiGatewayRequest.class);
        context = mock(Context.class);
        Map<String, Object> pathParameters = new HashMap<>();
        pathParameters.put(CharacterAPILambda.SHIPMENT_REFERENCE_KEY, "shipment_reference");
        pathParameters.put(CharacterAPILambda.CREATION_DATETIME_KEY, "" + DEFAULT_EPOCH_MILLIS);

        when(apiGatewayRequest.getPathParameters()).thenReturn(pathParameters);
    }

    @Test
    public void testPost() throws Exception {
        String jsonInput = IOUtils.toString(this.getClass().getResourceAsStream("/com/nike/emeasc/rebooking/rebooking_input.json"));
        Character expectedCharacter = gson.fromJson(jsonInput, Character.class);
        expectedCharacter.setExpirationDate(Date.from(Instant.ofEpochMilli(DEFAULT_EPOCH_MILLIS)));
        Map<String, Object> bodyMap = gson.fromJson(jsonInput, Map.class);

        when(apiGatewayRequest.getBody()).thenReturn(bodyMap);
        when(apiGatewayRequest.getHttpMethod()).thenReturn("POST");

        ProxyResponse proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        Character foundCharacter = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS));

        assertEquals(expectedCharacter, foundCharacter);
        assertNotNull(foundCharacter.getExpirationDate());

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void testDelete() throws Exception {
        String jsonInput = IOUtils.toString(this.getClass().getResourceAsStream("/com/nike/emeasc/rebooking/rebooking_input.json"));
        Character expectedCharacter = gson.fromJson(jsonInput, Character.class);
        Map<String, Object> bodyMap = gson.fromJson(jsonInput, Map.class);

        when(apiGatewayRequest.getBody()).thenReturn(bodyMap, Collections.EMPTY_MAP);
        when(apiGatewayRequest.getHttpMethod()).thenReturn("POST", "DELETE");
        when(apiGatewayRequest.getPath()).thenReturn("http://HOST:PORT/baselocation/shipment_reference/123/");

        ProxyResponse proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        Character foundCharacter = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS));

        assertEquals(expectedCharacter, foundCharacter);

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_OK);

        proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        foundCharacter = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS));

        assertNull(foundCharacter);

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void testGet() throws Exception {
        String jsonInput = IOUtils.toString(this.getClass().getResourceAsStream("/com/nike/emeasc/rebooking/rebooking_input.json"));
        Map<String, Object> bodyMap = gson.fromJson(jsonInput, Map.class);

        when(apiGatewayRequest.getBody()).thenReturn(bodyMap, Collections.EMPTY_MAP);
        when(apiGatewayRequest.getHttpMethod()).thenReturn("POST", "GET");
        when(apiGatewayRequest.getPath()).thenReturn("http://HOST:PORT/baselocation/shipment_reference/123/");

        ProxyResponse proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        Character foundCharacter = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS));

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_OK);

        proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        Character characterFromGet = gson.fromJson(proxyResponse.getBody(), Character.class);

        assertEquals(foundCharacter.getShipmentReference(), characterFromGet.getShipmentReference());
        assertEquals(foundCharacter.getComment(), characterFromGet.getComment());

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void testGet_All() throws Exception {
        String jsonInput = IOUtils.toString(this.getClass().getResourceAsStream("/com/nike/emeasc/rebooking/rebooking_input.json"));
        String jsonInput2 = IOUtils.toString(this.getClass().getResourceAsStream("/com/nike/emeasc/rebooking/rebooking_input_2.json"));

        Character character1 = gson.fromJson(jsonInput, Character.class);
        Character character2 = gson.fromJson(jsonInput2, Character.class);

        characterRepository.saveOrUpdate(character1);
        characterRepository.saveOrUpdate(character2);

        Map<String, Object> pathParameters = new HashMap<>();
        pathParameters.put(CharacterAPILambda.SHIPMENT_REFERENCE_KEY, "shipment_reference");

        when(apiGatewayRequest.getPathParameters()).thenReturn(pathParameters);
        when(apiGatewayRequest.getBody()).thenReturn(Collections.EMPTY_MAP);
        when(apiGatewayRequest.getHttpMethod()).thenReturn("GET");
        when(apiGatewayRequest.getPath()).thenReturn("http://HOST:PORT/baselocation/shipment_reference/");

        Character foundCharacter1 = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS));
        Character foundCharacter2 = characterRepository.load("shipment_reference", new Date(DEFAULT_EPOCH_MILLIS+MILLIS_1_DAY));

        assertNotNull(foundCharacter1);
        assertNotNull(foundCharacter2);

        // Get all
        ProxyResponse proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        List<Character> rebookingsFromGet = gson.fromJson(proxyResponse.getBody(), List.class);

        assertEquals(rebookingsFromGet.size(), 2);

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_OK);
    }

    @Test
    public void testGet_NoContent() throws Exception {
        when(apiGatewayRequest.getBody()).thenReturn(Collections.EMPTY_MAP);
        when(apiGatewayRequest.getHttpMethod()).thenReturn("GET");
        when(apiGatewayRequest.getPath()).thenReturn("http://HOST:PORT/baselocation/shipment_reference/123/");

        ProxyResponse proxyResponse = characterAPILambda.handleApiGatewayRequest(apiGatewayRequest, context);

        Character characterFromGet = gson.fromJson(proxyResponse.getBody(), Character.class);

        assertNull(characterFromGet);

        assertEquals(proxyResponse.getStatusCode(), HttpStatus.SC_NO_CONTENT);
    }
}