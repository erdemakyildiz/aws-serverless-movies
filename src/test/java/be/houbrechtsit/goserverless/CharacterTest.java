package be.houbrechtsit.goserverless;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author IHoubr
 */
public class CharacterTest {
    private static final String SHIPMENT_REFERENCE = "shipment_reference";
    private static final int DATE_AS_LONG = 123;
    
    private static DynamoTestUtils dynamoTestUtils = DynamoTestUtils.getInstance();
    private static CharacterRepository characterRepository = CharacterRepository.geInstance();

    @BeforeClass
    public static void startDynamo() throws Exception {
        dynamoTestUtils.startDynamo();
        dynamoTestUtils.createTable(Character.class);
    }

    @Before
    public void cleanDB() {
        dynamoTestUtils.clearTable(Character.class);
    }

    @Test
    public void testSaveRebooking() {
        Character character = createRebooking(SHIPMENT_REFERENCE, DATE_AS_LONG);
        assertEquals(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG)), character);
    }

    @Test
    public void testSaveMultipleRebookings() {
        Character character = createRebooking(SHIPMENT_REFERENCE, DATE_AS_LONG);
        Character character2 = createRebooking(SHIPMENT_REFERENCE, DATE_AS_LONG+1);
        assertEquals(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG)), character);
        assertEquals(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG+1)), character2);

        assertEquals(characterRepository.load(SHIPMENT_REFERENCE).size(), 2);
    }

    @Test
    public void testDeleteRebooking() {
        Character character = createRebooking(SHIPMENT_REFERENCE, DATE_AS_LONG);
        assertEquals(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG)), character);
        characterRepository.delete(character);
        assertNull(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG)));
    }

    @Test
    public void testUpdateRebooking() {
        Character character = createRebooking(SHIPMENT_REFERENCE, DATE_AS_LONG);
        character.setComment("Updated Comment");
        characterRepository.saveOrUpdate(character);
        assertEquals(characterRepository.load(SHIPMENT_REFERENCE, new Date(DATE_AS_LONG)).getComment(), "Updated Comment");
    }

    private Character createRebooking(String shipmentReference, long dateAsLong) {
        Character character = new Character(shipmentReference, new Date(dateAsLong));
        character.setAlignedWithCarrier(true);
        character.setShipToAddress("new address");
        characterRepository.saveOrUpdate(character);
        return character;
    }
}