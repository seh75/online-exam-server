package com.yb.onlineexamserver.mbg.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Created by Mybatis Generator on 2019/11/09
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Major implements Serializable {
    /**
     * 专业id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     * 科目id
     *
     * @mbg.generated
     */
    private Integer subjectId;

    /**
     * 专业名称
     *
     * @mbg.generated
     */
    private String name;

    /**
     * 创建时间
     *
     * @mbg.generated
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     *
     * @mbg.generated
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}