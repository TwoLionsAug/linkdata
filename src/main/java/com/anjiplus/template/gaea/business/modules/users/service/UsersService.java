package com.anjiplus.template.gaea.business.modules.users.service;

import com.anji.plus.gaea.curd.service.GaeaBaseService;
import com.anjiplus.template.gaea.business.modules.users.controller.param.UsersParam;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;

import java.util.List;


public interface UsersService extends GaeaBaseService<UsersParam, Users> {
    /**
     * 获取所有数据集
     * @return
     */
    List<Users> queryAllUsersList();

}
