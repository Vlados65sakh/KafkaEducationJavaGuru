package by.javaguru.products.service.handler;

import by.javaguru.core.dto.Product;
import by.javaguru.core.dto.command.ReserveProductCommand;
import by.javaguru.core.dto.event.ProductReservationFailedEvent;
import by.javaguru.core.dto.event.ProductReservedEvent;
import by.javaguru.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${products.commands.topic.name}")
public class ProductCommandsHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object>  kafkaTemplate;
    private final String productsEventsTopicName;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductCommandsHandler(ProductService productService,
                                  KafkaTemplate<String,
                                          Object> kafkaTemplate,
                                    @Value("${products.events.topic.name}") String productsEventsTopicName) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
        this.productsEventsTopicName = productsEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {

        try{
            Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
            Product reserveProduct = productService.reserve(desiredProduct, command.getProductId());

            ProductReservedEvent productReservedEvent = new ProductReservedEvent(command.getOrderId(),
                    command.getProductId(),
                    reserveProduct.getPrice(),
                    command.getProductQuantity());

            kafkaTemplate.send(productsEventsTopicName, productReservedEvent);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(command.getProductId(),
                    command.getOrderId(), command.getProductQuantity());

            kafkaTemplate.send(productsEventsTopicName, productReservationFailedEvent);

        }
    }

}
