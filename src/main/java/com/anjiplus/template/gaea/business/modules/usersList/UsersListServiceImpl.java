package com.anjiplus.template.gaea.business.modules.usersList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.anji.plus.gaea.bean.ResponseBean;
import com.anjiplus.template.gaea.business.modules.usersList.controller.UsersListController;
import com.anjiplus.template.gaea.business.util.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.anjiplus.template.gaea.business.modules.dataset.dao.DataSetMapper;
import com.anjiplus.template.gaea.business.modules.dataset.dao.entity.DataSet;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;
import com.anjiplus.template.gaea.business.modules.usersList.dao.UsersListMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class UsersListServiceImpl implements UsersListService {
    @Autowired
    private  UsersListMapper usersListMapper;
    private static final Logger logger = LoggerFactory.getLogger(UsersListController.class);


    @Autowired
    private DataSetMapper dataSetMapper;

    // 新增
    @Transactional
    @Override
    public ResponseBean addDataList (Users users){
        LambdaQueryWrapper<DataSet> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DataSet::getId, users.getDataSetCoding());
        DataSet dataSet = dataSetMapper.selectOne(wrapper);
        JSONArray array = JSONArray.parseArray(dataSet.getCaseResult());
        if(array.size() > 0){
            try {
                // 查询所有列名，根据列名判断是否执行添加列操作
                String selectColumnSql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = " + "'users_list_data' AND COLUMN_NAME = " + "'" +users.getFieldName() + "'";
                System.out.println(selectColumnSql);
                Map selectFlag = usersListMapper.selectColumns(selectColumnSql);
                if(CollectionUtils.isEmpty(selectFlag)){
                    String sql = "ALTER TABLE users_list_data" +  " ADD " +  users.getFieldName() + " " + users.getFieldType();
                    usersListMapper.createTableColumn(sql);
                    for(int i = 0 ; i < array.size(); i++){
                        JSONObject jsonObject = JSON.parseObject(array.get(i).toString());
                        // 更新的条件后面要进行修改根据主键ID进行处理
                        String sql2 = "UPDATE users_list_data SET " + users.getFieldName() + "=" + "'"+ jsonObject.get(users.getDataSetField()) + "'" +  " WHERE id = " + (i + 1) ;
                        usersListMapper.createTableColumn(sql2);
                    }
                    return ResponseBean.builder().code("200").message("添加新列成功").build();
                }else{
                   return ResponseBean.builder().code("5000").message("当前列名已经存在，无法添加").build();
                }
            }catch (Exception e) {
                return ResponseBean.builder().code("5000").message("插入新列失败,失败原因"+ e.getMessage()).build();
            }
        }else{
            return  ResponseBean.builder().code("5000").message("数据不存在，请检查数据集是否存在子项数据").build();
        }
    }


    @Transactional
    @Override
    public ResponseBean deleteColumn(String fieldName){
        List<String> list=new ArrayList<String>();
        if(fieldName.indexOf(",") == -1){
            list.add(fieldName);
        }else{
            list = Arrays.asList(fieldName.split(","));
        }
        try {
            for (int i = 0; i < list.size(); i++){
                String Sql = "ALTER TABLE users_list_data DROP COLUMN " + list.get(i);
                usersListMapper.deleteColumn(Sql);
            }
            return ResponseBean.builder().code("200").message("删除成功").build();
        }catch (Exception e){
            return ResponseBean.builder().code("5000").message("删除失败,失败原因"+ e.getMessage()).build();
        }
    }


    @Transactional
    @Override
    public  void selectAll(HttpServletResponse response,String fieldName,String fieldDescribe){
        List<String> list=new ArrayList<String>();
        List<String> listDescribe=new ArrayList<String>();
        if(fieldName.indexOf(",") == -1){
            list.add(fieldName);
        }else{
            list = Arrays.asList(fieldName.split(","));
        }
        if(fieldDescribe.indexOf(",") == -1){
            listDescribe.add(fieldDescribe);
        }else{
            listDescribe = Arrays.asList(fieldDescribe.split(","));
        }
        String Sql = "SELECT id," + fieldName + " FROM users_list_data ORDER BY id ASC";
        List map = usersListMapper.selectAll(Sql);

        String excelName = "测试信息表";
        LinkedHashMap<String, String> fieldMap = new LinkedHashMap<>();
        for (int i = 0 ; i < map.size(); i++){
            String string = JSON.toJSONString(map.get(i),SerializerFeature.WriteMapNullValue);
            String string2 = string.replaceAll("null","''");
            JSONObject parse = JSON.parseObject(string2);
            fieldMap.put("id","序号");
            for(int j = 0 ; j < list.size(); j++){
                fieldMap.put(list.get(j),listDescribe.get(j) );
            }
        }
        //导出数据
        new ExcelUtil().export(excelName,map,fieldMap,response);
    }

    @Transactional
    @Override
    public  ResponseBean updataColumn(Users users){
        System.out.println(users.getChangeSurface());
        if(users.getChangeSurface()){
            String selectColumnSql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = " + "'users_list_data' AND COLUMN_NAME = " + "'" +users.getFieldName() + "'";
            Map selectFlag = usersListMapper.selectColumns(selectColumnSql);
            if(CollectionUtils.isEmpty(selectFlag)){
                String updateColumnSql = "ALTER TABLE users_list_data CHANGE " + users.getOldFidleName() + " " + users.getFieldName() + " " + users.getFieldType();
                usersListMapper.createTableColumn(updateColumnSql);
                return ResponseBean.builder().code("200").message("更新成功").build();
            }else{
                return ResponseBean.builder().code("5000").message("当前列名已经存在，无法添加").build();
            }

        }else{
            LambdaQueryWrapper<DataSet> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(DataSet::getId, users.getDataSetCoding());
            DataSet dataSet = dataSetMapper.selectOne(wrapper);
            JSONArray array = JSONArray.parseArray(dataSet.getCaseResult());
            if(array.size() > 0){
                try {
                    String deleteSql = "ALTER TABLE users_list_data DROP COLUMN " + users.getOldFidleName();
                    usersListMapper.deleteColumn(deleteSql);
                    // 查询所有列名，根据列名判断是否执行添加列操作
                    String selectColumnSql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_NAME = " + "'users_list_data' AND COLUMN_NAME = " + "'" +users.getFieldName() + "'";
                    Map selectFlag = usersListMapper.selectColumns(selectColumnSql);
                    if(CollectionUtils.isEmpty(selectFlag)){
                        String sql = "ALTER TABLE users_list_data" +  " ADD " +  users.getFieldName() + " " + users.getFieldType();
                        usersListMapper.createTableColumn(sql);
                        for(int i = 0 ; i < array.size(); i++){
                            JSONObject jsonObject = JSON.parseObject(array.get(i).toString());
                            // 更新的条件后面要进行修改根据主键ID进行处理
                            String sql2 = "UPDATE users_list_data SET " + users.getFieldName() + "=" + "'"+ jsonObject.get(users.getDataSetField()) + "'" +  " WHERE id = " + (i + 1) ;
                            usersListMapper.createTableColumn(sql2);
                        }

                        return ResponseBean.builder().code("200").message("更新成功").build();
                    }else{
                        return ResponseBean.builder().code("5000").message("当前列名已经存在，无法添加").build();
                    }
                }catch (Exception e) {
                    return ResponseBean.builder().code("5000").message("更新失败,失败原因"+ e.getMessage()).build();
                }
            }else{
                return  ResponseBean.builder().code("5000").message("数据不存在，请检查数据集是否存在子项数据").build();
            }
        }
    }
}
