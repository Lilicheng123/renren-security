$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'ticket/ticket/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', index: 'id', width: 50, key: true },
			{ label: '编号', name: 'code', index: 'code', width: 80 },
            { label: '推荐价', name: 'advicePrice', index: 'advice_price', width: 80 },
			{ label: '0：正常，1：删除', name: 'delFlag', index: 'del_flag', width: 80 }			
        ],
		viewrecords: true,
        height: window.innerHeight - 120,
        rowNum: 25,
        rowList : [25,50,75],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
		postData:{
        	code:'',
		},
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		editVisible:false,
		title: null,
		ticket: {},
        formLabelWidth: '120px',
        q:{
			code:'',
		}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.editVisible = true;
			vm.title = "新增";
			vm.ticket = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
            vm.title = "修改";
            vm.getInfo(id)
            vm.editVisible = true;
		},
		saveOrUpdate: function (event) {
			var url = vm.ticket.id == null ? "ticket/ticket/save" : "ticket/ticket/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.ticket),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "ticket/ticket/delete",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get(baseURL + "ticket/ticket/info/"+id, function(r){
                vm.ticket = r.ticket;
            });
		},
		reload: function (event) {
            vm.editVisible=false;
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page,
				postData:{
                	'code':vm.q.code,
				}
            }).trigger("reloadGrid");
		}
	}
});