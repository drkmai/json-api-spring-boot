package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class RelationTest {

    private MockDataGenerator generator;

    private ObjectDto objectDto;
    private UserDto userDto;

    @Before
    public void before() throws CloneNotSupportedException {
        this.generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
        this.userDto = (UserDto) generator.getUserDto().clone();
    }

    @Test
    public void testIfRelationshipExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships"));
    }

    @Test
    public void testIfRelationshipCanBeNull() {
        objectDto.setOwner(null);
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertThrows(JSONException.class, () -> jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner"));
        objectDto = this.generator.getObjectDto();
    }

    @Test
    public void testIfRelationshipOwnerExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner"));
    }

    @Test
    public void testIfRelationshipOwnerDataExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data"));
    }

    @Test
    public void testIfRelationshipOwnerDataIdWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals(objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("id"));
    }

    @Test
    public void testIfRelationshipOwnerDataTypeWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("User", jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("data").getString("type"));
    }

    @Test
    public void testIfRelationshipOwnerLinksExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links"));
    }

    @Test
    public void testIfRelationshipOwnerLinksSelfWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/user/" + objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("self"));
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfRelationshipOwnerLinksRelatedWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + objectDto.getId() + "/user/" + objectDto.getOwner().getId(), jsonObject.getJSONObject("data").getJSONObject("relationships").getJSONObject("Owner").getJSONObject("links").getString("related"));
    }

    @Test
    public void testNoDuplicatesInRelationship() {
        // Arrange: Get the user and force duplicate in to-many relationship
        UserDto user = generator.getUserDto();
        List<ObjectDto> childObjects = new ArrayList<>();
        ObjectDto child1 = user.getChildObjects().get(0);
        childObjects.add(child1);
        childObjects.add(child1); // Intentional duplicate
        user.setChildObjects(childObjects);

        String jsonString = JsonApiConverter.convert(user, 1);
        JSONObject json = new JSONObject(jsonString);

        // Act: Extract the relationship data for childObjects
        JSONObject data = json.getJSONObject("data");
        JSONObject relationships = data.getJSONObject("relationships");
        JSONObject childRel = relationships.getJSONObject("childObjects");
        JSONArray relData = childRel.getJSONArray("data");

        // Assert: Only one unique entry in data array
        assertEquals("Relationship data should have no duplicates", 1, relData.length());

        // Additional check for uniqueness
        Set<String> typeIdSet = new HashSet<>();
        for (int i = 0; i < relData.length(); i++) {
            JSONObject obj = relData.getJSONObject(i);
            String type = obj.getString("type");
            String id = obj.getString("id");
            String key = type + ":" + id;
            assertFalse("Duplicate in relationship data: " + key, typeIdSet.contains(key));
            typeIdSet.add(key);
        }
    }
}
