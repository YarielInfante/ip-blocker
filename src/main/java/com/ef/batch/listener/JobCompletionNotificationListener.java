package com.ef.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Integer usersLogCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USER_LOG", Integer.class);
            Integer blockedUserCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BLOCKED_USER", Integer.class);
            List<Map<String, Object>> select_ip_from_blocked_user = jdbcTemplate.queryForList("SELECT ip FROM blocked_user", new String[]{});

            log.info("############### List of blocked users ###############");
            for (Map<String, Object> ips : select_ip_from_blocked_user){
                log.info(ips.get("ip").toString());
            }
            log.info("ROWS INSERTED IN USER_LOG TABLE: " + usersLogCount);
            log.info("ROWS INSERTED IN BLOCKED_USER TABLE: " + blockedUserCount);

            log.info("You can see the list of blocked users in BLOCKED_USER table");

        }
    }
}
