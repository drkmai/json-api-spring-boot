package nl.michelbijnen.jsonapi.parser;

import nl.michelbijnen.jsonapi.annotation.JsonApiId;
import nl.michelbijnen.jsonapi.annotation.JsonApiLink;
import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.annotation.JsonApiRelation;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.Field;
import java.util.Collection;

class RelationshipParser {
    private DataParser dataParser;
    private LinksParser linksParser;

    RelationshipParser() {
        this.dataParser = new DataParser();
        this.linksParser = new LinksParser();
    }

    /**
     * This class should add the name of the object and under it the following items:
     * <p>
     * links
     * data
     *
     * @param object
     * @return
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        ObjectNode jsonObject = mapper.createObjectNode();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiRelation.class)) {

                Object relationObject = GetterAndSetter.callGetter(object, field.getName());

                if (relationObject == null) {
                    continue;
                }

                ObjectNode relation;
                if (this.isList(relationObject)) {
                    relation = this.parseRelationshipAsList(object, field, mapper);
                } else {
                    relation = this.parseRelationshipAsObject(object, field, mapper);
                }
                if (relation.size() > 0)
                    jsonObject.set(field.getAnnotation(JsonApiRelation.class).value(), relation);
            }
        }
        return jsonObject;
    }

    private ObjectNode parseRelationshipAsObject(Object object, Field field, ObjectMapper mapper) {
        ObjectNode relationship = mapper.createObjectNode();

        Object relationObject = GetterAndSetter.callGetter(object, field.getName());

        ObjectNode linksParsed = this.linksParser.parse(relationObject, mapper);
        if (linksParsed.size() > 0)
            relationship.set("links", linksParsed);

        ObjectNode dataParsed = this.dataParser.parse(relationObject, true, mapper);
        if (dataParsed.size() > 0)
            relationship.set("data", dataParsed);

        return relationship;
    }

    private ObjectNode parseRelationshipAsList(Object object, Field field, ObjectMapper mapper) {
        ObjectNode relationship = mapper.createObjectNode();

        Collection<Object> relationObjectCollection = (Collection<Object>) GetterAndSetter.callGetter(object, field.getName());

        ObjectNode linksParsed = this.linksParser.parse(relationObjectCollection, mapper);
        if (linksParsed.size() > 0)
            relationship.set("links", linksParsed);

        ArrayNode dataForEach = mapper.createArrayNode();
        for (Object relationObject : relationObjectCollection) {
            ObjectNode dataObj = this.dataParser.parse(relationObject, true, mapper);
            if (!this.dataExistsInArray(dataForEach, dataObj)) {
                dataForEach.add(dataObj);
            }
        }
        if (dataForEach.size() > 0)
            relationship.set("data", dataForEach);

        return relationship;
    }

    private boolean dataExistsInArray(ArrayNode dataArray, ObjectNode newData) {
        String id = newData.get("id").asText();
        String type = newData.get("type").asText();
        for (JsonNode existing : dataArray) {
            if (existing.get("id").asText().equals(id) && existing.get("type").asText().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isList(Object object) {
        return Collection.class.isAssignableFrom(object.getClass());
    }
}
