package edu.uc.eh;

import edu.uc.eh.utils.DatabaseLoader;
import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.annotation.PostConstruct;


@SpringBootApplication
@EnableSpringDataWebSupport
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    DatabaseLoader databaseLoader;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    @Profile("default")
    public ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new WebServlet());
        registration.addUrlMappings("/console/*");
        return registration;
    }

    @PostConstruct
    void seeRepos() {}
}
