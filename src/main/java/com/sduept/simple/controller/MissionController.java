package com.sduept.simple.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Customer;
import com.sduept.simple.entity.MachineCode;
import com.sduept.simple.entity.Mission;
import com.sduept.simple.service.CustomerService;
import com.sduept.simple.service.MachineCodeService;
import com.sduept.simple.service.MissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "MissionController", tags = {"任务管理接口"})
@RestController
@RequestMapping("/mission-controller")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MachineCodeService machineCodeService;

    @ApiOperation(value = "分页获取任务列表")
    @GetMapping("/missions")
    public ServerResponse listMissions(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return missionService.listMissionByPage(pageNumber, pageSize);
    }

    @ApiOperation(value = "新增任务")
    @PostMapping("/mission/add")
    public ServerResponse addMission(Mission mission) {
        return missionService.addMission(mission);
    }

    @ApiOperation(value = "编辑任务")
    @PostMapping("/mission/edit")
    public ServerResponse editMission(Mission mission) {
        return missionService.editMission(mission);
    }

    @ApiOperation(value = "删除任务")
    @PostMapping("/mission/del/{id}")
    public ServerResponse deleteMission(@PathVariable Integer id) {
        return missionService.deleteMission(id);
    }

    @ApiOperation(value = "查询出所有的客户列表")
    @GetMapping("/customers")
    public ServerResponse listCustomers() {
        return ServerResponse.createBySuccess(customerService.list(new QueryWrapper<Customer>().lambda().eq(Customer::getDeleted, false)));
    }

    @ApiOperation(value = "更新客户信息数据")
    @PostMapping("/customer")
    public ServerResponse editCustomer(Customer customer) {
        return customerService.editCustomer(customer);
    }

    @ApiOperation(value = "重置用户的密码")
    @PostMapping("/password/reset")
    public ServerResponse resetPassword(Integer userId) {
        return customerService.resetPassword(userId);
    }

    @ApiOperation(value = "获取指定用户对应的机器码列表")
    @GetMapping("/machine-codes")
    public ServerResponse listMachineCodesByUserId(Integer userId) {
        return machineCodeService.listMachineCodesByUserId(userId);
    }

    @ApiOperation(value = "配置用户的机器码")
    @PostMapping("/machine-codes")
    public ServerResponse configMachineCode(@RequestParam("userId") Integer userId, @RequestParam("codes") String codes) {
        List<MachineCode> machineCodes = JSON.parseArray(codes, MachineCode.class);
        return machineCodeService.configMachineCode(userId, machineCodes);
    }
}
