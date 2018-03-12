package com.ef.parser;

import com.ef.batch.job.ParserJob;
import com.ef.batch.listener.JobCompletionNotificationListener;
import com.ef.batch.step.BlockedListLoaderStep;
import com.ef.batch.step.EmptyBlockedUserTableStep;
import com.ef.batch.step.EmptyTableStep;
import com.ef.batch.step.FileLoaderStep;
import com.ef.config.Configurations;
import com.ef.config.SpringRegistry;
import com.ef.domain.ParserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

@Slf4j
public class ParserInvoker {

    private AnnotationConfigApplicationContext context;

    public ParserInvoker() {
        context = SpringRegistry.getContext();
    }

    public BatchStatus invoke(ParserDTO parserDTO) {

        Date jobStartTime = new Date();

        try {

            JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
            Job job = (Job) context.getBean("ParserJob");

            JobParametersBuilder jobBuilder = new JobParametersBuilder();
            jobBuilder.addString("fileUrl", parserDTO.getFileUrl());
            jobBuilder.addString("startDate", parserDTO.getStartDate());
            jobBuilder.addString("duration", parserDTO.getDuration());
            jobBuilder.addLong("threshold", parserDTO.getThreshold());
            jobBuilder.addString("date", new Date().toString());

            JobParameters jobParameters = jobBuilder.toJobParameters();
            JobExecution execution = jobLauncher.run(job, jobParameters);

            Date jobEndTime = new Date();

            log.info("Job started at : " + jobStartTime.toString());
            log.info("Job ended at : " + jobEndTime.toString());

            return execution.getStatus();

        } catch (Exception e) {
            Date jobEndTime = new Date();

            log.info("Job started at : " + jobStartTime.toString());
            log.info("Job ended at : " + jobEndTime.toString());

            log.error(e.getMessage());
            e.printStackTrace();
            return BatchStatus.FAILED;
        }

    }
}
