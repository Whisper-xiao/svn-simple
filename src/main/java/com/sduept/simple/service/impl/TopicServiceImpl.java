package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.dto.MasterPostPage;
import com.sduept.simple.entity.Topic;
import com.sduept.simple.mapper.TopicMapper;
import com.sduept.simple.service.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-22
 */
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    @Override
    public ServerResponse listMasterPostByPage(MasterPostPage pageCondition) {
        if (StringUtils.isBlank(pageCondition.getType())) {
            return ServerResponse.createByErrorMessage("任务类型不能为空");
        }
        Page<Topic> pageRe = new Page<>(pageCondition.getPageNum(), pageCondition.getPageSize());
        IPage<Topic> page = page(pageRe, new QueryWrapper<Topic>().lambda().in(Topic::getTypeId, pageCondition.getType()));
        return ServerResponse.createBySuccess(page);
    }

    @Override
    public ServerResponse addMasterPost(Topic topic) {
        boolean isSuccessed = save(topic);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("任务新增成功");
        }
        return ServerResponse.createByErrorMessage("任务新增失败");
    }

    @Override
    public ServerResponse editMasterPost(Topic topic) {
        boolean isSuccessed = updateById(topic);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("任务修改成功");
        }
        return ServerResponse.createByErrorMessage("任务修改失败");
    }

    @Override
    public ServerResponse deleteMasterPost(Integer id) {
        boolean isSuccessed = removeById(id);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("任务删除成功");
        }
        return ServerResponse.createByErrorMessage("任务删除失败");
    }
}
