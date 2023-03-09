package com.anjiplus.template.gaea.business.modules.users.dao.entity;


import com.anji.plus.gaea.curd.entity.GaeaBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName(keepGlobalPrefix=true, value="users_list")
@Data
public class Users extends GaeaBaseEntity {
    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段描述")
    private String fieldDescribe;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "数据集编码")
    private String dataSetCoding;

    @ApiModelProperty(value = "字段类型")
    private String fieldType;

    @ApiModelProperty(value = "数据类型 1--学习数据 2--生活数据 3--导师数据")
    private String dataType;

    @ApiModelProperty(value = "0--未删除 1--已删除 DIC_NAME=DELETE_FLAG")
    private Integer deleteFlag;

    @ApiModelProperty(value = "数据集字段")
    private String dataSetField;

    @TableField(exist = false)
    private String oldFidleName;

    @TableField(exist = false)
    private Boolean changeSurface;

}

