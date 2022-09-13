package com.xxxx.crm.controller;

import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;
    //多条件分页查询数据
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> queryByParams(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest request){
       if(flag!=null && flag==1){
           int id= LoginUserUtil.releaseUserIdFromCookie(request);
           saleChanceQuery.setAssignMan(id);
       }
        return saleChanceService.queryByParams(saleChanceQuery);
    }


    @PostMapping("updateDevResult")
    @ResponseBody
    public ResultInfo updateDevResult(Integer id,Integer devResult){
        saleChanceService.updateDevResult(id,devResult);
        return  success();
    }
    //打开营销管理界面
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }
    //打开营销机会修改/添加页面
    @RequestMapping("toAddUpdatePage")
    public String toAddUpdatePage(Integer id,HttpServletRequest request){
        if(id != null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            AssertUtil.isTrue(saleChance == null,"数据异常，请重试");
            request.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    //添加数据
    @PostMapping("save")
    @ResponseBody
    @RequirePermission(code="101002")
    public ResultInfo save(HttpServletRequest request, SaleChance saleChance){
        String userName = CookieUtil.getCookieValue(request,"userName");
        saleChance.setCreateMan(userName);
        saleChanceService.addSlaChance(saleChance);
        return success();
    }
    //update
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success();
    }
    @PostMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){

        return saleChanceService.queryAllSales();
    }
    //逻辑删除
    @RequestMapping("deleteBatch")
    @ResponseBody
    public ResultInfo deleteBatchs(Integer[] ids){
        saleChanceService.deleteBatchs(ids);
        return success();
    }

}
