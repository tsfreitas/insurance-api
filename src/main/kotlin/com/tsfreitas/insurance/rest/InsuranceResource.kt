package com.tsfreitas.insurance.rest

import com.tsfreitas.insurance.model.SimulationRequest
import com.tsfreitas.insurance.service.RiskService
import javax.enterprise.inject.Default
import javax.inject.Inject
import javax.validation.Validator
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/risk")
@Produces(MediaType.APPLICATION_JSON)
class InsuranceResource {

    @Inject
    @field: Default
    lateinit var validator: Validator

    @Inject
    @field:Default
    lateinit var riskService: RiskService


    @POST
    @Path("/simulate")
    fun simulation(request: SimulationRequest): Response {
        // Validate Payload
        val violations = validator.validate(request)

        if (!violations.isEmpty()) {
            val error = ErrorResponse(violations.map { t -> t.message }.toList())
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build()
        }

        // Calculate the risk
        val riskSimulationResponse = riskService.calculateRisk(request)

        return Response.ok(riskSimulationResponse).build()
    }

}



