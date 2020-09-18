package com.sduept.simple.webservice.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.dto.CustomerDto;
import com.sduept.simple.dto.MachineCodeDto;
import com.sduept.simple.entity.Customer;
import com.sduept.simple.entity.MachineCode;
import com.sduept.simple.entity.Note;
import com.sduept.simple.service.CustomerService;
import com.sduept.simple.service.MachineCodeService;
import com.sduept.simple.service.NoteService;
import com.sduept.simple.webservice.WebServiceDemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@WebService(serviceName = "WebServiceDemoService",
        targetNamespace = "http://mananger.dragon.sduept.com",
        endpointInterface = "com.sduept.simple.webservice.WebServiceDemoService"
)
@Slf4j
public class WebServiceDemoServiceImpl implements WebServiceDemoService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MachineCodeService machineCodeService;

    @Autowired
    private NoteService noteService;

    @Override
    public String checkMachineCodeExpireById(Integer id) {
        MachineCode machineCode = machineCodeService.getOne(new QueryWrapper<MachineCode>()
                .lambda().eq(MachineCode::getId, id).eq(MachineCode::getDeleted, false));
        long value = machineCode.getExpireTime().getTime() - DateTime.now().getMillis();
        if (value < 0) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码已经过期"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccessMessage("机器码可正常使用"));
    }

    @Override
    public String checkMachineCodeExpire(String code) {
        log.info("接收的code=" + code);
        MachineCode machineCode = machineCodeService.getOne(new QueryWrapper<MachineCode>()
                .lambda().eq(MachineCode::getCode, code).eq(MachineCode::getDeleted, false));
        long value = machineCode.getExpireTime().getTime() - DateTime.now().getMillis();
        if (value < 0) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码已经过期"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccessMessage("机器码可正常使用"));
    }

    @Override
    public String login(String account, String password, String code) {
        LambdaQueryWrapper<Customer> wrapper = new QueryWrapper<Customer>().lambda()
                .eq(Customer::getAccount, account.trim())
                .eq(Customer::getPassword, password.trim());
        Customer customer = customerService.getOne(wrapper);
        if (Objects.isNull(customer)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户名或密码错误"));
        }

        // 检查用户机器码是否正确
        LambdaQueryWrapper<MachineCode> queryWrapper = new QueryWrapper<MachineCode>().lambda()
                .eq(MachineCode::getCustomerId, customer.getId())
                .eq(MachineCode::getCode, code)
                .eq(MachineCode::getDeleted, false);
        List<MachineCode> machineCodes = machineCodeService.list(queryWrapper);
        if (machineCodes.isEmpty()) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户对应的机器码不正确"));
        }

        if ("rJxMhQA1+DJub2CQ1pkf6A==".equals(password)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("此密码为默认密码,请修改后再重新登陆有"));
        }

        // 判断用户的过期时间
        long duration = DateTime.now().getMillis() - new DateTime(customer.getExpiredTime()).getMillis();

        if (duration > 0) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("您的试用已结束，请致电：13920887384 王生"));
        }

        return JSON.toJSONString(ServerResponse.createBySuccessMessage("用户登录成功"));
    }

    @Override
    public String listAllCustomers() {
        List<Customer> customers = customerService.list(new QueryWrapper<Customer>().lambda().eq(Customer::getDeleted, false));
        if (customers.isEmpty()) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("客户不存在"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccess(customers));
    }

    @Transactional
    @Override
    public String addCustomer(String customerJson) {
        if (StringUtils.isBlank(customerJson)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("客户数据为空"));
        }

        CustomerDto customerDto = JSONObject.parseObject(customerJson, CustomerDto.class);

        if (Objects.isNull(customerDto.getMachineCodes()) || customerDto.getMachineCodes().isEmpty()) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("请传入客户注册码"));
        }

        // 判断机器码是否已经存在
        List<String> codes = customerDto.getMachineCodes().stream().map(item -> item.getCode()).collect(Collectors.toList());
        boolean shouldCodeExists = machineCodeService.isMachineCodesExists(codes);
        if (shouldCodeExists) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("传入的机器码已经存在"));
        }

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDto, customer);
        customer.setUpdateTime(new Date());

        // 判断用户名和密码都不能为空
        if (StringUtils.isBlank(customer.getAccount())) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户名不能为空"));
        }
        if (StringUtils.isBlank(customer.getPassword())) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("密码不能为空"));
        }

        // 判断用户是否已经存在
        List<Customer> existsCustomers = customerService.list(new QueryWrapper<Customer>().lambda().eq(Customer::getAccount, customer.getAccount()));
        if (!existsCustomers.isEmpty()) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户已存在"));
        }

        // 设置过期时间在当前时间的基础上往后延30天
        customer.setExpiredTime(DateTime.now().plusDays(30).toDate());
        boolean isCustomerSaved = customerService.save(customer);
        if (isCustomerSaved && !Objects.isNull(customerDto.getMachineCodes())) {
            customerDto.getMachineCodes().stream().forEach(item -> {
                item.setUpdateTime(new Date());
                item.setCustomerId(customer.getId());
            });
            machineCodeService.saveBatch(customerDto.getMachineCodes());
        } else {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("客户新增失败"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccess("客户新增成功"));
    }

    @Override
    public String deleteCustomer(Integer id) {
        if (Objects.isNull(id)) {
            JSON.toJSONString(ServerResponse.createByErrorMessage("客户ID不能为空"));
        }

        Customer customer = new Customer();
        customer.setId(id);
        customer.setDeleted(true);
        boolean isDeleted = customerService.updateById(customer);
        if (isDeleted) {
            MachineCode machineCode = new MachineCode();
            machineCode.setDeleted(true);
            machineCodeService.update(machineCode, new QueryWrapper<MachineCode>().lambda().eq(MachineCode::getCustomerId, id));
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("客户删除成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("客户删除失败"));
    }

    @Override
    public String updateCustomer(String customerJson) {
        if (StringUtils.isBlank(customerJson)) {
            JSON.toJSONString(ServerResponse.createByErrorMessage("客户数据为空"));
        }

        CustomerDto customerDto = JSON.parseObject(customerJson, CustomerDto.class);

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDto, customer);
        boolean isSuccessed = customerService.updateById(customer);
        if (isSuccessed && !Objects.isNull(customerDto.getMachineCodes())) {
            customerDto.getMachineCodes().stream().forEach(item -> item.setUpdateTime(new Date()));
            machineCodeService.updateBatchById(customerDto.getMachineCodes());
        } else {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户更新失败"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccessMessage("用户更新成功"));
    }

    @Override
    public String updatePassword(String account, String oldPassword, String newPassword, String confirmPassword) {
        LambdaQueryWrapper<Customer> wrapper = new QueryWrapper<Customer>().lambda()
                .eq(Customer::getAccount, account)
                .eq(Customer::getPassword, oldPassword)
                .eq(Customer::getDeleted, false);
        Customer customer = customerService.getOne(wrapper);
        if (Objects.isNull(customer)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("用户名和密码不正确"));
        }

        if (!StringUtils.equals(newPassword, confirmPassword)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("新密码两次输入的不一致,请重新输入"));
        }

        Customer newCustomer = new Customer();
        newCustomer.setId(customer.getId());
        newCustomer.setPassword(newPassword);

        if (!customerService.updateById(newCustomer)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("密码修改失败"));
        }
        return JSON.toJSONString(ServerResponse.createBySuccessMessage("密码修改成功"));
    }

    @Override
    public String listAllMachineCodes() {
        List<MachineCode> machineCodes = machineCodeService.list(new QueryWrapper<MachineCode>().lambda()
                .eq(MachineCode::getDeleted, false));
        return JSON.toJSONString(ServerResponse.createBySuccess(machineCodes));
    }

    @Override
    public String listMachineCodesByCustomerId(Integer customerId) {
        List<MachineCode> machineCodes = machineCodeService.list(new QueryWrapper<MachineCode>().lambda()
                .eq(MachineCode::getCustomerId, customerId).eq(MachineCode::getDeleted, false));
        return JSON.toJSONString(ServerResponse.createBySuccess(machineCodes));
    }

    @Override
    public String addMachineCode(String machineCodeJson) {
        if (StringUtils.isBlank(machineCodeJson)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码数据为空"));
        }
        MachineCodeDto machineCodeDto = JSON.parseObject(machineCodeJson, MachineCodeDto.class);
        machineCodeDto.getMachineCodes().stream().forEach(item -> {
            item.setCustomerId(machineCodeDto.getCustomerId());
            item.setUpdateTime(new Date());
        });

        boolean isSuccessed = machineCodeService.saveBatch(machineCodeDto.getMachineCodes());
        if (isSuccessed) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("机器码添加成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码添加失败"));
    }

    @Override
    public String updateMachineCode(String machineCodeJson) {
        if (StringUtils.isBlank(machineCodeJson)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码数据为空"));
        }
        MachineCode machineCode = JSON.parseObject(machineCodeJson, MachineCode.class);
        boolean isSuccessed = machineCodeService.updateById(machineCode);
        if (isSuccessed) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("机器码修改成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码修改失败"));
    }

    @Override
    public String deleteMachineCode(Integer id) {
        MachineCode machineCode = new MachineCode();
        machineCode.setDeleted(true);
        boolean isSuccessed = machineCodeService.update(machineCode, new QueryWrapper<MachineCode>()
                .lambda().eq(MachineCode::getId, id));
        if (isSuccessed) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("机器码删除成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("机器码删除失败"));
    }

    @Override
    public String addNote(Note note) {
        if (Objects.isNull(note)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("请传入正确的数据"));
        }
        if (noteService.save(note)) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("数据保存成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("数据保存失败"));
    }

    @Override
    public String editNote(Note note) {
        if (Objects.isNull(note)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("请传入正确的数据"));
        }
        if (noteService.updateById(note)) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("数据更新成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("数据更新失败"));
    }

    @Override
    public String deleteNote(Integer id) {
        if (Objects.isNull(id)) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("请传入正确的数据"));
        }
        if (noteService.removeById(id)) {
            return JSON.toJSONString(ServerResponse.createBySuccessMessage("数据删除成功"));
        }
        return JSON.toJSONString(ServerResponse.createByErrorMessage("数据删除失败"));
    }

    @Override
    public String listNotes() {
        return JSON.toJSONString(ServerResponse.createBySuccess(noteService.list()));
    }

    @Override
    public String listAdList() {
        File file = new File(System.getProperty("user.dir") + File.separator + "title");
        if (!file.exists()) {
            return JSON.toJSONString(ServerResponse.createByErrorMessage("广告的配置目录不存在"));
        }

        List<String> titles = Arrays.stream(file.listFiles()).filter(f -> f.isDirectory()).map(f -> f.getName()).collect(Collectors.toList());
        return JSON.toJSONString(ServerResponse.createBySuccess(titles));
    }

    @Override
    public String getOneTitle(String titleName) {
        String path = System.getProperty("user.dir") + File.separator + "title" + File.separator + titleName;
        List<Map<String, Object>> datas = Arrays.stream(new File(path).listFiles()).filter(f -> !f.isDirectory()).map(f -> {
            Map<String, Object> result = Maps.newHashMap();
            if (f.getName().indexOf(".txt") != -1) {
                result.put("type", 0);
                result.put("content", readFileString(f));
            } else {
                result.put("type", 1);
                result.put("content", f.getAbsolutePath());
            }
            return result;
        }).collect(Collectors.toList());
        return JSON.toJSONString(ServerResponse.createBySuccess(datas));
    }

    @Override
    public byte[] downloadFile(String path) {
        File file = new File(path);
        byte[] bytes = FileUtil.readBytes(file);
        return bytes;
    }

    private String readFileString(File file) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                content.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
