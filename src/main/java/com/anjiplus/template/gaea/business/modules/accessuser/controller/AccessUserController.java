
package com.anjiplus.template.gaea.business.modules.accessuser.controller;

import com.anji.plus.gaea.annotation.Permission;
import com.anji.plus.gaea.bean.ResponseBean;
import com.anji.plus.gaea.curd.controller.GaeaBaseController;
import com.anji.plus.gaea.curd.service.GaeaBaseService;
import com.anji.plus.gaea.exception.BusinessExceptionBuilder;
import com.anji.plus.gaea.holder.UserContentHolder;
import com.anjiplus.template.gaea.business.code.ResponseCode;
import com.anjiplus.template.gaea.business.modules.accessrole.dao.AccessRoleMapper;
import com.anjiplus.template.gaea.business.modules.accessrole.dao.entity.AccessRole;
import com.anjiplus.template.gaea.business.modules.accessuser.controller.dto.GaeaUserDto;
import com.anjiplus.template.gaea.business.modules.accessuser.controller.dto.GaeaUserLoginOutDto;
import com.anjiplus.template.gaea.business.modules.accessuser.controller.dto.UpdatePasswordDto;
import com.anjiplus.template.gaea.business.modules.accessuser.dao.AccessUserMapper;
import com.anjiplus.template.gaea.business.modules.accessuser.dao.AccessUserRoleMapper;
import com.anjiplus.template.gaea.business.modules.accessuser.dao.entity.AccessUser;
import com.anjiplus.template.gaea.business.modules.accessuser.dao.entity.AccessUserRole;
import com.anjiplus.template.gaea.business.modules.accessuser.service.AccessUserService;
import com.anjiplus.template.gaea.business.modules.accessuser.controller.dto.AccessUserDto;
import com.anjiplus.template.gaea.business.modules.accessuser.controller.param.AccessUserParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
* @desc ???????????? controller
* @author ???????????De <lide1202@hotmail.com>
* @date 2019-02-17 08:50:11.902
**/
@RestController
@Api(tags = "??????????????????")
@RequestMapping("/accessUser")
@Permission(code = "userManage", name = "????????????")
public class AccessUserController extends GaeaBaseController<AccessUserParam, AccessUser, AccessUserDto> {

    @Autowired
    private AccessUserService accessUserService;

    @Autowired
    private AccessRoleMapper accessRoleMapper;

    @Autowired
    private AccessUserRoleMapper accessUserRoleMapper;

    @Override
    public GaeaBaseService<AccessUserParam, AccessUser> getService() {
        return accessUserService;
    }

    @Override
    public AccessUser getEntity() {
        return new AccessUser();
    }

    @Override
    public AccessUserDto getDTO() {
        return new AccessUserDto();
    }


    /**
     * ????????????????????????
     * @return
     */
    @Permission( code = "grantRole", name = "????????????")
    @GetMapping("/roleTree/{loginName}")
    public ResponseBean getRoleTree(@PathVariable("loginName")String loginName){
        String operator = UserContentHolder.getContext().getUsername();
        Map map = accessUserService.getRoleTree(loginName, operator);
        return responseSuccessWithData(map);
    }

    /**
     * ????????????????????????
     * @return
     */
    @Permission( code = "grantRole", name = "????????????")
    @PostMapping("/saveRoleTree")
    public ResponseBean saveRoleTree(@RequestBody AccessUserDto dto){
        Boolean data = accessUserService.saveRoleTree(dto);
        return responseSuccessWithData(data);
    }


    /**
     * ????????????
     * @param dto
     * @return
     */
    @Permission( code = "resetPassword", name = "????????????")
    @PostMapping({"/resetPassword"})
    public ResponseBean resetPassword(@RequestBody @Validated GaeaUserDto dto) {
        Boolean data = accessUserService.resetPassword(dto);
        return responseSuccessWithData(data);
    }




    /**
     * ??????????????????
     * @param dto
     * @return
     */
    @PostMapping({"/login"})
    public ResponseBean login(@RequestBody @Validated GaeaUserDto dto, HttpServletRequest request) {
        GaeaUserDto gaeaUserDto = accessUserService.login(dto,request);
        QueryWrapper<AccessUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name", gaeaUserDto.getLoginName());
        AccessUserRole userRole = accessUserRoleMapper.selectOne(queryWrapper);
        QueryWrapper<AccessRole> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("role_code",userRole.getRoleCode());
        AccessRole accessRole = accessRoleMapper.selectOne(queryWrapper2);
        if(accessRole.getEnableFlag() == 0){
            throw BusinessExceptionBuilder.build(ResponseCode.RULE_FIELDS_CHECK_ERROR,"????????????????????????");
        }
        gaeaUserDto.setRoleName(userRole.getRoleCode());
        return responseSuccessWithData(gaeaUserDto);
    }

    /**
     * ?????????????????????
     * @param dto
     * @return
     */
    @PostMapping("/updatePassword")
    public ResponseBean updatePassword(@RequestBody UpdatePasswordDto dto) {
        return responseSuccessWithData(accessUserService.updatePassword(dto));
    }

    @PostMapping("/loginout")
    public ResponseBean loginout(@RequestBody @Validated GaeaUserLoginOutDto gaeaUserLoginOutDto){
        return responseSuccessWithData(accessUserService.loginout(gaeaUserLoginOutDto));
    }

}
