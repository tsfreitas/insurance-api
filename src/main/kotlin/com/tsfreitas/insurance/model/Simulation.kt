package com.tsfreitas.insurance.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern


private const val MIN_SCORE = 0
private const val MAX_SCORE = 10
private const val MIN_GOOD_SCORE = 3
private const val MIN_REGULAR_SCORE = 1
private const val MAX_REGULAR_SCORE = 2


data class SimulationResponse(
    val auto: String,
    val disability: String,
    val home: String,
    val life: String
) {

    companion object Factory {
        fun create(
            auto: Int,
            disability: Int,
            home: Int,
            life: Int
        ): SimulationResponse {

            return SimulationResponse(
                auto = translate(auto),
                disability = translate(disability),
                home = translate(home),
                life = translate(life)
            )
        }

        private fun translate(value: Int) = when {
                value <= MIN_SCORE -> "economic"
                value in MIN_REGULAR_SCORE..MAX_REGULAR_SCORE -> "regular"
                value in MIN_GOOD_SCORE..MAX_SCORE -> "responsible"
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
    @field:Pattern(
        regexp = "single|married",
        message = "Field 'marital_status' must have the values 'SINGLE' or 'MARRIED'",
        flags = [Pattern.Flag.CASE_INSENSITIVE]
    )
    val maritalStatus: String?,

    @field:NotNull(message = "Field 'risk_questions' is required")
    val riskQuestions: List<Boolean>?,

    @field:Valid
    val vehicle: Vehicle? = null
) {

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    data class House(
        @field:NotNull(message = "Field 'house.ownership_status' is required")
        @field:Pattern(
            regexp = "owned|mortgaged",
            message = "Field 'house.ownership_status' must have the values 'OWNED' or 'MORTGAGED'",
            flags = [Pattern.Flag.CASE_INSENSITIVE]
        )
        val ownershipStatus: String?
    )

    data class Vehicle(
        @field:NotNull(message = "Field 'vehicle.year' is required")
        @field:Min(1, message = "Field 'vehicle.year' must be greater than 0")
        val year: Int?
    )

    enum class MaritalStatus {
        SINGLE, MARRIED
    }

    enum class HouseStatus {
        OWNED, MORTGAGED
    }
}
