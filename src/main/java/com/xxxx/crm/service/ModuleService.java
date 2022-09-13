package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    public List<TreeModel> queryAllModules(Integer rId){
        AssertUtil.isTrue(rId==null || roleMapper.selectByPrimaryKey(rId)==null,"角色不存在");
        List<Integer> mIds=permissionMapper.selectPermissionByRid(rId);
        List<TreeModel> treeModels=moduleMapper.queryAllModules();
        for (TreeModel treeModel:treeModels){
            Integer id=treeModel.getId();
            if(mIds.contains(id)){
                treeModel.setChecked(true);
                treeModel.setOpen(true);
            }
        }
        return treeModels;
    }

    //查询所有资源 资源管理使用
    public Map<String,Object> queryModules(){
        Map<String,Object> map=new HashMap<>();
        List<Module> modules=moduleMapper.queryModules();
        AssertUtil.isTrue(modules==null || modules.size()<1,"资源数据异常");
        //准备前台需要的数据接口
        map.put("code",0);
        map.put("msg","");
        map.put("count",modules.size());
        map.put("data",modules);
        return map;
    }
    @Transactional
    public void moduleAdd(Module module) {
        //层级
        AssertUtil.isTrue(module.getGrade()==null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade()==0 || module.getGrade()==1 || module.getGrade() ==2),"层级有误");

        //模块名称
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        Module dbModule=moduleMapper.queryModulByGradeAName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule !=null,"模块名称已存在");

        //二级菜单URL
        if(module.getGrade()==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"模块地址不为空");
            dbModule=moduleMapper.queryModulByGradeAUrl(module.getGrade(),module.getUrl());
            AssertUtil.isTrue(dbModule !=null,"地址已存在，请重新输入");
        }
        //父级菜单
        if(module.getGrade()==1 || module.getGrade()==2){
            AssertUtil.isTrue(module.getParentId()==null,"父类ID不能为空");
            dbModule=moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule==null,"父类ID不存在");

        }
        //权限码
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        dbModule = moduleMapper.queryModulByOptValue(module.getOptValue());
        AssertUtil.isTrue(dbModule!=null,"权限码已存在");

        //默认值
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        //执行添加操作  判断受影响行数
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"模块添加失败");

    }
    @Transactional
    public void moduleUpdete(Module module) {
        AssertUtil.isTrue(module.getId()==null,"待删除的资源不存在");
        Module dbModule=moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(dbModule==null,"系统异常");


        //层级
        AssertUtil.isTrue(module.getGrade()==null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade()==0 || module.getGrade()==1 || module.getGrade() ==2),"层级有误");

        //模块名称
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        dbModule=moduleMapper.queryModulByGradeAName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule !=null && !(module.getId().equals(dbModule.getId())),"模块名称已存在");

        //二级菜单URL
        if(module.getGrade()==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"模块地址不为空");
            dbModule=moduleMapper.queryModulByGradeAUrl(module.getGrade(),module.getUrl());
            AssertUtil.isTrue(dbModule !=null && !(module.getId().equals(dbModule.getId())),"地址已存在，请重新输入");
        }
        //父级菜单
        if(module.getGrade()==1 || module.getGrade()==2){
            AssertUtil.isTrue(module.getParentId()==null,"父类ID不能为空");
            dbModule=moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule==null && !(module.getId().equals(dbModule.getId())),"父类ID不存在");

        }
        //权限码
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        dbModule = moduleMapper.queryModulByOptValue(module.getOptValue());
        AssertUtil.isTrue(dbModule!=null && !(module.getId().equals(dbModule.getId())),"权限码已存在");

        //默认值

        module.setUpdateDate(new Date());

        //执行修改操作
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"资源修改失败");

    }


    //逻辑删除资源
    @Transactional
    public void moduledelete(Integer mId){
        //判断参数id非空
        AssertUtil.isTrue(mId == null,"系统异常，请重试");
        AssertUtil.isTrue(selectByPrimaryKey(mId)==null,"待删除数据不存在");

        //查询当前id下是否有子模块
        Integer count = moduleMapper.queryCountModuleByParentId(mId);
        AssertUtil.isTrue(count>0,"该模块下存在子模块，不能删除");

        //查询权限表中(角色和资源)是否包含当前模块的数据，有则删除
        count = permissionMapper.queryCountByMoudleId(mId);
        if(count > 0){
            AssertUtil.isTrue(permissionMapper.deletePermissionByMoudleId(mId) !=count,"权限删除失败");
        }
        //删除资源
        AssertUtil.isTrue(moduleMapper.deleteModuleByMid(mId)<1,"资源删除失败");

    }
}
