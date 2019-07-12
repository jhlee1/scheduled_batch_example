package lee.joohan.springbatchexample.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeciderJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job deciderJob() {
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())
                .from(decider())
                    .on("ODD")
                    .to(oddStep())
                .from(decider())
                    .on("EVEN")
                    .to(eventStep())
                .end()
                .build();
    }

    @Bean
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info(">>>>>> Start!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step eventStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info(">>>>>> Even!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet(((stepContribution, chunkContext) -> {
                    log.info(">>>>>> Odd!");
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();

    }

    public static class OddDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();

            int randomNum = random.nextInt(50) + 1;
            log.info("Random number: {}", randomNum);

            if (randomNum % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }

        }
    }
}
