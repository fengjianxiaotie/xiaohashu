package com.quanxiaoha.xiaohashu.auth.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.quanxiaoha.framework.common.exception.BizException;
import com.quanxiaoha.framework.common.response.Response;
import com.quanxiaoha.framework.common.util.JsonUtils;
import com.quanxiaoha.xiaohashu.auth.constant.RedisKeyConstants;
import com.quanxiaoha.xiaohashu.auth.domain.dataobject.UserDO;
import com.quanxiaoha.xiaohashu.auth.domain.mapper.UserDOMapper;
import com.quanxiaoha.xiaohashu.auth.enums.LoginTypeEnum;
import com.quanxiaoha.xiaohashu.auth.enums.ResponseCodeEnum;
import com.quanxiaoha.xiaohashu.auth.model.vo.user.UserLoginReqVO;
import com.quanxiaoha.xiaohashu.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 周思预
 * @date 2025/5/22
 * @Description 实现登录注册功能
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private UserDOMapper userDOMapper;

    /**
     * @param userLoginReqVO:
      * @return Response<String>
     * @author 29567
     * @description 登录注册功能
     * @date 2025/5/22 21:23
     */
    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        Integer loginType = userLoginReqVO.getType();
        String phone = userLoginReqVO.getPhone();
        //判断登录类型
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(loginType);

        switch (loginTypeEnum){
            case VERIFICATION_CODE :

                //获取redis中key
                String redisKey = RedisKeyConstants.buildVerificationCodeKey(phone);

                //获取验证码
                String verificationCode = userLoginReqVO.getCode();
                if(verificationCode==null) throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID);

                //获取redisCode
                String redisCode = redisTemplate.opsForValue().get(redisKey);

                //判断是否正确
                if(!StringUtils.equals(redisCode,verificationCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }

                //判断是否注册
                UserDO userDO = userDOMapper.selectByPhone(phone);
                //如果不存在则注册
                if(userDO == null){
                    //TODO
                } else {
                    // 已注册，则获取其用户 ID
                    Long userId = userDO.getId();
                }
                log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));
                break;
            case PASSWORD: // 密码登录
                // todo

                break;
            default:
                break;
        }

        // SaToken 登录用户，并返回 token 令牌
        // todo

        return Response.success("");
    }
}
