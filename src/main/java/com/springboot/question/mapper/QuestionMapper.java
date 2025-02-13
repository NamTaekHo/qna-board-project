package com.springboot.question.mapper;

import com.springboot.question.dto.QuestionDto;
import com.springboot.question.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    Question questionPostToQuestion(QuestionDto.Post postDto);
}
