package kuke.board.common.outboxmessagerelay

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@EnableAsync // 카프카 이벤트 전송을 비동기로 처리하기 위함
@Configuration
@ComponentScan("kuke.board.common.outboxmessagerelay")
class MessageRelayConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun messageRelayKafkaTemplate(): KafkaTemplate<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.ACKS_CONFIG] = "all"
        return KafkaTemplate(DefaultKafkaProducerFactory(configProps))
    }

    // 이벤트를 비동기로 전송하기 위한 스레드풀
    @Bean
    fun messageRelayPublishEventExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 20
        executor.maxPoolSize = 50
        executor.queueCapacity = 100
        executor.setThreadNamePrefix("mr-pub-event")
        return executor
    }

    @Bean
    fun messageRelayPublishPendingEventExecutor(): Executor {
        return Executors.newSingleThreadScheduledExecutor()
    }
}