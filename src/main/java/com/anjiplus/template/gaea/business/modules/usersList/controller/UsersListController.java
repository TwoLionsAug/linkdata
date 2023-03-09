package com.anjiplus.template.gaea.business.modules.usersList.controller;

import com.anji.plus.gaea.bean.ResponseBean;
import com.anji.plus.gaea.code.ResponseCode;
import com.anji.plus.gaea.exception.BusinessExceptionBuilder;
import com.anjiplus.template.gaea.business.modules.dataset.dao.DataSetMapper;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;
import com.anjiplus.template.gaea.business.modules.usersList.UsersListService;
import com.anjiplus.template.gaea.business.modules.usersList.dao.UsersListMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Arrays;

@RestController
@RequestMapping("users_lists/")
public class UsersListController {
    private static final Logger logger = LoggerFactory.getLogger(UsersListController.class);

    @Resource
    private UsersListMapper usersListMapper;

    @Autowired
    DataSetMapper dataSetMapper;

    @Autowired
    UsersListService usersListService;

    @PostMapping("/createTable")
    public ResponseBean createTableTest(@RequestParam String tableName, @RequestParam String columnName, @RequestParam String datatype) {
        try {
            // 创建数据库表
            String sql = "ALTER TABLE " +  tableName +  " ADD " +  columnName + " " + datatype;
            usersListMapper.createTableColumn(sql);
        } catch (Exception e) {
            logger.error("插入新列失败");
            throw BusinessExceptionBuilder.build(ResponseCode.FAIL_CODE, e.getMessage());
        }
        return ResponseBean.builder().code("200").message("添加新列成功").build();

    }

    // 添加
    @PostMapping("/addDataList")
    public ResponseBean addDataList(Users users){
        // 1. 接收参数，根据参数的内容获取到数据集的编码，去获取对应的数据
        // 2. 跟根据传递过来的子项，对查询到的数据集进行数据抽离，提取到想要到数据
        // 3. 获取学生数据的所有字段名，判断接收到的数据名是否存在表中，如果存在则抛出错误，否则添加新列
        // 4. 需要现将主键id导入到数据表中，后续的添加要根据主键进行对应添加
        return  usersListService.addDataList(users);
    }

    // 删除
    @PostMapping("/deleteColumn")
    public  ResponseBean deleteColumn(@RequestParam String fieldName){
        return  usersListService.deleteColumn(fieldName);
    }

    // 查询出所有数据
    @GetMapping("/selectAll")
    public void selectAll(HttpServletResponse response, String fieldName,String fieldDescribe){
        usersListService.selectAll(response, fieldName,fieldDescribe);
    }

    // 编辑
    @PostMapping("/updataColumn")
    public ResponseBean updataColumn(@RequestBody Users users){
        // 修改内容的判断在前端处理
        // 1. 当用户只修改了和表结构数据无关的信息时如备注、字段描述、数据类型 不会调用该接口，直接调用user中的接口修改内容
        // 2. 当用户修改了表接口和内容时调用当前接口,根据接口中的 changeSurface 判断更新情况
        //     2.1 changeSurface 存在表示用户修改了字段名和字段类型，这时只需要修改表中列的名称和字段类型，不会进行数据修改操作
        //     2.2 changeSurface 不存在表示用户不仅修改了字段信息还修改了数据内容，删除表中当前列,重新执行添加逻辑
        return usersListService.updataColumn(users);
    }
}
