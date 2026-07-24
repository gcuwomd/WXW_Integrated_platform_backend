package com.example.RecruitNewPeople.entity.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
public class Users {
    private String student_id;
    private String username;
    private String introduction;
    private String major;
    private String college;
    private String firstIntention;
    private String secondIntention;
    private String phone;
    private String gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer MessageStatus;

    public Users(String student_id, String user_name, String introduction, String major, String college, String firstIntention, String secondIntention, String gender, String phone) {
        this.student_id = student_id;
        this.username = user_name;
        this.introduction = introduction;
        this.major = major;
        this.college = college;
        this.firstIntention = firstIntention;
        this.secondIntention = secondIntention;
        this.gender = gender;
        this.phone = phone;
    }
}
