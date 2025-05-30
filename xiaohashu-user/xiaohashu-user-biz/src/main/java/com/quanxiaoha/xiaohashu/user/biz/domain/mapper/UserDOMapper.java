package com.quanxiaoha.xiaohashu.user.biz.domain.mapper;


import com.quanxiaoha.xiaohashu.user.biz.domain.dataobject.RoleDO;
import com.quanxiaoha.xiaohashu.user.biz.domain.dataobject.UserDO;

import java.util.List;

/**
 * @author 29567
 */
public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    UserDO selectByPhone(String phone);

    List<RoleDO> selectEnabledList();
}