package com.teamphoenix.ahub.reply.query;

import com.teamphoenix.ahub.reply.command.repository.ReplyRepository;
import com.teamphoenix.ahub.reply.query.controller.ReplyController;
import com.teamphoenix.ahub.reply.query.dto.ReplyDTO;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MybatisTest {

    private final ReplyController replyController;

    private final ReplyRepository replyRepository;

    private ModelMapper mapper;

    @Autowired
    public MybatisTest(ReplyController replyController, ReplyRepository replyRepository, ModelMapper mapper) {
        this.replyController = replyController;
        this.replyRepository = replyRepository;
        this.mapper = mapper;
    }

    @Test
    @Transactional
    public void 회원_별_댓글_조회_테스트() {
        //given
        int memberId = 2;
        List<ReplyDTO> foundReplies = replyRepository.findByMemberCode(memberId).stream().map(reply -> mapper.map(reply, ReplyDTO.class)).toList();

//        foundReplies.stream().map(reply -> mapper.map(reply, ReplyDTO.class)).collect(Collectors.toList());

        //when
        List<ReplyDTO> replies = replyController.selectReplyByWriter(memberId);

        //then
        assertEquals(foundReplies,replies);

    }

    @Test
    public void 게시판_별_댓글_조회_테스트() {
        int boardId = 2;
        List<ReplyDTO> replies = replyController.selectAllReplyInBoard(boardId);

        assertTrue(!replies.isEmpty());
    }

    @Test
    public void 댓글_검색기능_테스트() {
        String inputValue = "test";
        List<ReplyDTO> replies = replyController.selectReplyBySearch(inputValue);

        assertTrue(!replies.isEmpty());
    }
}
