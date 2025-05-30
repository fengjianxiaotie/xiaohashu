package com.quanxiaoha.xiaohashu.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import com.quanxiaoha.framework.biz.context.holder.LoginUserContextHolder;
import com.quanxiaoha.framework.common.enums.DeletedEnum;
import com.quanxiaoha.framework.common.enums.StatusEnum;
import com.quanxiaoha.framework.common.exception.BizException;
import com.quanxiaoha.framework.common.response.Response;
import com.quanxiaoha.framework.common.util.JsonUtils;
import com.quanxiaoha.xiaohashu.auth.constant.RedisKeyConstants;
import com.quanxiaoha.xiaohashu.auth.enums.LoginTypeEnum;
import com.quanxiaoha.xiaohashu.auth.enums.ResponseCodeEnum;
import com.quanxiaoha.xiaohashu.auth.model.vo.user.UpdatePasswordReqVO;
import com.quanxiaoha.xiaohashu.auth.model.vo.user.UserLoginReqVO;
import com.quanxiaoha.xiaohashu.auth.rpc.UserRpcService;
import com.quanxiaoha.xiaohashu.auth.service.AuthService;
import com.quanxiaoha.xiaohashu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 周思预
 * @date 2025/5/22
 * @Description 实现登录注册功能
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRpcService userRpcService;
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
        Long userId=0L;
        if(loginTypeEnum == null) {
            return Response.fail("没有此种类型登录方式");
        }
        switch (loginTypeEnum){
            case VERIFICATION_CODE :

                //获取redis中key
                String redisKey = RedisKeyConstants.buildVerificationCodeKey(phone);

                //获取验证码
                String verificationCode = userLoginReqVO.getCode();

                // 校验入参验证码是否为空
                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

                //获取redisCode
                String redisCode = redisTemplate.opsForValue().get(redisKey);

                //判断是否正确
                if(!StringUtils.equals(redisCode,verificationCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }

                // RPC: 调用用户服务，注册用户
                Long userIdTmp = userRpcService.registerUser(phone);

                // 若调用用户服务，返回的用户 ID 为空，则提示登录失败
                if (Objects.isNull(userIdTmp)) {
                    throw new BizException(ResponseCodeEnum.LOGIN_FAIL);
                }

                userId = userIdTmp;
                break;
            case PASSWORD: // 密码登录
                String password = userLoginReqVO.getPassword();

                // RPC: 调用用户服务，通过手机号查询用户
                FindUserByPhoneRspDTO findUserByPhoneRspDTO = userRpcService.findUserByPhone(phone);

                // 判断该手机号是否注册
                if (Objects.isNull(findUserByPhoneRspDTO)) {
                    throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
                }

                // 拿到密文密码
                String encodePassword = findUserByPhoneRspDTO.getPassword();

                // 匹配密码是否一致
                boolean isPasswordCorrect = passwordEncoder.matches(password, encodePassword);

                // 如果不正确，则抛出业务异常，提示用户名或者密码不正确
                if (!isPasswordCorrect) {
                    throw new BizException(ResponseCodeEnum.PHONE_OR_PASSWORD_ERROR);
                }

                userId = findUserByPhoneRspDTO.getId();
            default:
                break;
        }

        // SaToken 登录用户，并返回 token 令牌
        // SaToken 登录用户, 入参为用户 ID
        log.info("userId为{}",userId);
        StpUtil.login(userId);

        // 获取 Token 令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 返回 Token 令牌
        return Response.success(tokenInfo.tokenValue);
    }

    /**
     * @param :
      * @return Response<?>
     * @author 29567
     * @description 登出功能
     * @date 2025/5/30 14:36
     */
    @Override
    public Response<?> logOut() {
        //从ThreadLocal中获取UserId
        Long userId = LoginUserContextHolder.getUserId();

        StpUtil.logout(userId);
        return Response.success();
    }

        /**
         * 修改密码
         *
         * @param updatePasswordReqVO
         * @return
         */
        @Override
        public Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO) {
            // 新密码
            String newPassword = updatePasswordReqVO.getNewPassword();
            // 密码加密
            String encodePassword = passwordEncoder.encode(newPassword);

            // RPC: 调用用户服务：更新密码
            userRpcService.updatePassword(encodePassword);

            return Response.success();
        }
    }
