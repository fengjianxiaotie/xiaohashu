package com.quanxiaoha.xiaohashu.auth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 周思预
 * @date 2025/5/22
 * @Description 登录类型
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    VERIFICATION_CODE(1),
    PASSWORD(2);


    private final Integer value;

    public static LoginTypeEnum valueOf(Integer value){
        for (LoginTypeEnum loginTypeEnum : LoginTypeEnum.values()) {
            if(loginTypeEnum.value.equals(value)) {
                return loginTypeEnum;
            }
        }
        return null;
    }

}
