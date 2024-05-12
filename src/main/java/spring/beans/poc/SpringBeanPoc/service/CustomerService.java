package spring.beans.poc.SpringBeanPoc.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import spring.beans.poc.SpringBeanPoc.pojo.Customer;

import java.util.Collection;

@Service
public class CustomerService implements InitializingBean {

    private final JdbcClient  jdbcClient;

    public CustomerService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Collection<Customer> getCustomers(){
        return this
                .jdbcClient.sql("select * from customer")
                .query((rs, rowNum)-> new Customer(rs.getLong("id"), rs.getString("name")))
                .list();
    }

// to do last stage verification/validation, we can use afterPropertiesSet method
    //even inplace of InitializingBean you can use @PostConstuct/@PreDestroy Annotation with same behaviour
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("AfterPropertySet");
        Assert.notNull(this.jdbcClient, "The jdbcClient must not be null");
    }
}
