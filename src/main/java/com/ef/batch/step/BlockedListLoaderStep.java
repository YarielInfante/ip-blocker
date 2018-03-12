package com.ef.batch.step;

import com.ef.domain.BlockedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.Assert;

import javax.sql.DataSource;


@Configuration
@Slf4j
public class BlockedListLoaderStep {

    private DataSource dataSource;

    private String templateQuery = "SELECT count(*) requests, ip, 'blocked because it exceeded the threshold of %d' comment" +
            " FROM user_log " +
            " WHERE date BETWEEN '%s' AND adddate(date_format('%s', '%s')," +
            " INTERVAL 1 %s)" +
            " GROUP BY ip " +
            " HAVING requests >= %d;";


    @Autowired
    public BlockedListLoaderStep(DataSource dataSource) {
        Assert.isNull(this.dataSource, "Data source was not initialized");
        this.dataSource = dataSource;
    }

    @Bean("blockedListLoaderStepReader")
    @StepScope
    public JdbcCursorItemReader<BlockedUser> reader(@Value("#{jobParameters['startDate']}") String startDate,
                                                    @Value("#{jobParameters['duration']}") String duration,
                                                    @Value("#{jobParameters['threshold']}") long threshold) {

        String formatedQuery = String.format(templateQuery, threshold,
                startDate,
                startDate,
                "%Y-%m-%d.%H:%i:%s",
                duration,
                threshold);

        JdbcCursorItemReader<BlockedUser> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(formatedQuery);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<>(BlockedUser.class));

        return databaseReader;
    }

    @Bean("blockedListLoaderStepProcessor")
    @StepScope
    public ItemProcessor<BlockedUser, BlockedUser> processor() {
        return new ItemProcessor<BlockedUser, BlockedUser>() {

            private int id;

            @Override
            public BlockedUser process(BlockedUser item) throws Exception {
                item.setId(++id);
                return item;
            }
        };
    }

    @Bean("blockedListLoaderStepWriter")
    @StepScope
    public JdbcBatchItemWriter<BlockedUser> writer() {
        JdbcBatchItemWriter<BlockedUser> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO blocked_user (id, requests, ip, comment) VALUES (:id, :requests, :ip, :comment)");
        writer.setDataSource(dataSource);
        return writer;
    }


}
