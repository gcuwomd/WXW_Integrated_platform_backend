package com.example.RecruitNewPeople.entity.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class throughMembers {
    @ExcelProperty("姓名")
    private String user_name;
    @ExcelProperty("学号")
    private String student_id;
    @ExcelProperty("介绍")
    private String introduction;
    @ExcelProperty("专业")
    private String major;
    @ExcelProperty("学院")
    private String college;
    @ExcelProperty("第一志愿")
    private String first_intention;
    @ExcelProperty("第二志愿")
    private String second_intention;
    @ExcelProperty("电话")
    private String phone;
    @ExcelProperty("性别")
    private String gender;
    @ExcelProperty("录用部门")
    private String departmentName;

}
