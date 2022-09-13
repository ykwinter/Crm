layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        elem: '#saleChanceList', // 表格绑定的ID
        url : ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分页
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "saleChanceListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称', align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人', align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'uname', title: '指派人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d)
                {
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态',
                align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},

{title: '操作',
    templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
]]
});
    $("#btnSearch").click(function(){
        tableIns.reload({
            where: {
                customerName:$('[name="customerName"]').val(),
                createMan:$('[name="createMan"]').val(),
                state:$('[name="state"]').val()
            }
            ,page: {
                curr: 1
            }
        });
    });

    //监听表格的头部工具栏
    table.on('toolbar(saleChances)', function(obj){
        var checkStatus=table.checkStatus(obj.config.id)
        console.log(checkStatus.data);
        switch(obj.event){
            case 'add':
                openAddOrUpdateDialog();//打开添加修改的窗口页面
                break;
            case 'del':
               deleteSaleChancesBatch(checkStatus.data);
                break;
        };
    });

    //批量删除
    function deleteSaleChancesBatch(data){
        if(data.length==0){
            layer.msg("请至少选中一条数据");
            return ;

        }
        layer.confirm("你确定要删除选中的记录吗？",{
            btn:["确认","取消"],
        },function (index){
            layer.close(index);

            var str="ids=";
            for(var i=0;i<data.length;i++){
                if(i<data.length-1){
                    str +=data[i].id + "$ids=";
                }else{
                    str +=data[i].id;
                }
            }
            console.log(str);

            $.ajax({
                type:"post",
                url: ctx+"/sale_chance/deleteBatch",
                data:str,
                dataType:"json",
                success:function(data){
                    if(data.code==200){
                        tableIns.reload();

                    }else{
                        layer.msg(data.msg,{icon:5})
                    }
                }
            });
        })

    }


    //监听表格行工具栏
    table.on('tool(saleChances)',function(obj){
        if(obj.event == "edit"){
            openAddOrUpdateDialog(obj.data.id);
        }else if(obj.event=="del"){
            layer.confirm("确定要删除这条记录吗？",{icon:3,title:"营销机会数据管理"},function (index){
                layer.close(index);
                $.ajax({
                    type:"post",
                    url:ctx + "/sale_chance/deleteBatch",
                    data:{
                        ids:obj.data.id
                    },
                    dataType:"json",
                    success:function (result){
                        if(result.code==200){
                            tableIns.reload();
                            //obj.del();
                        }else{
                            layer.msg(result.msg,{icon:5});
                        }
                    }
                });
            });
        }
    });

    function openAddOrUpdateDialog(id){
        var title = "<h2>营销机会管理 - 机会添加</h2>";
        var url = ctx + "/sale_chance/toAddUpdatePage";

        if(id){
            title="<h2>营销机会管理 - 机会修改</h2>";
            url += "?id="+id;
        }

        layer.open({
            type:2,
            title:title,
            content: url,
            area:['500px','620px'],
            maxmin:true
        });
    }


    /**
     * 格式化分配状态
     * 0 - 未分配
     * 1 - 已分配
     * 其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if(state==1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }
    /**
     * 格式化开发状态
     * 0 - 未开发
     * 1 - 开发中
     * 2 - 开发成功
     * 3 - 开发失败
     * @param value
     * @returns {string}
     */
    function formatterDevResult(value){
        if(value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if(value==1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if(value==2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if(value==3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }
});
