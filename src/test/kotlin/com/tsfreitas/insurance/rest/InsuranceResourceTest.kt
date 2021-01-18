package com.tsfreitas.insurance.rest

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

@QuarkusTest
class InsuranceResourceTest {

    val mapper = jacksonObjectMapper()

    val baseRequestBody = mapOf("age" to 35,
            "dependents" to 2,
            "house" to mapOf("ownership_status" to "owned"),
            "income" to 0,
            "marital_status" to "married",
            "risk_questions" to listOf(0, 1, 0),
            "vehicle" to mapOf("year" to 2018)
    )

    @Test
    fun shouldReturnSuccessDueCorrectRequest() {
        val expected = """
                {
                    "auto": "regular",
                    "disability": "ineligible",
                    "home": "economic",
                    "life": "regular"
                }
            """

        val response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(baseRequestBody)
                .`when`().post("/risk/simulate")
                .then()
                .statusCode(200)
                .extract().asString()

        val responseMap = mapper.readValue<Map<String, String>>(response)
        val expectedMap = mapper.readValue<Map<String, String>>(expected)

        assert(expectedMap.equals(responseMap))

    }

    @Test
    fun shouldReturnErrorDueValuesNotSent() {

        val withoutAgeAndDependents = baseRequestBody.toMap().minus("age").minus("dependents")

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(withoutAgeAndDependents)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(400)
                .body("details", Matchers.hasItems("Field 'dependents' is required", "Field 'age' is required"))
    }

    @Test
    fun shouldGetErrorsDuePayloadContentIncorrect() {

        val invalidValues = baseRequestBody.toMutableMap()
        invalidValues["age"] = -1
        invalidValues["dependents"] = -1
        invalidValues["income"] = -1
        invalidValues["marital_status"] = "none"

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidValues)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(400)
                .body("details", Matchers.containsInAnyOrder(
                        "Field 'age' must have value equal or greater than 0",
                        "Field 'dependents' must have value equal or greater than 0",
                        "Field 'income' must have value equal or greater than 0",
                        "Field 'marital_status' must have the values 'SINGLE' or 'MARRIED'")
                )
                .body("details.size()", Matchers.equalTo(4))
    }

    @Test
    fun houseFieldValidations() {

        val withoutHousePayload = baseRequestBody.toMutableMap()
        withoutHousePayload.remove("house")

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(withoutHousePayload)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(200)

        val wrongOwnershipStatus = baseRequestBody.toMutableMap()
        wrongOwnershipStatus["house"] = mapOf("ownership_status" to "otherThing")

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(wrongOwnershipStatus)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(400)
                .body("details", Matchers.containsInAnyOrder(
                        "Field 'house.ownership_status' must have the values 'OWNED' or 'MORTGAGED'")
                )
    }

    @Test
    fun vehicleFieldValidations() {

        val withoutVehiclePayload = baseRequestBody.toMutableMap()
        withoutVehiclePayload.remove("vehicle")

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(withoutVehiclePayload)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(200)

        val wrongOwnershipStatus = baseRequestBody.toMutableMap()
        wrongOwnershipStatus["vehicle"] = mapOf("year" to -10)

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(wrongOwnershipStatus)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(400)
                .body("details", Matchers.containsInAnyOrder(
                        "Field 'vehicle.year' must be greater than 0")
                )
    }


    @Test
    fun shouldGetErrorDuePayloadWithSeriousError() {
        val wrongPayload = baseRequestBody.toMutableMap()
        wrongPayload["risk_questions"] = listOf("a", 43, "!@")

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(wrongPayload)
                .`when`()
                .post("/risk/simulate")
                .then()
                .statusCode(400)
                .body("details", Matchers.containsInAnyOrder("Payload incorrect, please fix it")
                )
    }


}