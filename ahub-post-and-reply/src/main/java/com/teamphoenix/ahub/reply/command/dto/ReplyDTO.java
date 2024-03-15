package com.teamphoenix.ahub.reply.command.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReplyDTO {

    private int replyId;
    private int postId;
    private String replyContent;
    private LocalDateTime replyDate;
    private int reportAcceptance;
    private int useAcceptance;
    private int memberCode;

}
