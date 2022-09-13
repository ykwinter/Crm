layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    //加载下拉框
    $.post(ctx+"/sale_chance/queryAllSales",function (data){
        var am=$("#assignMan");
        var aid=$("#assignId").val();
        if(data != null){
            for(var i=0;i<data.length;i++){
                if(aid == data[i].id){
                    var opt ="<option selected value="+data[i].id+">"+data[i].name+"</option>";

                }else{
                    var opt="<option value="+data[i].id+">"+data[i].name+"</option>";
                }
                am.append(opt);
            }
        }
        layui.form.render("select");
    })


    /**
     * 监听submit事件
     * 实现营销机会的添加与更新
     */
    form.on("submit(addOrUpdateSaleChance)", function (data) {

        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });
        var url=ctx + "/sale_chance/save";

        if($("#hidId").val()){
            url= ctx + "/sale_chance/update";

        }
        console.log(data.field);
        //发送请求
        $.post(url,data.field,function (data){
            if(data.code==200){
                //关上弹框
                layer.close(index);
                //关闭iframe层
                layer.closeAll("iframe");
                parent.location.reload();

            }else{
                layer.msg(data.msg,{icon:5})
            }
        });

        return false; // 阻止表单提交
    });
});
