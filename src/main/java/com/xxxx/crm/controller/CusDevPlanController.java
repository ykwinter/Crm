package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    @Resource
    private CusDevPlanService cusDevPlanService;
    @RequestMapping("index")
    public String toIndex(){
        return "cusDevPlan/cus_dev_plan";
    }
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer sId, HttpServletRequest request){
        System.out.println(sId);
        AssertUtil.isTrue(sId==null,"数据异常，请重试");
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sId);
        System.out.println(saleChance);
        if(saleChance!=null){
            request.setAttribute("saleChance",saleChance);
        }
        return "cusDevPlan/cus_dev_plan_data";
    }

    //多条件分页查询客户计划表
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryByParams(CusDevPlanQuery query){
        return cusDevPlanService.queryByParams(query);
    }
    //计划向数据添加
    @PostMapping("save")
    @ResponseBody
    public ResultInfo addCusDevPan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPan(cusDevPlan);
        return success("计划添加成功");
    }
    //计划项数据修改
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPan(cusDevPlan);
        return success("计划修改成功");
    }

    //计划项数据修改
    @RequestMapping("toAddOrUpdatePage")
    public String updateCusDevPan(Integer id,Integer sId, HttpServletRequest request){
        if(id!=null){
            CusDevPlan cusDevPlan=cusDevPlanService.selectByPrimaryKey(id);
            AssertUtil.isTrue(null ==cusDevPlan,"计划项数据异常请重试");
            request.setAttribute("cusDevPlan",cusDevPlan);
        }
        request.setAttribute("sId",sId);
        return "cusDevPlan/add_update";
    }

    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id){
        cusDevPlanService.delete(id);
        return success();
    }
}
