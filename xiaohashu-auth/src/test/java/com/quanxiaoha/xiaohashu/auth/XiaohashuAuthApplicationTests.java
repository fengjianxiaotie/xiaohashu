package com.quanxiaoha.xiaohashu.auth;

import com.quanxiaoha.framework.common.util.JsonUtils;
import com.quanxiaoha.xiaohashu.auth.domain.dataobject.UserDO;
import com.quanxiaoha.xiaohashu.auth.domain.mapper.UserDOMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
@Slf4j
class XiaohashuAuthApplicationTests {

    @Resource
    private UserDOMapper userDOMapper;

    /**
     * 测试插入数据
     */
    @Test
    void testInsert() {
        UserDO userDO = UserDO.builder()
                .username("犬小哈")
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        userDOMapper.insert(userDO);
    }

    @Test
    void testSelect() {
        // 查询主键 ID 为 4 的记录
        UserDO userDO = userDOMapper.selectByPrimaryKey(2L);
        log.info("User: {}", JsonUtils.toJsonString(userDO));
    }

//    @Test
//    void testUpdate() {
//        UserDO userDO = UserDO.builder()
//                .id(1L)
//                .username("犬小哈教程")
//                .updateTime(LocalDateTime.now())
//                .build();
//
//        // 根据主键 ID 更新记录
//        userDOMapper.updateByPrimaryKey(userDO);
//    }

    @Test
    void testDelete() {
        // 删除主键 ID 为 4 的记录
        userDOMapper.deleteByPrimaryKey(1L);
    }
}
