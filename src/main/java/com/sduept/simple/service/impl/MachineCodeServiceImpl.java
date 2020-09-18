package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Customer;
import com.sduept.simple.entity.MachineCode;
import com.sduept.simple.mapper.MachineCodeMapper;
import com.sduept.simple.service.CustomerService;
import com.sduept.simple.service.MachineCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-04-05
 */
@Service
public class MachineCodeServiceImpl extends ServiceImpl<MachineCodeMapper, MachineCode> implements MachineCodeService {

    @Autowired
    private CustomerService customerService;

    @Transactional
    @Override
    public ServerResponse configMachineCode(Integer userId, List<MachineCode> codes) {
        LambdaQueryWrapper<Customer> wrapper = new QueryWrapper<Customer>().lambda().eq(Customer::getId, userId).eq(Customer::getDeleted, false);
        Customer customer = customerService.getOne(wrapper);
        if (Objects.isNull(customer)) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        remove(new UpdateWrapper<MachineCode>().lambda().eq(MachineCode::getCustomerId, userId));
        saveBatch(codes);

        return ServerResponse.createBySuccessMessage("机器码修改成功");
    }

    @Override
    public ServerResponse listMachineCodesByUserId(Integer userId) {
        List<MachineCode> machineCodes = list(new QueryWrapper<MachineCode>().lambda().eq(MachineCode::getCustomerId, userId).eq(MachineCode::getDeleted, false));
        return ServerResponse.createBySuccess(machineCodes);
    }

    public boolean isMachineCodesExists(List<String> codes) {
        String codeStr = codes.stream().collect(Collectors.joining(","));
        List<MachineCode> machineCodes = list(new QueryWrapper<MachineCode>().lambda().in(MachineCode::getCode, codeStr));
        return machineCodes.isEmpty() ? false : true;
    }
}
