package com.quanxiaoha.xiaohashu.auth.model.vo.user;

import com.quanxiaoha.framework.common.validator.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 周思预
 * @date 2025/5/22
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginReqVO {
    @PhoneNumber
    private String phone;

    private String code;

    private String password;

    @NotNull(message = "登录类型不能为空")
    private Integer type;
}
