package com.anjiplus.template.gaea.business.modules.users.controller.dto;

import com.anji.plus.gaea.curd.dto.GaeaBaseDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class UsersDto extends GaeaBaseDTO implements Serializable {
    /** 字段名称 */
    private String fieldName;

    /** 字段描述 */
    private String fieldDescribe;

    /** 备注 */
    private String remarks;

    /** 数据集编码 */
    private String dataSetCoding;


    /** 字段类型 */
    private String fieldType;

    /** 数据类型 */
    private String dataType;

    /** 数据集字段 */
    private String dataSetField;

}
