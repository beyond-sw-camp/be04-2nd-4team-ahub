package com.teamphoenix.postreply.reply.command.controller;

import com.teamphoenix.postreply.reply.command.dto.ReplyDTO;
import com.teamphoenix.postreply.reply.command.service.ReplyService1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reply")
@Slf4j
public class ReplyController1 {

    private final ReplyService1 replyService;

    @Autowired
    public ReplyController1(ReplyService1 replyService) {
        this.replyService = replyService;
    }

    @PostMapping("/regist")
    public ResponseEntity<?> registReply(@RequestBody ReplyDTO registInfo) {

        List<ReplyDTO> replies = replyService.registReply(registInfo);

        return ResponseEntity.status(HttpStatus.OK).body(replies);
    }

    @PutMapping("/modify/{replyId}")
    public ResponseEntity<?> modifyReply(@RequestBody ReplyDTO modifyInfo, @PathVariable int replyId) {

        ReplyDTO reply = replyService.modifyReply(modifyInfo, replyId);

        return ResponseEntity.status(HttpStatus.OK).body(reply);
    }

    @DeleteMapping("/delete/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable int replyId){

        List<ReplyDTO> replies = replyService.deleteReply(replyId);

        return ResponseEntity.status(HttpStatus.OK).body(replies);
    }
}