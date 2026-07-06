package com.cluehub.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 线索提报请求
 */
@Data
public class ClueSubmitDTO {

    // ===== 第一步：谁报的 =====
    @NotBlank(message = "报备人不能为空")
    private String reporterName;
    private String reporterDept;
    private String reporterContact;
    private String wecomUserId;

    // ===== 第二步：什么线索 =====
    @NotBlank(message = "线索名称不能为空")
    private String clueName;
    @NotBlank(message = "线索类型不能为空")
    private String clueType;
    private String clueTypeOther;
    @NotBlank(message = "线索描述不能为空")
    private String clueDesc;

    // ===== 第三步：线索来源 =====
    private String infoSource;
    private String infoSourceOther;
    private String reliability;
    private String marketSize;

    // ===== 第四步：线索判断 =====
    private String productLines;
    private String productLinesDetail;
    private String targetCustomers;
    private String targetCustomersOther;
    private String urgency;
    private String productStatus;
    private String productStatusDetail;

    /** 补充信息（退回补充时填写） */
    private String supplementInfo;

    /** 补充材料类型，多个用逗号分隔 */
    private String supplementMaterialTypes;

    /** 提交类型：submit=直接提交, draft=保存草稿 */
    private String action;
    /** 草稿记录ID（更新草稿时传入） */
    private Long draftId;
}
