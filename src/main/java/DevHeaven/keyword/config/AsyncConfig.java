package DevHeaven.keyword.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean
  public TaskExecutor asyncExecutor() {
    ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
    asyncExecutor.setThreadNamePrefix("async-pool");
    asyncExecutor.setCorePoolSize(10);
    asyncExecutor.initialize();
    return asyncExecutor;
  }

}
