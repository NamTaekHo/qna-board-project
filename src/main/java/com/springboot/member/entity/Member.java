package com.springboot.member.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.like.entity.Like;
import com.springboot.question.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, updatable = false, unique = true)
    @Email(message = "올바르지 않은 형식의 이메일입니다.")
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 13, nullable = false, unique = true)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Question> questions = new ArrayList<>();

//    @OneToMany(mappedBy = "member")
//    private List<Like> likes = new ArrayList<>();

    public void setQuestion(Question question) {
        if (question.getMember() != this) {
            question.setMember(this);
        }
        if (!this.questions.contains(question)) {
            this.questions.add(question);
        }
    }


    public enum MemberStatus {
        MEMBER_ACTIVE("일반 회원"),
        MEMBER_QUIT("탈퇴 회원");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }


}
