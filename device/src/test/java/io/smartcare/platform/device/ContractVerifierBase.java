package io.smartcare.platform.device;

import io.smartcare.platform.device.dto.TelemetryData;
import io.smartcare.platform.device.repository.DeviceRepository;
import io.smartcare.platform.device.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
@DirtiesContext
@Import(ContractVerifierBase.TestConfig.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "management.health.redis.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration"
})
public abstract class ContractVerifierBase {

    @Autowired
    private DeviceService deviceService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @Autowired
    @Qualifier("internal.exchange")
    private MessageChannel internalExchange;

    @TestConfiguration
    public static class TestConfig {
        @Bean("internal.exchange")
        public MessageChannel internalExchange() {
            return new QueueChannel();
        }
    }

    @BeforeEach
    public void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Bridge RabbitTemplate to Spring Integration channel for Contract Verifier
        doAnswer(invocation -> {
            String routingKey = invocation.getArgument(1);
            Object payload = invocation.getArgument(2);
            internalExchange.send(MessageBuilder.withPayload(payload)
                    .setHeader("amqp_routingKey", routingKey)
                    .build());
            return null;
        }).when(rabbitTemplate).convertAndSend(eq("internal.exchange"), anyString(), any(Object.class));
    }

    public void triggerTelemetryUpdate() {
        TelemetryData data = new TelemetryData(
                "SN-CONTRACT-001",
                1L,
                85.0,
                "BPM",
                LocalDateTime.parse("2026-01-18T10:00:00")
        );
        deviceService.processTelemetry(data);
    }
}
