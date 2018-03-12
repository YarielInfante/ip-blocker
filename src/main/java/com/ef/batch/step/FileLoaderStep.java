package com.ef.batch.step;

import com.ef.domain.UserLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.util.Assert;

import javax.sql.DataSource;


@Configuration
@Slf4j
public class FileLoaderStep {

    private DataSource dataSource;

    @Autowired
    public FileLoaderStep(DataSource dataSource) {
        Assert.isNull(this.dataSource, "Data source was not initialized");
        this.dataSource = dataSource;
    }

    private final String delimiter = "|";


    @Bean("fileLoaderStepReader")
    @StepScope
    public FlatFileItemReader<UserLog> reader(@Value("#{jobParameters['fileUrl']}") String fileUrl) {

        FlatFileItemReader<UserLog> reader = new FlatFileItemReader<>();
        reader.setResource(new PathResource(fileUrl));
        reader.setLineMapper(new DefaultLineMapper<UserLog>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(delimiter);
                setNames("date", "ip", "request", "status", "userAgent");
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<UserLog>() {{
                setTargetType(UserLog.class);
            }});
        }});
        return reader;
    }

    @Bean("fileLoaderStepProcessor")
    @StepScope
    public ItemProcessor<UserLog, UserLog> processor() {
        return new ItemProcessor<UserLog, UserLog>() {

            private int id;

            @Override
            public UserLog process(UserLog item) throws Exception {

                item.setId(++id);
                item.setIp(item.getIp().trim());
                item.setRequest(item.getRequest().trim());
                item.setStatus(item.getStatus().trim());
                item.setUserAgent(item.getUserAgent().trim());

                return item;
            }
        };
    }

    @Bean("fileLoaderStepWriter")
    @StepScope
    public JdbcBatchItemWriter<UserLog> writer() {
        JdbcBatchItemWriter<UserLog> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO user_log (id, date, ip, request, status, user_agent) VALUES (:id, :date, :ip, :request, :status, :userAgent)");
        writer.setDataSource(dataSource);
        return writer;
    }
}
