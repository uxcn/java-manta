/**
 * Copyright (c) 2014, Joyent, Inc. All rights reserved.
 */
package com.joyent.manta.exception;


import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ObjectParser;
import com.joyent.manta.client.MantaHttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

/**
 * Convenience wrapper over {@link HttpResponseException} so that consumers of this library don't have to depend on the
 * underlying HTTP client implementation.
 *
 * @author Yunong Xiao
 */
public class MantaClientHttpResponseException extends IOException {


    private static final long serialVersionUID = -2233924525500839645L;


    /**
     * Logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MantaClientHttpResponseException.class);


    /**
     * JSON parser.
     */
    private static final ObjectParser PARSER = new JsonObjectParser(new JacksonFactory());


    /**
     * Default server code when no code is available.
     */
    static final String UNKNOWN_SERVER_CODE = "Unknown";


    /**
     * The underlying {@link HttpResponseException}.
     * */
    private final HttpResponseException innerException;


    /**
     * Server error code returned from Manta.
     */
    private final String serverCode;


    /**
     * Error message returned from Manta.
     */
    private final String message;


    /**
     *
     * @param innerException The {@link HttpResponseException} to be wrapped.
     */
    public MantaClientHttpResponseException(final HttpResponseException innerException) {
        super(innerException);
        final String jsonContent = innerException.getContent();
        final Map<String, Object> serverErrorInfo = parseJsonResponse(jsonContent);

        this.innerException = innerException;
        this.serverCode = serverErrorInfo.getOrDefault("code", UNKNOWN_SERVER_CODE).toString();

        if (serverErrorInfo.containsKey("message")) {
            this.message = serverErrorInfo.get("message").toString();
        } else {
            this.message = null;
        }
    }


    /**
     * @return Whether received a successful HTTP status code {@code >= 200 && < 300} (see {@link #getStatusCode()}).
     */
    public final boolean isSuccessStatusCode() {
        return this.innerException.isSuccessStatusCode();
    }


    /**
     * @return The HTTP status code or {@code 0} for none.
     */
    public final int getStatusCode() {
        return this.innerException.getStatusCode();
    }


    /**
     * @return The HTTP status message or {@code null} for none.
     */
    public final String getStatusMessage() {
        return this.innerException.getStatusMessage();
    }


    /**
     * @return The HTTP response headers.
     */
    public final MantaHttpHeaders getHeaders() {
        return new MantaHttpHeaders(innerException.getHeaders());
    }

    /**
     * Content returned as part of the HTTP response.
     *
     * @return HTTP body
     */
    public String getContent() {
        return innerException.getContent();
    }


    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }


    @Override
    public String getMessage() {
        if (serverCode.equals(UNKNOWN_SERVER_CODE)) {
            return innerException.getMessage();
        } else {
            return String.format("%d %s - [%s] %s",
                    innerException.getStatusCode(),
                    innerException.getStatusMessage(),
                    this.serverCode, this.message);
        }
    }


    /**
     * Error code returned from server. This is a String constant defined by
     * Manta.
     * @return error code, if unavailable the value of UNKNOWN_SERVER_CODE
     */
    public String getServerCode() {
        return this.serverCode;
    }


    /**
     * Error message returned from Manta API.
     *
     * @return server error message, if unavailable null
     */
    public String getServerMessage() {
        return this.message;
    }


    /**
     * Parses JSON error message returned from the Manta API.
     *
     * @param content JSON content
     * @return Map containing JSON values
     */
    static Map<String, Object> parseJsonResponse(final String content) {
        if (content == null || content.isEmpty()) {
            return Collections.emptyMap();
        }

        final Reader reader = new StringReader(content);
        final GenericJson genericJson;
        try {
            genericJson = PARSER.parseAndClose(reader, GenericJson.class);
        } catch (IOException e) {
            LOG.warn("Unable to parse JSON response from API", e);
            return Collections.emptyMap();
        }

        return genericJson;
    }
}
