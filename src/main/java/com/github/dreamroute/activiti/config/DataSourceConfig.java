package com.github.dreamroute.activiti.config;

import com.github.dreamroute.sqlprinter.interceptor.SqlPrinter;
import com.zaxxer.hikari.HikariDataSource;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Value("${sql-show:true}")
    private String show;

    @Bean
    public SqlPrinter printer() {
        SqlPrinter printer = new SqlPrinter();
        Properties props = new Properties();
        props.setProperty("sql-show", show);
        printer.setProperties(props);
        return printer;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource dataSource() {
        DataSource ds = new HikariDataSource();
        return ds;
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration() {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        DataSource ds = dataSource();
        configuration.setDataSource(ds);
        configuration.setDatabaseSchemaUpdate("false");
        configuration.setTransactionManager(platformTransactionManager);
//        configuration.setProcessDefinitionCacheLimit(0);
        return configuration;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine() {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(springProcessEngineConfiguration());
        return processEngineFactoryBean;
    }

    @Bean
    public RepositoryService repositoryService() throws Exception {
        return processEngine().getObject().getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService() throws Exception {
        return processEngine().getObject().getRuntimeService();
    }

    @Bean
    public TaskService taskService() throws Exception {
        return processEngine().getObject().getTaskService();
    }

    @Bean
    public HistoryService historyService() throws Exception {
        return processEngine().getObject().getHistoryService();
    }

}