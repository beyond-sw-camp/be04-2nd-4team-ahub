package com.teamphoenix.ahub.fair.query.controller;

import com.teamphoenix.ahub.fair.query.dto.FairDTO;
import com.teamphoenix.ahub.fair.query.service.FairService;
import com.teamphoenix.ahub.fair.query.vo.ResponseFindPost;
import com.teamphoenix.ahub.fair.query.vo.ResponseList;
import com.teamphoenix.ahub.fair.query.vo.ResponseSearchList;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@RestController(value = "queryFairController")
@RequestMapping("/fairs")
@Slf4j
public class FairController {

    private final FairService fairService;
    private final ModelMapper modelMapper;

    @Autowired
    public FairController(FairService fairServiceImpl, ModelMapper modelMapper) {
        this.fairService = fairServiceImpl;
        this.modelMapper = modelMapper;
    }

    // 게시글을 클릭하면 내용을 호출하는 핸들러 메소드
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseFindPost> findFairPost(@PathVariable(value = "postId") int postId) {

        FairDTO fairDTO = fairService.getFairPost(postId);
        ResponseFindPost result = new ResponseFindPost();

        // 예외 발생 시 반환할 if문
        if(fairDTO == null){
            ResponseList respMessage = new ResponseList();

            result.setCode("204, No Content");
            result.setMessage("There are No Contents about [" + postId + "] fair post.");
            result.setUrl("http://localhost:8000/board/fairs/lists");
            result.setResult(respMessage);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        ResponseList respMessage = modelMapper.map(fairDTO, ResponseList.class);

        // 사진 기능 구현이 안되서 임시로 섬네일 넣는 부분
        if(respMessage.getFairId() == 1)
            respMessage.setThumImage("https://www.hotelrestaurant.co.kr/data/photos/20230206/art_16761266140036_c0dcfe.jpg");
        if(respMessage.getFairId() == 2)
            respMessage.setThumImage("https://www.hotelrestaurant.co.kr/data/photos/20240104/art_17063431368517_6a19fd.jpg");
        if(respMessage.getFairId() == 3)
            respMessage.setThumImage("https://bsiwse.com/data/designImages/POPUP_1709860556.png");
        if(respMessage.getFairId() == 4)
            respMessage.setThumImage("https://tong.visitkorea.or.kr/cms/resource/69/3091569_image2_1.jpg");
        if(respMessage.getFairId() == 5)
            respMessage.setThumImage("https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FUSb3P%2FbtsDAxeyESy%2FbMqddMHke4x4HZih1quo9k%2Fimg.png");
        if(respMessage.getFairId() == 6)
            respMessage.setThumImage("https://akei.or.kr/data/file/schedule/3718762006_8uPasgFS_7bcb347f62f20f9f672000d83c975129129a8cb7.jpg");
        if(respMessage.getFairId() == 7)
            respMessage.setThumImage("https://tkfile.yes24.com/upload2/PerfBlog/202208/20220811/20220811-43163.jpg");
        if(respMessage.getFairId() == 8)
            respMessage.setThumImage("https://img.youtube.com/vi/RGOUS60dAYE/mqdefault.jpg");
        //
        result.setCode("200, OK");
        result.setMessage("Success to Read [" + postId + "] fair post.");
        result.setUrl("http://localhost:8000/board/fairs/lists");
        result.setResult(respMessage);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 검색 조건에 해당하는 게시글 리스트 반환하는 메소드
    @GetMapping("/lists")
    public ResponseEntity<ResponseSearchList> findPostsByCondition(
            @RequestParam(value = "search_type") String type,
            @RequestParam(value = "search_condition", required = false) String condition) {

        // 게시글 검색 타입(제목내용, 제목, 내용, 작성자)에 따라 검색 조건을 담는다.
        FairDTO searchInfo = new FairDTO();
        if(type.equals("titleContent")) {
            searchInfo.setFairTitle(condition);
            searchInfo.setFairContent(condition);
        }
        if(type.equals("title")) {
            searchInfo.setFairTitle(condition);
        }
        if(type.equals("content")) {
            searchInfo.setFairContent(condition);
        }
        if(type.equals("writerId")) {
            searchInfo.setWriterId(condition);
        }

        //담은 검색조건을 서비스단으로 넘겨서 결과를 받아옴
        List<FairDTO> resultList = fairService.findPostsByCondition(searchInfo);

        // service 통신 결과 예외 발생 시 넘길 if문
        if(resultList == null) {
            ResponseSearchList result = new ResponseSearchList();
            result.setCode("204, No Content");
            result.setMessage("No Contents to read");
            result.setUrl("http://localhost:8000/board/fairs/{postId}");
            result.setResult(null);

            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(result);
        }

        List<ResponseList> responseLists = doDTOToList(resultList);

        ResponseSearchList result = new ResponseSearchList();
        result.setCode("200, OK");
        result.setMessage("Success to Read list.");
        result.setUrl("http://localhost:8000/board/fairs/{postId}");
        result.setResult(responseLists);

        return ResponseEntity
                .ok()
                .body(result);
    }

    // List<FairDTO>를 List<ResponseList> 로 바꿔주는 메소드
    private List<ResponseList> doDTOToList(List<FairDTO> fairList) {
        List<ResponseList> responseLists = new ArrayList<>();
        for (FairDTO fairDTO : fairList) {
            ResponseList responseList = modelMapper.map(fairDTO, ResponseList.class);
            String returnString = fairDTO.getFairContent().substring(0, Math.min(fairDTO.getFairContent().length(), 70)) + "...";
            responseList.setFairContent(returnString);
            String writeDate = fairDTO.getFairWritedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            responseList.setFairWritedate(writeDate);

            // 사진 기능 구현이 안되서 임시로 섬네일 넣는 부분
            if(responseList.getFairId() == 1)
                responseList.setThumImage("https://www.hotelrestaurant.co.kr/data/photos/20230206/art_16761266140036_c0dcfe.jpg");
            if(responseList.getFairId() == 2)
                responseList.setThumImage("https://www.hotelrestaurant.co.kr/data/photos/20240104/art_17063431368517_6a19fd.jpg");
            if(responseList.getFairId() == 3)
                responseList.setThumImage("https://bsiwse.com/data/designImages/POPUP_1709860556.png");
            if(responseList.getFairId() == 4)
                responseList.setThumImage("https://tong.visitkorea.or.kr/cms/resource/69/3091569_image2_1.jpg");
            if(responseList.getFairId() == 5)
                responseList.setThumImage("https://img1.daumcdn.net/thumb/R800x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FUSb3P%2FbtsDAxeyESy%2FbMqddMHke4x4HZih1quo9k%2Fimg.png");
            if(responseList.getFairId() == 6)
                responseList.setThumImage("https://akei.or.kr/data/file/schedule/3718762006_8uPasgFS_7bcb347f62f20f9f672000d83c975129129a8cb7.jpg");
            if(responseList.getFairId() == 7)
                responseList.setThumImage("https://tkfile.yes24.com/upload2/PerfBlog/202208/20220811/20220811-43163.jpg");
            if(responseList.getFairId() == 8)
                responseList.setThumImage("https://img.youtube.com/vi/RGOUS60dAYE/mqdefault.jpg");
            //
            responseLists.add(responseList);
        }

        return responseLists;
    }

}
