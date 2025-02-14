package com.springboot.question.mapper;

import com.springboot.answer.dto.AnswerDto;
import com.springboot.answer.entity.Answer;
import com.springboot.member.entity.Member;
import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "member.memberId", source = "memberId")
    Question questionPostToQuestion(QuestionDto.Post postDto);
    @Mapping(target = "member.memberId", source = "memberId")
    Question questionPatchToQuestion(QuestionDto.Patch patchDto);
    AnswerDto.Response answerToAnswerResponse(Answer answer);
    @Mapping(target = "answer", source = "answer")
    @Mapping(target = "memberId", source = "member.memberId")
    QuestionDto.Response questionToQuestionResponse(Question question);
    List<QuestionDto.Response> questionsToQuestionResponses(List<Question> questions);
//    default List<Question> questionsToSecretTitleQuestions(List<Question> questions, Member currentMember){
//        questions.stream().forEach(
//                question -> question.setTitle(question.getDisplayTitle(currentMember))
//        );
//        return questions;
//    }

}
