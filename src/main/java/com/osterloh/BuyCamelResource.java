package com.osterloh;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.camel.CamelContext;

@Path("buy-camel")
public class BuyCamelResource {

    @Inject
    CamelContext context;

    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response saga() {
        Long id = 0L;
        buy(++id, 20);
        buy(++id, 30);
        buy(++id, 30);
        buy(++id, 25);
        return Response.ok().build();
    }

    private void buy(Long id, int value) {
        context.createFluentProducerTemplate()
                .to("direct:saga")
                .withHeader("id", id)
                .withHeader("value", value)
                .request();
    }
}
