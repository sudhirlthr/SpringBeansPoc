package spring.beans.poc.SpringBeanPoc.config;

import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import spring.beans.poc.SpringBeanPoc.service.CustomerService;

import javax.sql.DataSource;

@Configuration
@Profile("five")
@ComponentScan
@PropertySource("classpath:/application.properties")
public class FiveApplicationConfiguration {
    FiveApplicationConfiguration() {
        System.out.println("FiveApplicationConfiguration...");
    }

    @Bean
    public DataSource dataSource(Environment environment, @Value("${spring.datasource.url}") String url) {
        Driver postgresDriver = new Driver();
        return new SimpleDriverDataSource(postgresDriver,
                url,
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"));
    }

    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public CustomerService customerService(JdbcClient jdbcClient){
        return new CustomerService(jdbcClient);
    }
}
