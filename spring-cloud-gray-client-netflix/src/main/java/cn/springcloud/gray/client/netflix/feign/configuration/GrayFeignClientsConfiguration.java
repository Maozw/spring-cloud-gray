package cn.springcloud.gray.client.netflix.feign.configuration;

import cn.springcloud.gray.client.config.properties.GrayRequestProperties;
import cn.springcloud.gray.client.netflix.connectionpoint.RibbonConnectionPoint;
import cn.springcloud.gray.client.netflix.feign.GrayFeignClient;
import feign.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrayFeignClientsConfiguration {


    @Autowired
    private GrayRequestProperties grayRequestProperties;


    @Bean
    public Client getFeignClient(Client feignClient, RibbonConnectionPoint ribbonConnectionPoint) {
        return new GrayFeignClient(feignClient, ribbonConnectionPoint, grayRequestProperties);
    }


}
