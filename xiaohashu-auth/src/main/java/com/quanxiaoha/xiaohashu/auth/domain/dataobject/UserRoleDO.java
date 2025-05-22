package com.quanxiaoha.xiaohashu.auth.domain.dataobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleDO {
    private Long id;

    private Long userId;

    private Long roleId;

    private Date createTime;

    private Date updateTime;

    private Boolean isDeleted;
}