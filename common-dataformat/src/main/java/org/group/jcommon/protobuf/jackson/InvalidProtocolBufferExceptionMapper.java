package org.group.jcommon.protobuf.jackson;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.InvalidProtocolBufferException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Provider
public class InvalidProtocolBufferExceptionMapper implements ExceptionMapper<InvalidProtocolBufferException> {
    private static final Logger logger = LoggerFactory.getLogger(InvalidProtocolBufferExceptionMapper.class);

    static class Error {
        @JsonProperty
        String message;
        @JsonProperty
        int code;
    }

    @Override
    public Response toResponse(InvalidProtocolBufferException exception) {
        final Error error = new Error();
        error.message = "Unable to process protocol buffer";
        error.code = Response.Status.BAD_REQUEST.getStatusCode();

        logger.debug("Unable to process protocol buffer message", exception);
        return Response.status(Response.Status.BAD_REQUEST).type(ProtocolBufferMediaType.APPLICATION_PROTOBUF_TYPE)
                .entity(error).build();
    }
}