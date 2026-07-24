package com.example.RecruitNewPeople.Service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.RecruitNewPeople.Service.UserService;
import com.example.RecruitNewPeople.entity.pojo.*;
import com.example.RecruitNewPeople.mapper.UserMapper;
import com.example.RecruitNewPeople.utils.OssUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    OssUtil ossUtil;


    /**
     * 查询所有报名情况
     */
    @Override
    public PageBean allInfo(Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        //查看是否存在组织
        List<Users> list = userMapper.getAllInfo();
        Page<Users> p = (Page<Users>) list;
        JSONArray info = this.personDescript(list);
        PageBean pageBean = new PageBean(info,p.getTotal());
        return pageBean;
    }

    @Override
    public JSONObject departmentData() {
        List list = userMapper.statistics();
        JSONArray departmentData = new JSONArray();
        for (Object item : list) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON((Statistics) item);
            departmentData.add(jsonObject);
        }
        JSONObject total = new JSONObject();
        total.put("department", departmentData);
        total.put("total", userMapper.totalNumber());
        return total;
    }

    /**
     * @param departmentId 根据部门id选取出部门的对应部门的报名人员信息
     */
    @Override
    public JSONArray InfoByDepartment(String departmentId) {
        List<Users> infoByDepartment = userMapper.getInfoByDepartment(departmentId);
        JSONArray info = this.personDescript(infoByDepartment);
        return info;
    }

    @Override
    public boolean register(JSONObject json) {
        String student_id = json.getString("student_id");
        String user_name = json.getString("username");
        String introduction = json.getString("introduction");
        String major = json.getString("major");
        String college = json.getString("college");
        String gender = json.getString("gender");
        String phone = json.getString("phone");
        String first_intention = json.getString("firstIntention");
        String second_intention = json.getString("secondIntention");

        userMapper.registerUser(student_id, user_name, introduction, major, college, first_intention, second_intention, gender, phone);
        // 优先处理volunteer数组格式（前端新格式）
        JSONArray volunteerArray = json.getJSONArray("volunteer");
        String firstDeptId = null;
        if (volunteerArray != null && !volunteerArray.isEmpty()) {
            for (int i = 0; i < volunteerArray.size(); i++) {
                JSONObject volItem = volunteerArray.getJSONObject(i);
                String level = volItem.getString("level");
                String departmentId = volItem.getString("departmentId");
                if (departmentId != null && !departmentId.isEmpty()) {
                    userMapper.insertVolunteer(student_id, departmentId, level);
                    if (firstDeptId == null) {
                        firstDeptId = departmentId;
                    }
                }
            }
        } else {
            // 兼容旧的firstIntention/secondIntention格式
            if (first_intention != null && !first_intention.isEmpty()) {
                userMapper.insertVolunteer(student_id, first_intention, "1");
                firstDeptId = first_intention;
            }
            if (second_intention != null && !second_intention.isEmpty()) {
                userMapper.insertVolunteer(student_id, second_intention, "2");
            }
        }
        // 写入status表，取第一志愿作为department_id，状态默认为0（未处理）
        if (firstDeptId != null && !firstDeptId.isEmpty()) {
            userMapper.InsertStatus(student_id, firstDeptId);
        }
        return true;
    }

    @Override
    public JSONObject getUserByIp(String id){
        UserGetById userGetById = userMapper.userGetById(id);

        JSONObject json = (JSONObject) JSON.toJSON(userGetById);
        // 从volunteer表获取志愿信息并拼装
        JSONArray volunteerArray = new JSONArray();
        List<Volunteer> volunteerList = userMapper.getVolunteer(id);
        for (Volunteer v : volunteerList) {
            JSONObject volObj = new JSONObject();
            volObj.put("level", v.getLevel());
            volObj.put("departmentId", v.getDepartmentId());
            volunteerArray.add(volObj);
        }
        json.put("volunteer", volunteerArray);
        return json;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Users updatedUser, ArrayList updatedVolunteerList) {
        userMapper.updateUser(updatedUser);
        String id = updatedUser.getStudent_id();
        for (Object item : updatedVolunteerList) {
            JSONObject object = (JSONObject) JSON.toJSON(item);
            Integer level = Integer.parseInt(object.getString("level"));
            userMapper.updateVolunteer(updatedVolunteerList);
            if (level == 1) {
                userMapper.insertStatus(id, object.getString("volunteer"));
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id){
        userMapper.deleteUser(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVolunteer(String id){
        userMapper.deleteVolunteer(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(String id){
        userMapper.deleteImage(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStatus(String id){
        userMapper.deleteStatus(id);
    }

    @Override
    public boolean rememberInComment(Comment comment) {
       return userMapper.rememberInComment(comment);
    }

    @Override
    public List<Comment> getComment(String id) {
        return userMapper.getComment(id);
    }


    /**
     * @param key 根据关键词查找用户信息
     */

    @Override
    public JSONArray InfoByPersonal(String key) {
        List<Users> infoByDepartment = userMapper.getInfoByKey(key);
        JSONArray info = this.personDescript(infoByDepartment);
        return info;
    }

    @Override
    public boolean OssPut(MultipartFile image, String id) {
        URL url = ossUtil.put(image);
        String urls = url.toString();
        if (userMapper.addImage(id, urls)) return true;
        return false;
    }

    JSONArray personDescript(List list) {
        JSONArray array_userInfo = new JSONArray();
        for (Object item : list) {
            JSONObject personSingle = (JSONObject) JSON.toJSON((Users) item);
            JSONArray volunteerArray = new JSONArray();
            JSONObject image = new JSONObject();
            List list_volunteer = userMapper.getVolunteer(((Users) item).getStudent_id());
            for (Object user : list_volunteer) {
                JSONObject volObj = new JSONObject();
                volObj.put("level", ((Volunteer) user).getLevel());
                volObj.put("departmentId", ((Volunteer) user).getDepartmentId());
                volunteerArray.add(volObj);
            }
            List list_image = userMapper.getImageById(((Users) item).getStudent_id());
            int i = 1;
            for (Object images : list_image) {
                image.put((Integer.toString(i++)), (((Image) images).getUrl()));
            }
            personSingle.put("volunteer", volunteerArray);
            personSingle.put("image", image);
            array_userInfo.add(personSingle);

        }
        return array_userInfo;
    }

    @Override
    public List<WriteExcel> getExcelData() {
        List<WriteExcel> userData = userMapper.fetchUserData();
        if (userData == null){
            return null;
        }
        return userData;
    }

    @Override
    public List<throughMembers> throughMembers(String departmentId){
        List<throughMembers> throughMembers = userMapper.throughMembers(departmentId);
        if (throughMembers == null){
            return null;
        }
        return throughMembers;
    }
}
