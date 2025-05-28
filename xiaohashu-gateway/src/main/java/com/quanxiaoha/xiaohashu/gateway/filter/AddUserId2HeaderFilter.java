package com.quanxiaoha.xiaohashu.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author 周思预
 * @date 2025/5/28
 * @Description
 */
@Slf4j
@Component
public class AddUserId2HeaderFilter implements GlobalFilter {
    /**
     * 请求头中，用户 ID 的键
     */
    private static final String HEADER_USER_ID = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Long userId = null;
        log.info("==================> TokenConvertFilter");
        //从sa-token中获取userID
        try {
            //如果登录了，获取UserID
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            //若没有登录，则直接放行
            log.info("没有登录");
            return chain.filter(exchange);
        }

        //将userId放入newExchange中
        log.info("## 当前登录的用户 ID: {}", userId);

        Long finalUserId = userId;
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.header(HEADER_USER_ID, String.valueOf(finalUserId))) // 将用户 ID 设置到请求头中
                .build();
        return chain.filter(newExchange);
    }
}
