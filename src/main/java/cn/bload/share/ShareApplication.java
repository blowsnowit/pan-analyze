package cn.bload.share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class ShareApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
//        BaiduPan baiduPan = new BaiduPan("https://pan.baidu.com/s/1I27ypr_bXpLJWvciQq2n1g", "kb5m");
//        baiduPan.init();
//        System.out.println(baiduPan.getTreeStr());

        SpringApplication.run(ShareApplication.class, args);
    }

    protected SpringApplicationBuilder config(SpringApplicationBuilder applicationBuilder){
        return applicationBuilder.sources(ShareApplication.class);
    }
}
