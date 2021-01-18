package com.tsfreitas.insurance.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

data class SimulationResponse(
        val auto: String,
        val disability: String,
        val home: String,
        val life: String) {

    companion object Factory {
        fun create(auto: Int,
                   disability: Int,
                   home: Int,
                   life: Int): SimulationResponse {

            return SimulationResponse(
                    auto = translate(auto),
                    disability = translate(disability),
                    home = translate(home),
                    life = translate(life))
        }

        private fun translate(value: Int) = when {
            value <= 0 -> "economic"
            value in 1..2 -> "regular"
            value in 3..10 -> "responsible"
            else -> "ineligible"
        }
    }


}


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class SimulationRequest(
        @field:NotNull(message = "Field 'age' is required")
        @field:Min(0, message = "Field 'age' must have value equal or greater than 0")
        val age: Int?,

        @field:NotNull(message = "Field 'dependents' is required")
        @field:Min(0, message = "Field 'dependents' must have value equal or greater than 0")
        val dependents: Int?,

        @field:Valid
        val house: House? = null,

        @field:NotNull(message = "Field 'income' is required")
        @field:Min(0, message = "Field 'income' must have value equal or greater than 0")
        val income: Int?,

        @field:NotNull(message = "Field 'marital_status' is required")
        @field:Pattern(regexp = "single|married", message = "Field 'marital_status' must have the values 'SINGLE' or 'MARRIED'", flags = [Pattern.Flag.CASE_INSENSITIVE])
        val maritalStatus: String?,

        @field:NotNull(message = "Field 'risk_questions' is required")
        val riskQuestions: List<Boolean>?,

        @field:Valid
        val vehicle: Vehicle? = null
) {

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    data class House(
            @field:NotNull(message = "Field 'house.ownership_status' is required")
            @field:Pattern(regexp = "owned|mortgaged", message = "Field 'house.ownership_status' must have the values 'OWNED' or 'MORTGAGED'", flags = [Pattern.Flag.CASE_INSENSITIVE])
            val ownershipStatus: String?
    )

    data class Vehicle(
            @field:NotNull(message = "Field 'vehicle.year' is required")
            @field:Min(1, message = "Field 'vehicle.year' must be greater than 0")
            val year: Int?)

    enum class MaritalStatus {
        single, married
    }

    enum class HouseStatus {
        owned, mortgaged
    }
}