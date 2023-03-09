package com.anjiplus.template.gaea.business.modules.users.controller.param;

import com.anji.plus.gaea.annotation.Query;
import com.anji.plus.gaea.constant.QueryEnum;
import com.anji.plus.gaea.curd.params.PageParam;
import lombok.Data;

import java.io.Serializable;

@Data
public class UsersParam extends PageParam implements Serializable {
    /** 数据类型 */
    @Query(QueryEnum.LIKE)
    private String dataType;

    /** 字段名称 */
    @Query(QueryEnum.LIKE)
    private String fieldName;

    /** 字段描述 */
    @Query(QueryEnum.LIKE)
    private String fieldDescribe;

    /** 数据集编码 */
    @Query(QueryEnum.EQ)
    private String dataSetCoding;
}
