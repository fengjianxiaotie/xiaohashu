package com.quanxiaoha.xiaohashu.kv.biz.service.impl;

import com.quanxiaoha.framework.common.exception.BizException;
import com.quanxiaoha.framework.common.response.Response;
import com.quanxiaoha.xiaohashu.kv.biz.domain.dataobject.NoteContentDO;
import com.quanxiaoha.xiaohashu.kv.biz.domain.repository.NoteContentRepository;
import com.quanxiaoha.xiaohashu.kv.biz.enums.ResponseCodeEnum;
import com.quanxiaoha.xiaohashu.kv.biz.service.NoteContentService;
import com.quanxiaoha.xiaohashu.kv.dto.req.AddNoteContentReqDTO;
import com.quanxiaoha.xiaohashu.kv.dto.req.DeleteNoteContentReqDTO;
import com.quanxiaoha.xiaohashu.kv.dto.req.FindNoteContentReqDTO;
import com.quanxiaoha.xiaohashu.kv.dto.resp.FindNoteContentRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * @author 周思预
 * @date 2025/5/30
 * @Description
 */
@Service
@Slf4j
public class NoteContentServiceImpl implements NoteContentService {
    @Resource
    private NoteContentRepository noteContentRepository;

    /**
     * @param addNoteContentReqDTO:
      * @return Response<?>
     * @author 29567
     * @description 增加笔记
     * @date 2025/5/30 17:21
     */
    @Override
    public Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO) {
        //笔记id
        UUID noteId = UUID.randomUUID();
        //笔记内容
        String content = addNoteContentReqDTO.getContent();
        //创建DO实例
        NoteContentDO noteContentDO = NoteContentDO.builder()
                .id(noteId)
                .content(content).build();
        //保存笔记
        noteContentRepository.save(noteContentDO);
        return Response.success();
    }

    /**
     * @param findNoteContentReqDTO:
      * @return Response<FindNoteContentRspDTO>
     * @author 29567
     * @description 查找笔记
     * @date 2025/5/30 17:22
     */
    @Override
    public Response<FindNoteContentRspDTO> findNoteContent(FindNoteContentReqDTO findNoteContentReqDTO) {
        //获取noteId
        String noteId = findNoteContentReqDTO.getNoteId();
        //获取optional
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(noteId));
        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }

        NoteContentDO noteContentDO = optional.get();
        // 构建返参 DTO
        FindNoteContentRspDTO findNoteContentRspDTO = FindNoteContentRspDTO.builder()
                .noteId(noteContentDO.getId().toString())
                .content(noteContentDO.getContent())
                .build();

        return Response.success(findNoteContentRspDTO);
    }

    /**
     * 删除笔记内容
     *
     * @param deleteNoteContentReqDTO
     * @return
     */
    @Override
    public Response<?> deleteNoteContent(DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        // 笔记 ID
        String noteId = deleteNoteContentReqDTO.getNoteId();
        // 删除笔记内容
        noteContentRepository.deleteById(UUID.fromString(noteId));

        return Response.success();
    }
}
