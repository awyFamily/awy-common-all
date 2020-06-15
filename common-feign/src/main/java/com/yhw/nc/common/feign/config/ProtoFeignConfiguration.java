package com.yhw.nc.common.feign.config;

import com.yhw.nc.common.feign.converter.ProtostuffHttpMessageConverter;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * feign config
 * @author yhw
 */
@Configuration
public class ProtoFeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverterObjectFactory;

    @Bean
    public ProtostuffHttpMessageConverter protostuffHttpMessageConverter() {
        return new ProtostuffHttpMessageConverter();
    }

    @Bean
    public Encoder springEncoder() {
        return new SpringEncoder(this.messageConverterObjectFactory);
    }

    @Bean
    public Decoder springDecoder() {
        return new ResponseEntityDecoder(new SpringDecoder(this.messageConverterObjectFactory));
    }
}
