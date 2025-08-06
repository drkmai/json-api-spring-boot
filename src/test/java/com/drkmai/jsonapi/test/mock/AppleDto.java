package com.drkmai.jsonapi.test.mock;

import com.drkmai.jsonapi.annotation.JsonApiObject;
import com.drkmai.jsonapi.annotation.JsonApiProperty;
import com.drkmai.jsonapi.generator.JsonApiDtoExtendable;

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
