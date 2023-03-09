package com.anjiplus.template.gaea.business.modules.users.controller;

import com.anji.plus.gaea.annotation.Permission;
import com.anji.plus.gaea.bean.ResponseBean;
import com.anji.plus.gaea.curd.controller.GaeaBaseController;
import com.anji.plus.gaea.curd.service.GaeaBaseService;
import com.anjiplus.template.gaea.business.modules.users.controller.dto.UsersDto;
import com.anjiplus.template.gaea.business.modules.users.controller.param.UsersParam;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;
import com.anjiplus.template.gaea.business.modules.users.service.UsersService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "学生管理")
@Permission(code = "userManage", name = "学生数据")
@RequestMapping("/users")
public class UsersController extends GaeaBaseController<UsersParam,Users,UsersDto> {
    @Autowired
    private UsersService usersService;

    @Override
    public GaeaBaseService<UsersParam, Users> getService() {
        return usersService;
    }

    @Override
    public Users getEntity() {
        return new Users();
    }

    @Override
    public UsersDto getDTO() {
        return new UsersDto();
    }

//    获取所有数据
    @GetMapping("/queryAllUsersList")
    public ResponseBean queryAllUsersList() {
        return responseSuccessWithData(usersService.queryAllUsersList());
    }
}
