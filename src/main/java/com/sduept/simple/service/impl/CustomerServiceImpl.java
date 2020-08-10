package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Customer;
import com.sduept.simple.mapper.CustomerMapper;
import com.sduept.simple.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-04-05
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public ServerResponse editCustomer(Customer customer) {
        if (Objects.isNull(customer)) {
            return ServerResponse.createByErrorMessage("需要修改的对象不能为空");
        }
        customer.setUpdateTime(new Date());
        if (updateById(customer)) {
            return ServerResponse.createBySuccessMessage("用户数据更新成功");
        }

        return ServerResponse.createByErrorMessage("用户数据更新失败");
    }

    @Override
    public ServerResponse resetPassword(Integer userId) {
        Customer customer = getOne(new QueryWrapper<Customer>().lambda().eq(Customer::getId, userId).eq(Customer::getDeleted, false));
        if (Objects.isNull(customer)) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        Customer willUpdatedCustomer = new Customer();
        willUpdatedCustomer.setPassword("rJxMhQA1+DJub2CQ1pkf6A==");
        willUpdatedCustomer.setId(userId);
        if (!updateById(willUpdatedCustomer)) {
            return ServerResponse.createByErrorMessage("用户密码重置失败");
        }

        return ServerResponse.createBySuccessMessage("用户密码重置成功");
    }
}
