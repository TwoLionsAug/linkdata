package com.anjiplus.template.gaea.business.modules.users.service.impl;

import com.anji.plus.gaea.constant.Enabled;
import com.anji.plus.gaea.curd.mapper.GaeaBaseMapper;
import com.anjiplus.template.gaea.business.modules.users.dao.UsersMapper;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;
import com.anjiplus.template.gaea.business.modules.users.service.UsersService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersMapper usersMapper;

    @Override
    public GaeaBaseMapper<Users> getMapper() {
        return usersMapper;
    }

    @Override
    public List<Users> queryAllUsersList() {
        LambdaQueryWrapper<Users> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Users::getFieldName, Users::getDataSetCoding, Users::getFieldType, Users::getId)
                .eq(Users::getDeleteFlag, Enabled.NO.getValue());
        wrapper.orderByDesc(Users::getUpdateTime);
        return usersMapper.selectList(wrapper);
    }


}
