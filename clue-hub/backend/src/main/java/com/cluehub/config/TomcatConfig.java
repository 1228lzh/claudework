package com.cluehub.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatContextCustomizer sameSiteCookieCustomizer() {
        return context -> {
            Rfc6265CookieProcessor processor = new Rfc6265CookieProcessor();
            processor.setSameSiteCookies("Lax");
            context.setCookieProcessor(processor);
        };
    }
}
