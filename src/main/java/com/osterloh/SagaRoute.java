package com.osterloh;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.CamelSagaService;
import org.apache.camel.saga.InMemorySagaService;

@ApplicationScoped
public class SagaRoute extends RouteBuilder {

    @Inject
    OrderService orderService;

    @Inject
    CreditService creditService;

    @Override
    public void configure() throws Exception {
        CamelSagaService sagaService = new InMemorySagaService(); // executa a saga em memoria
        getContext().addService(sagaService);

        //Saga
        from("direct:saga").saga().propagation(SagaPropagation.REQUIRES_NEW).log("Starting the transaction")
                .to("direct:newOrder").log("Creating new order with id ${header.id}")
                .to("direct:newOrderValue").log("Reserving the credit")
                .to("direct:finish").log("Success!");

        //Order service
        from("direct:newOrder").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "newOrder").log("Order ${body} created");
        from("direct:cancelOrder")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(orderService, "cancelOrder").log("Order ${body} canceled");

        //Credit service
        from("direct:newOrderValue").saga().propagation(SagaPropagation.MANDATORY)
                .compensation("direct:cancelOrderValue")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditService, "newOrderValue")
                .log("Credit of order ${header.id} in value of ${header.value} reserved to the saga ${body}");

        from("direct:cancelOrderValue")
                .transform().header(Exchange.SAGA_LONG_RUNNING_ACTION)
                .bean(creditService, "cancelOrderValue")
                .log("Credit of order ${header.orderId} in value of ${header.value} reserved to the saga ${body}");

        //Finished
        from("direct:finish").saga().propagation(SagaPropagation.MANDATORY)
                .choice()
                .end();
//                .when(header("fail").isEqualTo(true)).to("saga:COMPENSATE")
    }
}
