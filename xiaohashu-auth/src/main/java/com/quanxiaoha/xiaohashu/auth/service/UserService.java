package com.quanxiaoha.xiaohashu.auth.service;

import com.quanxiaoha.framework.common.response.Response;
import com.quanxiaoha.xiaohashu.auth.model.vo.user.UserLoginReqVO;

/**
 * @author 周思预
 * @date 2025/5/22
 * @Description
 */
public interface UserService {

    /**
     * @param userLoginReqVO:
      * @return Response<String>
     * @author 29567
     * @description 登录与注册
     * @date 2025/5/22 21:00
     */
    Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO);
}
