package com.ef.batch.job;

import com.ef.batch.listener.JobCompletionNotificationListener;
import com.ef.batch.step.BlockedListLoaderStep;
import com.ef.batch.step.EmptyBlockedUserTableStep;
import com.ef.batch.step.EmptyTableStep;
import com.ef.batch.step.FileLoaderStep;
import com.ef.domain.BlockedUser;
import com.ef.domain.UserLog;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:/application.properties")
public class ParserJob {

    private Environment env;
    private EmptyTableStep emptyTableStep;
    private FileLoaderStep fileLoaderStep;
    private StepBuilderFactory stepBuilderFactory;
    private BlockedListLoaderStep blockedListLoaderStep;
    private EmptyBlockedUserTableStep emptyBlockedUserTableStep;
    private JobCompletionNotificationListener listener;


    @Autowired
    public ParserJob(Environment env, EmptyTableStep emptyTableStep, FileLoaderStep fileLoaderStep, StepBuilderFactory stepBuilderFactory, BlockedListLoaderStep blockedListLoaderStep, EmptyBlockedUserTableStep emptyBlockedUserTableStep, JobCompletionNotificationListener listener) {
        this.env = env;
        this.emptyTableStep = emptyTableStep;
        this.fileLoaderStep = fileLoaderStep;
        this.stepBuilderFactory = stepBuilderFactory;
        this.blockedListLoaderStep = blockedListLoaderStep;
        this.emptyBlockedUserTableStep = emptyBlockedUserTableStep;
        this.listener = listener;
    }

    @Bean("ParserJob")
    public Job job(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("Parser Job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(emptyStep())
                .next(loaderStep())
                .next(emptyBlockUserTableStep())
                .next(blockUserStep())
                .build();
    }


    private Step emptyStep() {
        return stepBuilderFactory.get("empty tmp Step")
                .tasklet(emptyTableStep)
                .allowStartIfComplete(true)
                .build();
    }

    private Step loaderStep() {
        return stepBuilderFactory.get("txt Loader Step")
                .<UserLog, UserLog>chunk(Integer.valueOf(env.getProperty("application.job.chunkSize")))
                .reader(fileLoaderStep.reader(null))
                .processor(fileLoaderStep.processor())
                .writer(fileLoaderStep.writer())
                .allowStartIfComplete(true)
                .build();
    }

    private Step emptyBlockUserTableStep() {
        return stepBuilderFactory.get("empty blocked user table Step")
                .tasklet(emptyBlockedUserTableStep)
                .allowStartIfComplete(true)
                .build();
    }

    private Step blockUserStep() {
        return stepBuilderFactory.get("block Loader Step")
                .<BlockedUser, BlockedUser>chunk(Integer.valueOf(env.getProperty("application.job.chunkSize")))
                .reader(blockedListLoaderStep.reader(null, null, 0))
                .processor(blockedListLoaderStep.processor())
                .writer(blockedListLoaderStep.writer())
                .allowStartIfComplete(true)
                .build();
    }
}
