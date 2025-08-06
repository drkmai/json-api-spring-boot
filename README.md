[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.drkmai.jsonapi/json-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.drkmai.jsonapi/json-api)
[![Coverage Status](https://coveralls.io/repos/github/drkmai/json-api-spring-boot/badge.svg?branch=master)](https://coveralls.io/github/drkmai/json-api-spring-boot?branch=master)

# json-api-spring-boot

This project converts a normal Java object to [json:api](https://jsonapi.org/) standard.

This project is a Fork from [MieskeB's JSON:API project](https://github.com/MieskeB/json-api-spring-boot/)

---

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.drkmai.jsonapi</groupId>
    <artifactId>json-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.drkmai.jsonapi:json-api:1.0.0'
```


---

## Usage

### 1. Defining your models

Models must include getters and setters:

```java
public class User {
    private String id;
    private String username;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
```

> Tip: Use Lombok (`@Getter`, `@Setter`) or your IDE to generate boilerplate code.

### 2. Adding annotations

Extend the base class to handle IDs and links:

```java
import generator.com.derekmai.jsonapi.JsonApiDtoExtendable;

public class User extends JsonApiDtoExtendable {
    // ...
}
```

#### Available Annotations

| Annotation       | Description                   | Mandatory | Covered by JsonApiDtoExtendable |
| ---------------- | ----------------------------- | --------- | ------------------------------- |
| `@JsonApiObject`   | Marks the main object          | Yes       | No                              |
| `@JsonApiId`       | Specifies ID field             | Yes       | Yes                             |
| `@JsonApiProperty` | Regular property               | No        | No                              |
| `@JsonApiLink`     | Link to self/related URLs      | No        | Yes                             |
| `@JsonApiRelation` | Relationship to another object | No        | No                              |

#### Example

```java
import annotation.com.derekmai.jsonapi.JsonApiObject;
import annotation.com.derekmai.jsonapi.JsonApiProperty;
import generator.com.derekmai.jsonapi.JsonApiDtoExtendable;

@JsonApiObject("User")
public class User extends JsonApiDtoExtendable {
    @JsonApiProperty
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

### 3. Adding Relations

```java
@JsonApiRelation("owner")
private Box boxOwner;
```

The `Box` class must also be annotated with `@JsonApiObject("box")` and have an ID.

### 4. Adding Links

Call `generate()` to set self and collection URIs:

```java
public User(String id, String username) {
    this.setId(id);
    this.setUsername(username);
    this.generate("/user", "/users");
}
```

To override default base URL (`http://localhost:8080`), set env or config:

```
jsonapi.baseurl=http://localhost:8081
```

#### Output Example

**Single object**:

```json
{
  "data": {
    "attributes": {
      "username": "the username"
    },
    "id": "the id",
    "type": "User"
  },
  "links": {
    "self": "http://localhost:8080/user/the+id"
  }
}
```

**List of objects**:

```json
{
  "data": [
    {
      "attributes": {
        "username": "the username 1"
      },
      "id": "the id 1",
      "type": "User"
    },
    {
      "attributes": {
        "name": "the username 2"
      },
      "id": "the id 2",
      "type": "User"
    }
  ],
  "links": {
    "self": "http://localhost:8080/users"
  }
}
```

### 5. Converting to JSON string

```java
import parser.com.derekmai.jsonapi.JsonApiConverter;

String result = JsonApiConverter.convert(user);
```

To increase relation depth:

```java
String result = JsonApiConverter.convert(user, 2);
```

Or via environment/config:

```
jsonapi.depth=2
```

---

## Conclusion

This library aims to simplify JSON:API conversions in Java/Spring projects. If you encounter any issues, feel free to open an [issue](https://github.com/drkmai/json-api-spring-boot/issues) or submit a PR!

More examples available in: `src/main/java/com/drkmai/jsonapi/test`