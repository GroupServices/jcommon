A Jackson module for using ObjectMapper to serialize protobuf to JSON and
deserialize JSON string.

Forked from 
```
https://github.com/HubSpot/jackson-datatype-protobuf/
branch "jackson29-proto3"
```

Although protobuf version3 (proto3) has builtin support for JSON
serialization and deserialization (JsonFormat.{Printer, Parser}), the
POJO-protobuf mixed mode is not supported. E.g., if we have
```java
class Board {
    @JsonProperty
    int version;
    @JsonProperty
    TextBullet title;  // TextBullet is a protobuf-generated class.
}
```
Then the Board class cannot be serialized or deserialized by neither
vanilla ObjectMapper nor protobuf's JsonFormat.

This module is designed specifically to support this use case.

N.B. the implementation of this module sacrifices efficiency for less
intrusiveness.  For protobuf-struct fields embedded in POJO class,
serialization and deserialization involves a two-pass process, one by
Jackson's {JsonGenerator,JsonParser}, the other by protobuf's
JsonFormat.{Printer,Parser}.  Given this obvious inefficiency, this
class is intended only for transiting our data classes from POJO to
protobuf.

To use this module:
```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ProtobufModule());
```
