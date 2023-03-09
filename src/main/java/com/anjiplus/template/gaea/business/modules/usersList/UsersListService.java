package com.anjiplus.template.gaea.business.modules.usersList;


import com.anji.plus.gaea.bean.ResponseBean;
import com.anjiplus.template.gaea.business.modules.users.dao.entity.Users;

import javax.servlet.http.HttpServletResponse;


//@Transactional
//@Service
public interface UsersListService {
    ResponseBean addDataList(Users users);

    ResponseBean deleteColumn(String fieldName);

    void selectAll( HttpServletResponse response,String fieldName, String fieldDescribe);

    ResponseBean updataColumn(Users users);
}
