/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.examples.conference.mp;

import java.util.Collections;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.glassfish.jersey.server.Uri;

import io.helidon.security.Principal;
import io.helidon.security.SecurityContext;
import io.helidon.security.annotations.Authenticated;
import io.helidon.security.integration.jersey.ClientSecurityFeature;
import io.helidon.security.integration.jersey.SecureClient;

/**
 * A simple JAX-RS resource to greet you. Examples:
 *
 * Get default greeting message: curl -X GET http://localhost:8080/greet
 *
 * Get greeting message for Joe: curl -X GET http://localhost:8080/greet/Joe
 *
 * Change greeting curl -X PUT -H "Content-Type: application/json" -d
 * '{"greeting" : "Howdy"}' http://localhost:8080/greet/greeting
 *
 * The message is returned as a JSON object.
 */
@Path("/greet")
@RequestScoped
@Authenticated
public class GreetResource {

	private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

	/**
	 * The greeting message provider.
	 */
	private final GreetingProvider greetingProvider;

	@Uri("http://localhost:8080/greet")
	@SecureClient
	private WebTarget target;

	/**
	 * Using constructor injection to get a configuration property. By default this
	 * gets the value from META-INF/microprofile-config
	 *
	 * @param greetingConfig the configured greeting message
	 */
	@Inject
	public GreetResource(GreetingProvider greetingConfig) {
		this.greetingProvider = greetingConfig;
	}

	/**
	 * Return a wordly greeting message.
	 *
	 * @return {@link JsonObject}
	 */
	@SuppressWarnings("checkstyle:designforextension")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	@Counted(name = "greet.default.counter", monotonic = true, absolute = true)
	public JsonObject getDefaultMessage(@Context SecurityContext context) {
		String user = context.userPrincipal().map(Principal::getName).orElse("World");

		return createResponse(user);
	}

	/**
	 * Return a greeting message using the name that was provided.
	 *
	 * @param name the name to greet
	 * @return {@link JsonObject}
	 */
	@SuppressWarnings("checkstyle:designforextension")
	@Path("/{name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	@Counted(name = "greet.message.counter", monotonic = true, absolute = true)
	public JsonObject getMessage(@PathParam("name") String name, @Context SecurityContext context) {
		String user = context.userPrincipal().map(Principal::getName).orElse("Anonymous");

		return createResponse(name + " (security: " + user + ")");
	}

	/**
	 * Set the greeting to use in future messages.
	 *
	 * @param jsonObject JSON containing the new greeting
	 * @return {@link Response}
	 */
	@SuppressWarnings("checkstyle:designforextension")
	@Path("/greeting")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("admin")
	@Timed
	@Counted(name = "greet.message.update.counter", monotonic = true, absolute = true)
	public Response updateGreeting(JsonObject jsonObject) {

		if (!jsonObject.containsKey("greeting")) {
			JsonObject entity = JSON.createObjectBuilder().add("error", "No greeting provided").build();
			return Response.status(Response.Status.BAD_REQUEST).entity(entity).build();
		}

		String newGreeting = jsonObject.getString("greeting");

		greetingProvider.setMessage(newGreeting);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	@Path("/outbound/{name}")
	@Fallback(fallbackMethod = "onFailureOutbound")
	public JsonObject outbound(@PathParam("name") String name, @Context SecurityContext context) {
//		return target.path(name).request().accept(MediaType.APPLICATION_JSON_TYPE).get(JsonObject.class);
//		return target.path(name).request().property(ClientSecurityFeature.PROPERTY_CONTEXT, context)
//				.get(JsonObject.class);
		Response response = target.path(name).request().property(ClientSecurityFeature.PROPERTY_CONTEXT, context).get();
		return response.readEntity(JsonObject.class);
	}

	public JsonObject onFailureOutbound(String name, SecurityContext context) {
		return Json.createObjectBuilder().add("Failed", name).build();
	}

	private JsonObject createResponse(String who) {
		String msg = String.format("%s %s!", greetingProvider.getMessage(), who);

		return JSON.createObjectBuilder().add("message", msg).build();
	}
}
