package org.chao.unifyapi;

import org.chao.unifyapi.common.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/excep")
    public int testException(){
        throw new RuntimeException("测试异常");
    }
}
