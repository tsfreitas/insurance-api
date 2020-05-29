# insurance-api project

This project is built in Kotlin with Quarkus Framework.

To run this project is necessary to have Java 11 or greater.

## Running the application in dev mode

You can run this application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

To run unit tests, use the command:

```
./gradlew test
```

## Packaging and running the application

This application was package using `./gradlew quarkusBuild --uber-jar`

The jar was commited, to simplify the application tests and validation, at `/output/insurance-api.jar`.

To run the application production version use the command: 

`java -jar output/insurance-api.jar`


## Application test urls

The application runs at 8080 port and it is possible to test it with cURL:

```shell script
curl -XPOST -H 'Content-Type: application/json' http://localhost:8080/risk/simulate -d @- << 'EOF'
{
  "age": 35,
  "dependents": 2,
  "house": {"ownership_status": "owned"},
  "income": 0,
  "marital_status": "married",
  "risk_questions": [0, 1, 0],
  "vehicle": {"year": 2018}
}
EOF

```