package com.example.RecruitNewPeople.entity.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Repository;

@Data
@Repository
@EqualsAndHashCode
public class ReadExcel {
    @ExcelProperty(value = "学号",index = 6)
    private String student_id;
    @ExcelProperty(value = "姓名",index = 7)
    private String user_name;
    @ExcelProperty(value = "性别",index = 8)
    private String gender;
    @ExcelProperty(value = "学院",index = 9)
    private String college;
    @ExcelProperty(value = "专业",index = 10)
    private String major;
    @ExcelProperty(value = "联系方式",index = 11)
    private String phone;
    @ExcelProperty(value = "第一志愿部门",index = 12)
    private String first_intention;
    @ExcelProperty(value = "第二志愿部门",index = 13)
    private String second_intention;
    @ExcelProperty(value = "自我介绍",index = 14)
    private String introduction;
}
