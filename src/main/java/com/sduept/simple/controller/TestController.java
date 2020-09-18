package com.sduept.simple.controller;

import com.alibaba.fastjson.JSON;
import com.sduept.simple.service.RoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Api(value = "TestController", tags = {"测试"})
@RestController
public class TestController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/test")
    public String test() {
        String address = "贵州省铜仁市德江县泉口镇大元村黄泥坡组";
        String url = "https://restapi.amap.com/v3/geocode/geo?address=" + address + "&output=JSON&key=a23b77b278ece3a9103bd800a4e5fff2";
        RestTemplate rest = new RestTemplate();
        String resultStr = rest.getForObject(url, String.class);
        Map<String, Object> result = JSON.parseObject(resultStr);
        return "";
    }
}
