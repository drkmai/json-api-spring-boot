package com.derekmai.jsonapi.parser;

import com.derekmai.jsonapi.annotation.JsonApiProperty;
import com.derekmai.jsonapi.helper.GetterAndSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Field;

class AttributesParser {

    /**
     * This method should return all @JsonApiProperty annotated properties in one object
     *
     * @param object the object to be converted
     * @return the json of only the attributes
     */
    ObjectNode parse(Object object, ObjectMapper mapper) {
        ObjectNode jsonObject = mapper.createObjectNode();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonApiProperty.class)) {
                Object value = GetterAndSetter.callGetter(object, field.getName());
                jsonObject.set(field.getName(), mapper.valueToTree(value));
            }
        }
        return jsonObject;
    }
}