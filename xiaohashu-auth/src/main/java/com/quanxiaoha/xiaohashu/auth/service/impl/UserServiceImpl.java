package com.quanxiaoha.xiaohashu.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.quanxiaoha.framework.common.enums.DeletedEnum;
import com.quanxiaoha.framework.common.enums.StatusEnum;
import com.quanxiaoha.framework.common.exception.BizException;
import com.quanxiaoha.framework.common.response.Response;
import com.quanxiaoha.framework.common.util.JsonUtils;
import com.quanxiaoha.xiaohashu.auth.constant.RedisKeyConstants;
import com.quanxiaoha.xiaohashu.auth.constant.RoleConstants;
import com.quanxiaoha.xiaohashu.auth.domain.dataobject.UserDO;
import com.quanxiaoha.xiaohashu.auth.domain.dataobject.UserRoleDO;
import com.quanxiaoha.xiaohashu.auth.domain.mapper.UserDOMapper;
import com.quanxiaoha.xiaohashu.auth.domain.mapper.UserRoleDOMapper;
import com.quanxiaoha.xiaohashu.auth.enums.LoginTypeEnum;
import com.quanxiaoha.xiaohashu.auth.enums.ResponseCodeEnum;
import com.quanxiaoha.xiaohashu.auth.model.vo.user.UserLoginReqVO;
import com.quanxiaoha.xiaohashu.auth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    @Resource
    private UserRoleDOMapper userRoleDOMapper;
    @Resource
    private TransactionTemplate transactionTemplate;


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
        if(loginTypeEnum == null) return Response.fail("没有此种类型登录方式");
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

                //判断是否注册
                UserDO userDO = userDOMapper.selectByPhone(phone);
                //如果不存在则注册
                if(userDO == null){
                    //系统自动注册
                    userId=registerUser(phone);
                } else {
                    // 已注册，则获取其用户 ID
                     userId = userDO.getId();
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
        // SaToken 登录用户, 入参为用户 ID
        StpUtil.login(userId);

        // 获取 Token 令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        // 返回 Token 令牌
        return Response.success(tokenInfo.tokenValue);
    }

    public Long registerUser(String phone) {
        return transactionTemplate.execute(status -> {
            try{
                //获取redis中自增id
                Long id=redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHASHU_ID_GENERATOR_KEY);
                //创建用户对象
                UserDO userDO=UserDO.builder()
                        .phone(phone)
                        .xiaohashuId(String.valueOf(id)) // 自动生成小红书号 ID
                        .nickname("小红薯" + id) // 自动生成昵称, 如：小红薯10000
                        .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(DeletedEnum.NO.getValue()) // 逻辑删除
                        .build();
                // 添加入库
                userDOMapper.insert(userDO);
                // 获取刚刚添加入库的用户 ID
                Long userId = userDO.getId();

                // 给该用户分配一个默认角色
                UserRoleDO userRoleDO = UserRoleDO.builder()
                        .userId(userId)
                        .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .isDeleted(DeletedEnum.NO.getValue())
                        .build();
                userRoleDOMapper.insert(userRoleDO);

                // 将该用户的角色 ID 存入 Redis 中
                List<Long> roles = Lists.newArrayList();
                roles.add(RoleConstants.COMMON_USER_ROLE_ID);
                String userRolesKey = RedisKeyConstants.buildUserRoleKey(phone);
                redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));
                return userId;
            }catch (Exception e){
                status.setRollbackOnly(); // 标记事务为回滚
                log.error("==> 系统注册用户异常: ", e);
                return null;
            }
    });
    }
}
