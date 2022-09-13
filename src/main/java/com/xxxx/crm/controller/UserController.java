package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @PostMapping("login")
    @ResponseBody
    public ResultInfo login(String userName,String userPwd){
       /* ResultInfo resultInfo=new ResultInfo();
        try {
            resultInfo=userService.loginCheck(userName,userPwd);
        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(400);
            resultInfo.setMsg(e.getMsg());
        }catch(Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("登录失败");
        }
        return resultInfo;*/
        return userService.loginCheck(userName,userPwd);
    }
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(HttpServletRequest request,String oldPassword,String newPassword,String confirmPassword){
        /*ResultInfo resultInfo=new ResultInfo();

        int id= LoginUserUtil.releaseUserIdFromCookie(request);
        try {
            userService.userUpdate(id,oldPassword,newPassword,confirmPassword);
        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(400);
            resultInfo.setMsg(e.getMsg());
        }catch(Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败");
        }
        return resultInfo;*/
       // int i=1/0;
        int id=LoginUserUtil.releaseUserIdFromCookie(request);
            userService.userUpdate(id,oldPassword,newPassword,confirmPassword);
            return success();
    }
    //跳转用户模块首页
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @PostMapping("save")
    @ResponseBody
    public ResultInfo saveUser(User user){
        userService.saveUser(user);
        return success("用户添加成功");
    }

    @PostMapping("updateUser")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户修改成功");
    }

    //打开添加/修改页面
    @RequestMapping("toUpdateAddPage")
    public String toUpdateAddPage(Integer id,HttpServletRequest request){
        if(id !=null){
            User user=userService.selectByPrimaryKey(id);
            AssertUtil.isTrue(user==null,"数据异常请重试");
            request.setAttribute("user",user);
        }
        return "user/add_update";
    }
    //批量删除
    @PostMapping("deleteBatch")
    @ResponseBody
    public ResultInfo deleteUsers(Integer[] ids){
        userService.deleteUsers(ids);
        return success();
    }


    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery query){
        return userService.queryUserBYParams(query);
    }

    //打开修改密码页面
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }


}
