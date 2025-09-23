package by.javaguru.ws.mockservice;

import by.javaguru.ws.core.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@SpringBootTest()
public class IdempotentProducerIntegrationTest {

    @Autowired
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @MockBean
    KafkaAdmin kafkaAdmin;

    @Test
    void testProducerConfig_whenIdempotenceEnabled_assertsIdempotentProperties(){
        // Arrange
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();

        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        Assertions.assertEquals("true", config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        Assertions.assertTrue("all".equalsIgnoreCase((String) config.get(ProducerConfig.ACKS_CONFIG)));

        if(config.containsKey(ProducerConfig.RETRIES_CONFIG)){
            Assertions.assertTrue(
                    Integer.parseInt(config.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0
            );
        }
        System.out.println(config);

    }

    @Test
    void testProducerConfig_countMaxInFlightRequestsPerConnection_assertsFlightRequestsPerConnectionProperties(){
        // Arrange
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();

        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        Assertions.assertEquals("5", config.get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION));

    }
}
