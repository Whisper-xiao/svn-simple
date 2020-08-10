package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.entity.Mission;
import com.sduept.simple.mapper.MissionMapper;
import com.sduept.simple.service.MissionService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-24
 */
@Service
public class MissionServiceImpl extends ServiceImpl<MissionMapper, Mission> implements MissionService {

    @Override
    public ServerResponse listMissionByPage(Integer pageNum, Integer pageSize) {
        Page<Mission> pageRe = new Page<>(pageNum, pageSize);
        return ServerResponse.createBySuccess(page(pageRe, new QueryWrapper<Mission>().orderByAsc("sort")));
    }

    @Override
    public ServerResponse addMission(Mission mission) {
        if (Objects.isNull(mission.getId()) || mission.getId() == 0) {
            boolean isSuccessed = save(mission);
            if (isSuccessed) {
                return ServerResponse.createBySuccessMessage("新增成功");
            }
            return ServerResponse.createByErrorMessage("新增失败");
        } else {
            boolean isSuccessed = updateById(mission);
            if (isSuccessed) {
                return ServerResponse.createBySuccessMessage("修改成功");
            }
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    @Override
    public ServerResponse editMission(Mission mission) {
        boolean isSuccessed = updateById(mission);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("修改成功");
        }
        return ServerResponse.createByErrorMessage("失败失败");
    }

    @Override
    public ServerResponse deleteMission(Integer id) {
        boolean isSuccessed = removeById(id);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }
}
