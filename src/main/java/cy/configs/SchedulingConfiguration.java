package cy.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfiguration {
    //Scheduled job bean config
    @Bean
    @ConditionalOnProperty(prefix = "job", name = "enabled", matchIfMissing =true, havingValue = "true")
    public ScheduledJobConfig scheduledJob(){
        return new ScheduledJobConfig();
    }
}
