package com.anjiplus.template.gaea.business.modules.usersList.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UsersListMapper extends BaseMapper {
    void createTableColumn(@Param("paramSQL") String sql);

    Map selectColumns (@Param("paramSQL") String sql);

    void deleteColumn(@Param("paramSQL") String sql);

    List<Map> selectAll(@Param("paramSQL") String sql);

}
