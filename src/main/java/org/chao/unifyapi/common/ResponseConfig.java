package org.chao.unifyapi.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Configuration
public class ResponseConfig {
    @RestControllerAdvice
    static class CommonResultResponseAdvice implements ResponseBodyAdvice<Object> {


        @Override
        public boolean supports(MethodParameter methodParameter, Class aClass) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
            if (o instanceof R) {
                return o;
            }
            if (o instanceof String) {
                R<Object> r = new R<>();
                r.setData(o);
                try {
                    return new ObjectMapper().writeValueAsString(r);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            R<Object> r = new R<>();
            r.setData(o);
            return r;
        }
    }
}
