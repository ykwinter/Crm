package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map queryByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object> map=new HashMap<>();
        //分页开启
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        List<SaleChance> saleChances=saleChanceMapper.queryByParams(saleChanceQuery);
        //按照分页条件，格式化数据
        PageInfo<SaleChance> saleChancePageInfo =new PageInfo<>(saleChances);

        map.put("code",0);
        map.put("msg","");
        map.put("count",saleChancePageInfo.getTotal());
        map.put("data",saleChancePageInfo.getList());
        return map;
    }
    //更新开发状态
    public void updateDevResult(Integer id,Integer devResult){
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(id==null || null==saleChance,"更新数据不存在");
        AssertUtil.isTrue(null == devResult,"更新状态不存在");

        saleChance.setDevResult(devResult);
        saleChance.setUpdateDate(new Date());
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"状态更新失败");
    }

    //添加数据
    public void addSlaChance(SaleChance saleChance){
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());

        saleChance.setIsValid(1);
        saleChance.setUpdateDate(new Date());
        saleChance.setCreateDate(new Date());

        if(StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }else{
            saleChance.setAssignTime(new Date());
            saleChance.setState(1);
            saleChance.setDevResult(1);
        }
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)<1,"营销机会数据添加失败");

    }
    //修改数据
    public void updateSaleChance(SaleChance saleChance){
        //判断id
        AssertUtil.isTrue(saleChance.getId() == null,"数据异常，请重试");
        //检验非空
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //默认值
        saleChance.setUpdateDate(new Date());

        SaleChance dbSaleChance =saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(dbSaleChance==null,"数据异常，请重试");

        if(StringUtils.isBlank(dbSaleChance.getAssignMan())){

            if(!StringUtils.isBlank(saleChance.getAssignMan())){
                saleChance.setAssignTime(new Date());
                saleChance.setState(1);
                saleChance.setDevResult(1);
            }
        }else{
            if(StringUtils.isBlank(saleChance.getAssignMan())){
                saleChance.setAssignTime(null);
                saleChance.setState(0);
                saleChance.setDevResult(0);
            }else{
                if(!dbSaleChance.getAssignMan().equals(saleChance.getAssignMan())){
                    saleChance.setAssignTime(new Date());

                }else{

                    saleChance.setAssignTime(new Date());
                }
            }
        }
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"营销数据修改失败");
    }
    //查询销售人员
    public List<Map<String,Object>> queryAllSales(){
        return saleChanceMapper.queryAllSales();
    }
    //逻辑删除
    public void deleteBatchs(Integer[] ids){
        AssertUtil.isTrue(ids==null || ids.length==0,"未选中删除数据");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length,"数据删除失败");
    }


    private void checkParams(String customerName,String linkMan,String linkPhone){
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"手机号码不能为空");
        //检验手机号规范
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号不符合规范");
    }

}
