package com.anjiplus.template.gaea.business.modules.users.dao;


import com.anji.plus.gaea.curd.mapper.GaeaBaseMapper;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper extends GaeaBaseMapper<Users> {
}
