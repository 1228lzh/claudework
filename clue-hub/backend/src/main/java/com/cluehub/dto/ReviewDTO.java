package com.cluehub.dto;

import lombok.Data;

/**
 * 审核操作请求
 */
@Data
public class ReviewDTO {

    /** 操作：pass=通过, reject=不通过, return=退回补充 */
    private String action;

    /** 审核意见 */
    private String comment;

    /** 审核人姓名 */
    private String reviewerName;

    /** IPD立项时间（验证通过时填写） */
    private String ipdApprovedAt;

    /** 完结时间（验证通过时填写） */
    private String completedAt;
}
