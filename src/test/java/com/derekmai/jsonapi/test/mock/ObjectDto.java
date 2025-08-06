package com.derekmai.jsonapi.test.mock;

import com.derekmai.jsonapi.annotation.JsonApiObject;
import com.derekmai.jsonapi.annotation.JsonApiProperty;
import com.derekmai.jsonapi.annotation.JsonApiRelation;
import com.derekmai.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("Object")
public class ObjectDto extends JsonApiDtoExtendable implements Cloneable {
    @JsonApiProperty
    private String name;

    @JsonApiRelation("Owner")
    private UserDto owner;

    @JsonApiRelation("Apple")
    private AppleDto apple;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public AppleDto getApple() {
        return apple;
    }

    public void setApple(AppleDto apple) {
        this.apple = apple;
    }
}
