package com.example.consumer.service;

import com.example.consumer.config.RabbitMQConfig;
import com.example.consumer.entity.Complaint;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;

@Service
public class RabbitMQConsumer {

    private static final Logger logger = LogManager.getLogger(RabbitMQConsumer.class);

    private final InfrastructureService infrastructureService;
    private final ElectricityService electricityService;
    private final LegalService legalService;
    private final Tracer tracer;

    public RabbitMQConsumer(
            InfrastructureService infrastructureService,
            ElectricityService electricityService,
            LegalService legalService
    ) {
        this.infrastructureService = infrastructureService;
        this.electricityService = electricityService;
        this.legalService = legalService;
        this.tracer = GlobalOpenTelemetry.getTracer("complaint-consumer");
    }

    private final TextMapGetter<MessageProperties> getter = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(MessageProperties carrier) {
            return carrier.getHeaders().keySet();
        }

        @Override
        public String get(MessageProperties carrier, String key) {
            Object value = carrier.getHeaders().get(key);
            return value != null ? value.toString() : null;
        }
    };

    @RabbitListener(queues = RabbitMQConfig.INFRASTRUCTURE_QUEUE)
    public void receiveInfrastructureComplaint(Complaint complaint, Message message) {
        handleMessage(complaint, message,
                (userId, backend2Span) -> infrastructureService.processComplaint(complaint, userId, backend2Span));
    }

    @RabbitListener(queues = RabbitMQConfig.ELECTRICITY_QUEUE)
    public void receiveElectricityComplaint(Complaint complaint, Message message) {
        handleMessage(complaint, message,
                (userId, backend2Span) -> electricityService.processComplaint(complaint, userId, backend2Span));
    }

    @RabbitListener(queues = RabbitMQConfig.LEGAL_QUEUE)
    public void receiveLegalComplaint(Complaint complaint, Message message) {
        handleMessage(complaint, message,
                (userId, backend2Span) -> legalService.processComplaint(complaint, userId, backend2Span));
    }

    private void handleMessage(Complaint complaint, Message message,
                               BiConsumer<String, Span> serviceLogic) {
        MessageProperties properties = message.getMessageProperties();
        String userId = getUserIdFromHeaders(properties);

        logger.info("Received headers: {}", properties.getHeaders());

        // Extract context from headers injected by RabbitMQ producer
        Context parentContext = GlobalOpenTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), properties, getter);

        // Start "Backend2" span as a sibling to previous spans
        Span backend2Span = tracer.spanBuilder("Backend2")
                .setSpanKind(SpanKind.CONSUMER)
                .setParent(parentContext)
                .setAttribute("user.id", userId)
                .startSpan();

        try (Scope scope = backend2Span.makeCurrent()) {
            logger.info("Processing complaint from user {}: {}", userId, complaint);
            // Pass Backend2 span down to the service for later sibling spans
            serviceLogic.accept(userId, backend2Span);
        } catch (Exception e) {
            backend2Span.recordException(e);
            backend2Span.setStatus(StatusCode.ERROR, "Error while processing complaint");
            backend2Span.setAttribute("error.message", e.getMessage());
            logger.error("Error during complaint processing", e);
        } finally {
            backend2Span.end(); // Ensure span ends here
        }
    }

    private String getUserIdFromHeaders(MessageProperties properties) {
        Object userIdHeader = properties.getHeaders().get("user-id");
        return userIdHeader != null ? userIdHeader.toString() : "unknown";
    }
}
