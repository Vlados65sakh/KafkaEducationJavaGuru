package by.javaguru.ws.mockservice.service;

import by.javaguru.ws.core.ProductCreatedEvent;
import by.javaguru.ws.mockservice.service.dto.CreateProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ProductServiceImpl implements ProductService {

    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String  createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException {

        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, createProductDto.getTitle(),
                createProductDto.getPrice(), createProductDto.getQuantity());

        /**
         * Cинхронный код
         */
        SendResult<String, ProductCreatedEvent>  result =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();


        LOGGER.info("Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset: {}", result.getRecordMetadata().offset());

        LOGGER.info("Return: {}", productId);

        /**
         * Асинхронный код
         */
//        CompletableFuture<SendResult<String, ProductCreatedEvent>>  future =
//                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);
//
//        future.whenComplete((result, exception) -> {
//            if (exception != null) {
//                LOGGER.error("Failed to send message: {}", exception.getMessage());
//            }else {
//                LOGGER.info("Message sent successfully: {}", result.getRecordMetadata());
//            }
//        });

//        LOGGER.info("Return: {}", productId);

        return productId;
    }
}
