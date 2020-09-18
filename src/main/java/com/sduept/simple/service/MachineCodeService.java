package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.MachineCode;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-04-05
 */
public interface MachineCodeService extends IService<MachineCode> {

    /**
     * 配置用户对应的机器码
     *
     * @param userId 用户ID
     * @param codes  机器码列表
     */
    ServerResponse configMachineCode(Integer userId, List<MachineCode> codes);

    /**
     * 获取指定用户对应的机器码列表
     *
     * @param userId 用户ID
     */
    ServerResponse listMachineCodesByUserId(Integer userId);


    /**
     * 判断code是否存在
     *
     * @param codes code列表
     */
    boolean isMachineCodesExists(List<String> codes);
}
