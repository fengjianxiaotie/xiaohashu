package com.quanxiaoha.xiaohashu.kv.dto.resp;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 周思预
 * @date 2025/5/30
 * @Description 查找笔记响应内容
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNoteContentRspDTO {

    @NotBlank
    private String noteId;

    @NotBlank
    private String content;

}
