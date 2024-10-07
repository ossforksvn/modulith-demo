# Modulith Demo

<figure>
<img src="./assets/images/ModulithDiagram.png" alt="Modulith" />
</figure>

# Architecture & Technology Stack

The purpose of this demo is to illustrate various architectural designs,
and the Apache frameworks/libraries/runtimes that help build them.

## Apache Karaf: Modulith Runtime

## Apache CXF: JAX-RS

### JAX-RS WebClient

## JavaFX

### Java Native GUI

# Build and run the demo.

## Server

``` bash
cd server
mvn clean install
```

## Client

``` bash
cd client
mvn clean install
```

## Test APIs

Verify Admin Service:

``` bash
curl -X GET -H "Content-type: application/json" -H "Accept: application/json" "http://127.0.0.1:8181/cxf/admin/status"
```

Verify Game Service:

``` bash
 curl -X GET -H "Content-type: application/json" -H "Accept: application/json" "http://127.0.0.1:8181/cxf/game/newGame"
```

Verify the message service:

``` bash
curl -X POST -H "Content-type: application/json" -H "Accept: application/json" "http://127.0.0.1:8181/cxf/game/sendGameMessage" --data "{\"gameMessage\":{\"gameId\":\"123\",\"message\":\
"test\"}}"
```

# Conclusions

# About the Authors

[Jamie
Goodyear](https://github.com/savoirtech/blogs/blob/main/authors/JamieGoodyear.md)

# Reaching Out

Please do not hesitate to reach out with questions and comments, here on
the Blog, or through the Savoir Technologies website at
<https://www.savoirtech.com>.

# With Thanks

Thank you to the Apache Karaf, CXF, ActiveMQ, and Camel communities.

\(c\) 2024 Savoir Technologies
