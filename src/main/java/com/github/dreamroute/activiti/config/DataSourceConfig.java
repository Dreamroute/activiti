package com.github.dreamroute.activiti.config;

import com.zaxxer.hikari.HikariDataSource;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        DataSource ds = new HikariDataSource();
        return ds;
    }


    @Bean
    public ProcessEngineConfiguration processEngineConfiguration() {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        DataSource ds = dataSource();
        configuration.setDataSource(ds);
        configuration.setDatabaseSchemaUpdate("true");
        return configuration;
    }

}
