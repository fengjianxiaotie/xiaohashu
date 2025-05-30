package com.quanxiaoha.xiaohashu.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 周思预
 * @date 2025/5/30
 * @Description 更改用户密码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserPasswordDTO {

    @NotBlank(message = "密码不能为空")
    private String encodePassword;

}
