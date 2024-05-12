package spring.beans.poc.SpringBeanPoc.config;

import org.postgresql.Driver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class JdbcClientConfigration implements FactoryBean<JdbcClient> {

    private String url, username, password;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public JdbcClient getObject() throws Exception {
        Driver postgresDriver = new Driver();
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(postgresDriver, this.url, this.username, this.password);
        return JdbcClient.create(dataSource);
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcClient.class;
    }
}
