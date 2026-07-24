package com.example.RecruitNewPeople.Service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.example.RecruitNewPeople.Service.DepartmentService;
import com.example.RecruitNewPeople.entity.pojo.Image;
import com.example.RecruitNewPeople.entity.pojo.Users;
import com.example.RecruitNewPeople.entity.pojo.Volunteer;
import com.example.RecruitNewPeople.mapper.DepartmentMapper;
import com.example.RecruitNewPeople.mapper.UserMapper;
import com.example.RecruitNewPeople.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    DepartmentMapper departmentMapper;
    @Override
    public boolean nextDepartment(String id) {
        // 先获取当前待处理(status=0)记录的pk_id
        Integer pkId = departmentMapper.getPkIdByStudentId(id);
        // 将当前status记录标记为未通过(status=2)
        departmentMapper.changeToNoPassStatus(pkId, id);
        // 插入新记录：取第二志愿的department_id，status默认为0
        return departmentMapper.nextDepartment(id);
    }

    @Override
    public ResultUtil choosePassPerson(String departmentId) {
        if (departmentExist(departmentId)){
            List list=departmentMapper.choosePassPerson(departmentId);
            JSONArray array = this.personDescript(list);
            return  ResultUtil.success(array);
        }

        return new ResultUtil(200,"部门编号错误",null);
    }

    @Override
    public ResultUtil chooseNoPassPerson(String departmentId) {
        if (departmentExist(departmentId)){
            List list=departmentMapper.chooseNotPassPerson(departmentId);
            JSONArray array = this.personDescript(list);
            return  ResultUtil.success(array);
        }

        return new ResultUtil(200,"部门编号错误",null);
    }

    @Override
    public ResultUtil chooseWillPassPerson(String departmentId) {
        if (departmentExist(departmentId)){
            List list=departmentMapper.chooseWillPassPerson(departmentId);
            JSONArray array = this.personDescript(list);
            return  ResultUtil.success(array);
        }

        return new ResultUtil(200,"部门编号错误",null);
    }

    @Override
    public boolean changeStatus(String id, Integer status) {
        // 先获取当前待处理(status=0)记录的pk_id
        Integer pkId = departmentMapper.getPkIdByStudentId(id);
        if (pkId == null) {
            return false;
        }
        if (status == 1) {
            return departmentMapper.changeToPassStatus(pkId, id);
        } else if (status == 2) {
            return departmentMapper.changeToNoPassStatus(pkId, id);
        } else if (status == 0) {
            departmentMapper.updateStatusToUnprocessed(pkId, id);
            return true;
        }
        return false;
    }
    boolean departmentExist(String departmentId){
        return departmentMapper.departmentExist(departmentId);
    }
    JSONArray personDescript(List list){
        JSONArray array_userInfo=new JSONArray();
        for (Object item:list){
            JSONObject personSingle = (JSONObject) JSON.toJSON((Users)item);
            JSONArray volunteerArray = new JSONArray();
            JSONObject image=new JSONObject();
            // 从volunteer表获取志愿信息
            List list_volunteer = userMapper.getVolunteer(((Users) item).getStudent_id());
            for (Object user : list_volunteer) {
                JSONObject volObj = new JSONObject();
                volObj.put("level", ((Volunteer) user).getLevel());
                volObj.put("departmentId", ((Volunteer) user).getDepartmentId());
                volunteerArray.add(volObj);
            }
            List  list_image= userMapper.getImageById(((Users)item).getStudent_id());
            int i=1;
            for(Object images:list_image){
                image.put((Integer.toString(i++)),(((Image)images).getUrl()));
            }
            personSingle.put("volunteer", volunteerArray);
            personSingle.put("image",image);
            array_userInfo.add(personSingle);

        };
        return array_userInfo;
    }


}
