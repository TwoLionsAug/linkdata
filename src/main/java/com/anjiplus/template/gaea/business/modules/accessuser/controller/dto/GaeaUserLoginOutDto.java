package com.anjiplus.template.gaea.business.modules.accessuser.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@ApiModel(value = "用户注销")
@Data
public class GaeaUserLoginOutDto {
    @ApiModelProperty(value = "登录名")
    @NotBlank
        private String loginName;

    @ApiModelProperty(value = "token")
    @NotBlank
    private String token;
}
