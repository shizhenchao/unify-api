​ Question:

    在开发接口的时候，每次在Controller中都要用返回类包装一下数据，例如：return new Result<>("数据")；

    代码中各种Try Catch，感觉代码过于冗杂。

   有没有什么优雅的方式处理呢？答案是有的。下面将使用Spring Boot实现。





首先定义一个Api统一返回的接口类
package org.chao.unifyapi.common;
​
import lombok.Data;
​
@Data
public class R<T> {
    private String code;
    private String message;
    private T data;
}
​


定义统一数据接口处理类：

package org.chao.unifyapi.common;
​
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
​
@Configuration
public class ResponseConfig {
    @RestControllerAdvice
    static class CommonResultResponseAdvice implements ResponseBodyAdvice<Object> {
​
​
        @Override
        public boolean supports(MethodParameter methodParameter, Class aClass) {
            return true;
        }
​
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
​


实现ResponseBodyAdvice，再返回数据前重新组装数据接口。


定义controller。



package org.chao.unifyapi;
​
import org.chao.unifyapi.common.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
​
@RestController
public class ApiController {
    /**
     * 获取信息
     *
     * @param a
     * @return
     */
    @GetMapping("/unifyapi")
    public int getMessage(@Validated(R.class) int a) {
        return 0;
    }
}
​
在接口中，直接返回int类型（当然其他类型也可以。这里有个特例是String。），我们必须使用类似如下代码处理
if (o instanceof String) {
    R<Object> r = new R<>();
    r.setData(o);
    try {
        return new ObjectMapper().writeValueAsString(r);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
}
访问http://localhost:8080/unifyapi







我们看到返回的数据0已经被统一api返回类R包装。


如何处理统一异常呢？

定义异常全局处理类：
package org.chao.unifyapi.common;
​
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
​
import javax.servlet.http.HttpServletRequest;
​
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
​
    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public  String exceptionHandler(HttpServletRequest req, Exception e){
        log.error("发生业务异常！原因是：{}",e.getMessage());
        return e.getMessage();
    }
}


我们测试下：
 
@GetMapping("/excep")
public int testException(){
    throw new RuntimeException("测试异常");
}
访问http://localhost:8080/excep





异常已经被处理

不同的异常可以定义不同的处理方式。返回不同的信息。并且建议使用枚举来定义错误信息。



源码地址：


https://github.com/shizhenchao/unify-api.git



