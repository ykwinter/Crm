package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;
    //查询所有资源
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer rId){
        return moduleService.queryAllModules(rId);
    }

    //查询所有资源 资源管理使用
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryModules(){
        return moduleService.queryModules();
    }

    @RequestMapping("add")
    @ResponseBody
    public ResultInfo moduleAdd(Module module){
        moduleService.moduleAdd(module);
        return success("资源添加成功");
    }
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo moduleUpdate(Module module){
        moduleService.moduleUpdete(module);
        return success("资源修改成功");
    }
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo moduledelete(Integer mId){
        moduleService.moduledelete(mId);
        return success("资源删除成功");
    }


    //跳转到模块页面
    @RequestMapping("index")
    public String toIndex(){
        return "module/module";
    }

    @RequestMapping("toAdd")
    public String toAdd(Integer grade, Integer parentId, HttpServletRequest request){
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);

        return "module/add";
    }
    @RequestMapping("updateModulePage")
    public String toAdd(Integer id, HttpServletRequest request){
        Module module =moduleService.selectByPrimaryKey(id);
        request.setAttribute("module",module);

        return "module/update";
    }

}
