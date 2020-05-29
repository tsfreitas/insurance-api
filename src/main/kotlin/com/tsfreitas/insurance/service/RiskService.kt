package com.tsfreitas.insurance.service

import com.tsfreitas.insurance.model.SimulationRequest
import com.tsfreitas.insurance.model.SimulationResponse
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class RiskService {

    fun calculateRisk(simulationRequest: SimulationRequest): SimulationResponse {

        val baseScore = simulationRequest.riskQuestions!!.map { it.toInt() }.sum()
        var score = Score(baseScore, baseScore, baseScore, baseScore)

        score = incomeRules(simulationRequest.income!!, score)
        score = ageRules(simulationRequest.age!!, score)
        score = houseRules(simulationRequest.house, score)
        score = dependentsRules(simulationRequest.dependents!!, score)
        score = marriageRules(simulationRequest.maritalStatus!!, score)
        score = vehicleRules(simulationRequest.vehicle, score)

        return SimulationResponse.create(score.auto, score.disability, score.home, score.life)
    }

    private fun vehicleRules(vehicle: SimulationRequest.Vehicle?, score: Score): Score {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return when {
            vehicle == null -> score.ineligible(InsuranceType.AUTO)
            (currentYear - vehicle.year!!) <= 5 -> score.add(InsuranceType.AUTO, 1)
            else -> score
        }
    }

    private fun marriageRules(maritalStatus: String, score: Score) = when {
        maritalStatus.equals(SimulationRequest.MaritalStatus.married.name) -> score.add(InsuranceType.LIFE, 1).deduct(InsuranceType.DISABILITY, 1)
        else -> score
    }

    private fun dependentsRules(dependents: Int, score: Score) = when {
        dependents > 0 -> score.add(InsuranceType.DISABILITY, 1).add(InsuranceType.LIFE, 1)
        else -> score
    }

    private fun houseRules(house: SimulationRequest.House?, score: Score) =
            when {
                house == null -> score.ineligible(InsuranceType.HOME)
                house.ownershipStatus!!.equals(SimulationRequest.HouseStatus.mortgaged.name) -> score.add(InsuranceType.HOME, 1)
                        .add(InsuranceType.DISABILITY, 1)
                else -> score
            }


    private fun incomeRules(income: Int, score: Score) =
            when (income) {
                0 -> score.ineligible(InsuranceType.DISABILITY)
                in 200_000..Int.MAX_VALUE -> score.deduct(InsuranceType.ALL, 1)
                else -> score
            }


    private fun ageRules(age: Int, score: Score) =
            when (age) {
                in 60..Int.MAX_VALUE -> score.ineligible(InsuranceType.DISABILITY)
                        .ineligible(InsuranceType.LIFE)
                in 0..30 -> score.deduct(InsuranceType.ALL, 2)
                in 30..40 -> score.deduct(InsuranceType.ALL, 1)
                else -> score
            }
}

private fun Boolean.toInt() = if (this) 1 else 0

private enum class InsuranceType {
    AUTO, DISABILITY, HOME, LIFE, ALL
}

private data class Score(
        val auto: Int,
        val disability: Int,
        val home: Int,
        val life: Int
)

private fun Score.ineligible(type: InsuranceType): Score {
    var (auto, disability, home, life) = this

    when (type) {
        InsuranceType.AUTO -> auto = 999
        InsuranceType.DISABILITY -> disability = 999
        InsuranceType.HOME -> home = 999
        InsuranceType.LIFE -> life = 999
        InsuranceType.ALL -> {
            auto = 999; disability = 999; home = 999; life = 999
        }
    }

    return Score(auto, disability, home, life)
}

private fun Score.deduct(type: InsuranceType, value: Int): Score {
    var (auto, disability, home, life) = this

    when (type) {
        InsuranceType.AUTO -> auto -= value
        InsuranceType.DISABILITY -> disability -= value
        InsuranceType.HOME -> home -= value
        InsuranceType.LIFE -> life -= value
        InsuranceType.ALL -> {
            auto -= value; disability -= value; home -= value; life -= value
        }
    }

    return Score(auto, disability, home, life)
}

private fun Score.add(type: InsuranceType, value: Int): Score {
    var (auto, disability, home, life) = this

    when (type) {
        InsuranceType.AUTO -> auto += value
        InsuranceType.DISABILITY -> disability += value
        InsuranceType.HOME -> home += value
        InsuranceType.LIFE -> life += value
        InsuranceType.ALL -> {
            auto += value; disability += value; home += value; life += value
        }
    }

    return Score(auto, disability, home, life)
}

