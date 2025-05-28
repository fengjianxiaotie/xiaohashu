package com.quanxiaoha.xiaohashu.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quanxiaoha.xiaohashu.gateway.constant.RedisKeyConstants;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 周思预
 * @date 2025/5/27
 * @Description 自定义权限验证接口拓展
 */
@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * @param loginId:
    	 * @param loginType:
      * @return List<String>
     * @author 29567
     * @description 获取用户权限列表
     * @date 2025/5/27 16:08
     **/
    @Override
    @SneakyThrows
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 构建用户-角色 redis key
        String userRoleRedisKey = RedisKeyConstants.buildUserRoleKey(Long.valueOf(loginId.toString()));

        //从redis中获取角色列表
        String userRoleValues = redisTemplate.opsForValue().get(userRoleRedisKey);


        if (StringUtils.isBlank(userRoleValues)) {
            return null;
        }

        //将角色列表转换为List
        List<String> userRoles = objectMapper.readValue(userRoleValues, new TypeReference<>() {});

        if (CollUtil.isNotEmpty(userRoles)) {
            //获取所有role对应的permission
            List<String> userPermissions = redisTemplate
                    .opsForValue()
                    .multiGet(userRoles.stream()
                            .map(RedisKeyConstants::buildRolePermissionsKey)
                            .toList());
            if (CollUtil.isNotEmpty(userPermissions)) {
                List<String> permissions = Lists.newArrayList();

                // 遍历所有角色的权限集合，统一添加到 permissions 集合中
                userPermissions.forEach(jsonValue -> {
                    try {
                        // 将 JSON 字符串转换为 List<String> 权限集合
                        List<String> rolePermissions = objectMapper.readValue(jsonValue, new TypeReference<>() {});
                        permissions.addAll(rolePermissions);
                    } catch (JsonProcessingException e) {
                        log.error("==> JSON 解析错误: ", e);
                    }
                });

                // 返回此用户所拥有的权限
                log.info("此用户per为{}",permissions);
                return permissions;
            }
        }

        return null;
    }


    /**
     * @param loginId:
    	 * @param loginType:
      * @return List<String>
     * @author 29567
     * @description 获取用户角色列表
     * @date 2025/5/27 16:08
     **/
    @Override
    @SneakyThrows
    public List<String> getRoleList(Object loginId, String loginType) {
        // 构建用户-角色 redis key
        String userRoleRedisKey = RedisKeyConstants.buildUserRoleKey(Long.valueOf(loginId.toString()));

        //从redis中获取角色列表
        String userRoleValues = redisTemplate.opsForValue().get(userRoleRedisKey);


        if (StringUtils.isBlank(userRoleValues)) {
            return null;
        }

        return objectMapper.readValue(userRoleValues, new TypeReference<>() {
        });
    }
}
