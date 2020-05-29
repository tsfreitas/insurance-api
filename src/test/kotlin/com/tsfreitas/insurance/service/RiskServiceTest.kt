package com.tsfreitas.insurance.service

import com.tsfreitas.insurance.model.SimulationRequest
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.enterprise.inject.Default
import javax.inject.Inject

@QuarkusTest
class RiskServiceTest {

    @Inject
    @field:Default
    lateinit var riskService: RiskService

    @Test
    fun userWithMoreThan60years() {
        // GIVEN
        val r = SimulationRequest(
                age = 62,
                dependents = 2,
                income = 200,
                maritalStatus = "married",
                vehicle = SimulationRequest.Vehicle(2017),
                riskQuestions = listOf(true, false, true),
                house = SimulationRequest.House(ownershipStatus = SimulationRequest.HouseStatus.owned.name)
        )

        // WHEN
        val response = riskService.calculateRisk(r)

        // THEN
        Assertions.assertEquals("ineligible", response.life)
        Assertions.assertEquals("ineligible", response.disability)
        Assertions.assertEquals("responsible", response.auto)
        Assertions.assertEquals("regular", response.home)
    }

    @Test
    fun youngAndSingleWithOldCarUser() {
        val r = SimulationRequest(
                age = 18,
                dependents = 0,
                income = 200,
                maritalStatus = "single",
                riskQuestions = listOf(true, false, true),
                vehicle = SimulationRequest.Vehicle(2009)
        )

        // WHEN
        val response = riskService.calculateRisk(r)

        // THEN
        Assertions.assertEquals("economic", response.auto)
        Assertions.assertEquals("economic", response.disability)
        Assertions.assertEquals("ineligible", response.home)
        Assertions.assertEquals("economic", response.life)
    }


    @Test
    fun marriedWithChildrenAndMortgagedHouse() {
        val r = SimulationRequest(
                age = 35,
                dependents = 2,
                income = 200,
                maritalStatus = SimulationRequest.MaritalStatus.married.name,
                riskQuestions = listOf(true, false, true),
                vehicle = SimulationRequest.Vehicle(2018),
                house = SimulationRequest.House(SimulationRequest.HouseStatus.mortgaged.name)
        )

        // WHEN
        val response = riskService.calculateRisk(r)

        // THEN
        Assertions.assertEquals("regular", response.auto)
        Assertions.assertEquals("regular", response.disability)
        Assertions.assertEquals("regular", response.home)
        Assertions.assertEquals("responsible", response.life)
    }

    @Test
    fun userFromExample() {
        val r = SimulationRequest(
                age = 35,
                dependents = 2,
                income = 0,
                maritalStatus = "married",
                riskQuestions = listOf(false, true, false),
                vehicle = SimulationRequest.Vehicle(2018),
                house = SimulationRequest.House(SimulationRequest.HouseStatus.owned.name)
        )

        // WHEN
        val response = riskService.calculateRisk(r)

        // THEN
        Assertions.assertEquals("regular", response.auto)
        Assertions.assertEquals("ineligible", response.disability)
        Assertions.assertEquals("economic", response.home)
        Assertions.assertEquals("regular", response.life)
    }

}