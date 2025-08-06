package com.derekmai.jsonapi.test.mock;

import com.derekmai.jsonapi.annotation.JsonApiObject;
import com.derekmai.jsonapi.annotation.JsonApiProperty;
import com.derekmai.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("Object")
public class AppleDto extends JsonApiDtoExtendable implements Cloneable {
    @JsonApiProperty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
