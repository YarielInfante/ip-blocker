package com.ef.batch.step;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

@Configuration
@Slf4j
public class EmptyTableStep implements Tasklet {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EmptyTableStep(JdbcTemplate jdbcTemplate) {
        Assert.isNull(this.jdbcTemplate, "Data source was not initialized");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        log.info("EMPTY TABLE USER_LOG ");
        jdbcTemplate.execute("TRUNCATE TABLE USER_LOG");

        return RepeatStatus.FINISHED;
    }
}
