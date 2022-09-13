package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.query.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    public ResultInfo loginCheck(String userName, String userPwd){
        //检验参数
        checkLoginData(userName,userPwd);
        //调用dao层查询数据库
        User user=userMapper.queryUserByName(userName);
        //判断账号是否存在
        AssertUtil.isTrue(user == null,"账号不存在");
        //检验前台数据进行比较
        checkLoginPwd(user.getUserPwd(),userPwd);
        //封装ResultInfo对象给前台
        ResultInfo resultInfo = buildResultInfo(user);
        return resultInfo;



    }
    //修改密码
    public void userUpdate(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        //确保用户是否是登录状态获取cookie中的id 非空 查询数据库
        AssertUtil.isTrue(userId == null,"用户未登录");
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user==null,"用户状态异常");

        //检验密码数据
        checkUpdateData(oldPassword,newPassword,confirmPassword,user.getUserPwd());

        user.setUserPwd(Md5Util.encode(newPassword));
        user.setUpdateDate(new Date());
        //判断是否成功
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"密码修改失败");

    }
    //密码校验
    private void checkUpdateData(String oldPassword,String newPassword,String confirmPassword,String dbPassword){
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原密码不存在");
        AssertUtil.isTrue(!dbPassword.equals(Md5Util.encode(oldPassword)),"原密码错误");
        //新密码
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码不能为空");
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新老密码不能一样");
        //
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"确认密码不能为空");
        AssertUtil.isTrue(!confirmPassword.equals(newPassword),"确认密码必须和新密码一致");
    }

    //添加用户
    public void saveUser(User user){
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()),"用户名称不能为空");
        AssertUtil.isTrue(null !=userMapper.queryUserByName(user.getUserName()),"用户名已存在");

        checkUserParams(user.getEmail(), user.getPhone());
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //执行添加操作
        //AssertUtil.isTrue(userMapper.insertSelective(user)<1,"用户添加失败");
        //执行添加操作，设置对应sql属性，主键返回到user对象中
        AssertUtil.isTrue(userMapper.insertHasKey(user)<1,"用户添加失败");
        //绑定角色给用户
        relationUserRole(user.getId(),user.getRoleIds());
    }
    //给用户绑定角色
    private void relationUserRole(Integer id, String roleIds) {

            Integer count=userRoleMapper.countUserRole(id);
            if(count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUid(id) !=count,"原有角色删除失败");
            }
            AssertUtil.isTrue(roleIds==null,"角色不存在");
            List<UserRole> urs=new ArrayList<>();
            //切割获取到每个id
        String[] splits=roleIds.split(",");
        for(String idStr:splits){
            UserRole userRole=new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(Integer.parseInt(idStr));
            userRole.setUpdateDate(new Date());
            userRole.setCreateDate(new Date());

            urs.add(userRole);

        }
        AssertUtil.isTrue(userRoleMapper.insertBatch(urs) != splits.length,"角色绑定失败");
    }


    //修改用户
    public void updateUser(User user) {
        //id
        AssertUtil.isTrue(null==user.getId() || null== userMapper.selectByPrimaryKey(user.getId()),"数据异常请重试");
        //用户名
        AssertUtil.isTrue(user.getUserName()==null,"用户名不能为空");
        //名称唯一
        User dbUser = userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue(dbUser != null && user.getId() != dbUser.getId(),"用户名已存在");
        //检验
        checkUserParams(user.getEmail(),user.getPhone());
        //设默认值
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"用户修改失败");

        relationUserRole(user.getId(),user.getRoleIds());
    }



    //校验用户添加和修改的数据
    private void checkUserParams(String email,String phone){
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式错误");
    }

    //批量删除
    public void deleteUsers(Integer[] ids){
        AssertUtil.isTrue(ids==null || ids.length<1,"未选中删除数据");
        AssertUtil.isTrue(userMapper.deleteUsers(ids) != ids.length,"用户删除失败");
    }

    //多条件分页查询
    public Map<String,Object> queryUserBYParams(UserQuery query){
        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        List<User> users = userMapper.queryUserBYParams(query);
        //按照分页条件，格式化数据
        PageInfo<User> userPageInfo = new PageInfo<>(users);

        map.put("code",0);
        map.put("msg","");
        map.put("count",userPageInfo.getTotal());
        map.put("data",userPageInfo.getList());
        return map;
    }

    private ResultInfo buildResultInfo(User user){
        ResultInfo resultInfo=new ResultInfo();
        //封装
        UserModel userModel=new UserModel();
        String id = UserIDBase64.encoderUserID(user.getId());
        userModel.setUserId(id);
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());

        resultInfo.setResult(userModel);
        return resultInfo;

    }
    private void checkLoginPwd(String dbPwd,String userPwd){
        String encodePwd = Md5Util.encode(userPwd);
        //校验
        AssertUtil.isTrue(!encodePwd.equals(dbPwd),"用户密码错误");
    }


    private void checkLoginData(String userName,String userPwd){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"密码不能为空");
    }


}
