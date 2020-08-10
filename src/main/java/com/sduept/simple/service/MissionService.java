package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Mission;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-24
 */
public interface MissionService extends IService<Mission> {

    ServerResponse listMissionByPage(Integer pageNum, Integer pageSize);

    ServerResponse addMission(Mission mission);

    ServerResponse editMission(Mission mission);

    ServerResponse deleteMission(Integer id);
}
