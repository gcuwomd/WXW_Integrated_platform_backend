package com.example.RecruitNewPeople.mapper;


import com.example.RecruitNewPeople.entity.pojo.Department;
import com.example.RecruitNewPeople.entity.pojo.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    boolean nextDepartment(String id);
     List<Users> choosePassPerson(String departmentId);
    List<Users> chooseNotPassPerson(String departmentId);
    List<Users> chooseWillPassPerson(String departmentId);

    Integer getPkIdByStudentId(String id);
    boolean changeToPassStatus(@Param("pkId") Integer pkId, @Param("id") String id);
    boolean changeToNoPassStatus(@Param("pkId") Integer pkId, @Param("id") String id);
    void updateStatusToUnprocessed(@Param("pkId") Integer pkId, @Param("id") String id);
    boolean departmentExist(String departmentId);
    boolean userExist(String id);

    boolean selectMessageId(String messageId);

    List<Department> getDepartment();
}
