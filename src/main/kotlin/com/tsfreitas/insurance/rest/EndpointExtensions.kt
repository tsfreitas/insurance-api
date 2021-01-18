package com.tsfreitas.insurance.rest

import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
@ApplicationScoped
class MyError : ExceptionMapper<Exception> {

    override fun toResponse(exception: Exception?): Response {
        print(exception)
        val error = ErrorResponse(details = listOf("Payload incorrect, please fix it"))
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build()
    }

}

data class ErrorResponse(
        val details: List<String>
)