package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv=new ModelAndView();
        if(ex instanceof NoLoginException){
            NoLoginException ne=(NoLoginException)ex;
            mv.setViewName("redirect:/index");
            return mv;
        }
        //默认异常处理
        mv.setViewName("error");
        mv.addObject("code",300);
        mv.addObject("msg","数据异常，请重试");

        if(handler instanceof HandlerMethod){
            //转换成controller方法对象
            HandlerMethod handlerMethod=(HandlerMethod)handler;
            //获取responsebody注解对象
            ResponseBody responseBody=handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);

            if(responseBody==null){
                if(ex instanceof ParamsException){
                    ParamsException pe=(ParamsException)ex;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg",pe.getMsg());
                }
                return mv;
            }else{
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常请重试");

                if(ex instanceof ParamsException){
                    ParamsException pe=(ParamsException)ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }
                response.setContentType("application/json;charset=utf-8");

                PrintWriter writer=null;

                try {
                    writer=response.getWriter();
                    writer.write(JSON.toJSONString(resultInfo));
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(writer!=null){
                        writer.close();
                    }
                }
                return null;
            }
        }
        return mv;
    }
}
