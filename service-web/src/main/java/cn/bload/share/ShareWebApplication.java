package cn.bload.share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ShareWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ShareWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //此处加载的资源为`MainApplication`
        return builder.sources(ShareWebApplication.class);
    }
}
