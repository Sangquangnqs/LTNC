package com.project.backend.scheduler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {
    @Bean @Qualifier("EmailScheduler")
    public TaskScheduler EmaiScheduler() {
        SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();
        scheduler.setThreadNamePrefix("EmailScheduler");
        scheduler.setConcurrencyLimit(1);
        return scheduler;
    }
}
