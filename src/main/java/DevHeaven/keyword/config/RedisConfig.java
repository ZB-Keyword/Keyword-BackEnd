package DevHeaven.keyword.config;

import DevHeaven.keyword.domain.notice.dto.response.NoticeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  private final ObjectMapper objectMapper;
  private static final String REDISSON_HOST_PREFIX = "redis://";

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
    return Redisson.create(config);
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    return redisTemplate;
  }

  // Redis 관련 구성 및 리스너
  @Bean
  public RedisOperations<String, NoticeResponse> eventRedisOperations(
      RedisConnectionFactory redisConnectionFactory,
      @Qualifier("objectMapper") ObjectMapper objectMapper) {
    final Jackson2JsonRedisSerializer<NoticeResponse> jsonRedisSerializer =
        new Jackson2JsonRedisSerializer<>(NoticeResponse.class);
    jsonRedisSerializer.setObjectMapper(objectMapper);
    final RedisTemplate<String, NoticeResponse> eventRedisTemplate = new RedisTemplate<>();
    eventRedisTemplate.setConnectionFactory(redisConnectionFactory);
    eventRedisTemplate.setKeySerializer(RedisSerializer.string());
    eventRedisTemplate.setValueSerializer(jsonRedisSerializer);
    eventRedisTemplate.setHashKeySerializer(RedisSerializer.string());
    eventRedisTemplate.setHashValueSerializer(jsonRedisSerializer);
    return eventRedisTemplate;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory redisConnectionFactory) {
    final RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
    redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
    return redisMessageListenerContainer;
  }
}