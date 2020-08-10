package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Customer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-04-05
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 更新客户信息
     *
     * @param customer 客户数据
     */
    ServerResponse editCustomer(Customer customer);

    /**
     * 重置用户的密码
     *
     * @param userId 用户ID
     */
    ServerResponse resetPassword(Integer userId);
}
