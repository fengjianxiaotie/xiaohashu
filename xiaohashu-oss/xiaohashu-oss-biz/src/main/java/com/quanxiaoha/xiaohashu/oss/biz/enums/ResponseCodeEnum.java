package com.quanxiaoha.xiaohashu.oss.biz.enums;

import com.quanxiaoha.framework.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 周思预
 * @date 2025/5/30
 * @Description 存储服务业务异常状态码
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {
    // ----------- 通用异常状态码 -----------
    SYSTEM_ERROR("OSS-10000", "出错啦，后台小哥正在努力修复中..."),
    PARAM_NOT_VALID("OSS-10001", "参数错误"),
    ;
    //异常码
    private final String errorCode;
    //异常信息
    private final String errorMessage;

}
