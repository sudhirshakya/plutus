package co.sorus.plutus.core.jaxrs;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {
        var response = new ResponseError();
        response.error = exception.getMessage();
        return Response.status(Status.BAD_REQUEST)
                .entity(response)
                .build();
    }

    public static class ResponseError {
        public String error;
    }
}
