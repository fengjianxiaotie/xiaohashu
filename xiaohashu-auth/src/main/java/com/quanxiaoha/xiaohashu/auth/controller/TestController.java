package com.quanxiaoha.xiaohashu.auth.controller;

import com.quanxiaoha.xiaohashu.auth.alarm.AlarmInterface;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 周思预
 * @date 2025/5/25
 * @Description test
 */
@RestController
public class TestController {

    @Resource
    private AlarmInterface alarm;


    @GetMapping("/alarm")
    public String sendAlarm() {
        alarm.send("系统出错啦，犬小哈这个月绩效没了，速度上线解决问题！");
        return "alarm success";
    }
}
