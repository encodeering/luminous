# Luminous - Partner Service

### Protocol

Another protocol implementation of an API streaming instrument and quote data over plain web sockets. The protocol itself is rather hard/tricky to implement, as both data collections are streamed without additional timestamp information.
Clients would have to use the message ingestion time and to make proper assumptions about the service and delivery constraints. The volume on both streams is not equal either and instruments can further be re-cycled, meaning, that received quotes between a DELETE and (RE-)ADD of an instrument could belong to any of the surrounding instruments.
The protocol does also not implement a re-conciliation process or a redelivery strategy.

### API

The service opens a port locally on `8080` and streams instruments and quotes homogeneously on two dedicate data endpoints, using the following payload structure:

```
ws://localhost:8080/instruments

{
    "type" : String | Any of (ADD, DELETE)
    "data" : Object | {
        "isin"        : String | Example "DE000BAY0017"
        "description" : String | Example "ut labore et dolore magna aliquyam erat"
        "stamp"       : String | Example "2007-08-31T14:46:16.042370100Z" (ISO 8601)
    }
}


ws://localhost:8080/quotes

{
    "type" : String | Any of (ADD)
    "data" : Object | {
        "isin"  : String | Example "DE000BAY0017"
        "price" : Number | Example 52.63
        "stamp" : String | Example "2007-08-31T14:46:16.042370100Z" (ISO 8601)
    }
}
```

#### Note

This implementation

+ emits all instrument information as soon as a connection to the instrument endpoint is established.
+ provides timestamp information in a chronologically correct order, but the home task itself might not have been adapted yet. The format is therefore compatible, if unknown keys are ignored.
