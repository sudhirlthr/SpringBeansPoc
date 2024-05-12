package spring.beans.poc.SpringBeanPoc;

import org.postgresql.Driver;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import spring.beans.poc.SpringBeanPoc.config.FiveApplicationConfiguration;
import spring.beans.poc.SpringBeanPoc.service.CustomerService;

import javax.sql.DataSource;
import java.io.IOException;

@SpringBootApplication
public class SpringBeanPocApplication {
	static void zero(){
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		RootBeanDefinition postgresRootBeanDefinition = new RootBeanDefinition(Driver.class);
		factory.registerBeanDefinition("driver", postgresRootBeanDefinition);

		//construct dataSource
		RootBeanDefinition dataSourceBeanDefinition = new RootBeanDefinition(SimpleDriverDataSource.class);
		var constructorArgumentValues = dataSourceBeanDefinition.getConstructorArgumentValues();
		constructorArgumentValues.addGenericArgumentValue(new RuntimeBeanReference("driver"));
		constructorArgumentValues.addGenericArgumentValue("jdbc:postgresql://localhost:8888/postgres");
		constructorArgumentValues.addGenericArgumentValue("postgres");
		constructorArgumentValues.addGenericArgumentValue("123456");

		// construct jdbcClient by providing above dataSource reference
		var jdbcClientBeanDefinition = new RootBeanDefinition(JdbcClient.class);
		jdbcClientBeanDefinition.setFactoryMethodName("create");
		jdbcClientBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(dataSourceBeanDefinition);
		factory.registerBeanDefinition("jdbcClient", jdbcClientBeanDefinition);

		var customerServiceBeanDefinition = new RootBeanDefinition(CustomerService.class);
		customerServiceBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(new RuntimeBeanReference("jdbcClient"));
		factory.registerBeanDefinition("customerService", customerServiceBeanDefinition);

		var applicationContext = new GenericApplicationContext(factory);
		applicationContext.refresh();
		execute(applicationContext);
	}

	static void oneWithXml(){
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("Beans-1.xml");
		execute(classPathXmlApplicationContext);
	}

	static void twoWithXmlAndApplicationProperties(){
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("Beans-2.xml");
		execute(classPathXmlApplicationContext);
	}

	static void threeWithXmlAndApplicationPropertiesAndWithFactory(){
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("Beans-3.xml");
		execute(classPathXmlApplicationContext);
	}

	static void fourWithXmlAndApplicationPropertiesAndWithFactoryAndComponentScan(){
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("Beans-4.xml");
		execute(classPathXmlApplicationContext);
	}

	static void five(){
		System.setProperty("spring.profiles.active", "five");
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(FiveApplicationConfiguration.class);
		execute(annotationConfigApplicationContext);
	}
	static void six(){
		var genericApplicationContext = new GenericApplicationContext();
		genericApplicationContext.registerBean("customerService", CustomerService.class);
		genericApplicationContext.registerBean("driver", Driver.class);
		genericApplicationContext.registerBean("environment", Environment.class, () -> {
			try {
				StandardEnvironment standardEnvironment = new StandardEnvironment();
				standardEnvironment.getPropertySources().addFirst(new ResourcePropertySource("classpath:/application.properties"));
				return standardEnvironment;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		genericApplicationContext.registerBean("simpleDriverDatasource", DataSource.class, ()-> {
			Environment environment = genericApplicationContext.getBean(Environment.class);
			return new SimpleDriverDataSource(genericApplicationContext.getBean(Driver.class),
					environment.getProperty("spring.datasource.url"),
					environment.getProperty("spring.datasource.username"),
					environment.getProperty("spring.datasource.password"));
		});
		genericApplicationContext.registerBean("jdbcClient", JdbcClient.class, () -> JdbcClient.create(genericApplicationContext.getBean(DataSource.class)));
		genericApplicationContext.registerBean("customerService", CustomerService.class, ()-> new CustomerService(genericApplicationContext.getBean(JdbcClient.class)));

		genericApplicationContext.refresh();

		execute(genericApplicationContext);
	}

	// here nothing to register as a bean
	// because AutoConfiguration.import file
	static void seven(){
		ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringBeanPocApplication.class);
		execute(applicationContext);
	}

	static void execute(ApplicationContext  applicationContext){
		CustomerService customerService = applicationContext.getBean(CustomerService.class);
		for (var cs: customerService.getCustomers()){
			System.out.println(cs.toString());
		}
	}

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		//zero();
		//oneWithXml();
		//twoWithXmlAndApplicationProperties();
		//threeWithXmlAndApplicationPropertiesAndWithFactory();
		//fourWithXmlAndApplicationPropertiesAndWithFactoryAndComponentScan();
		//five();
		//six();
		seven();
		long stopTime = System.nanoTime();
		System.out.println("Seconds: "+(stopTime-startTime)/1000000000.0);
	}

}
