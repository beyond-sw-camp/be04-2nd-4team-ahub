package com.teamphoenix.ahub.reply.command.repository;

import com.teamphoenix.ahub.reply.command.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {
    List<Reply> findByMemberCode(int memberCode);
}
