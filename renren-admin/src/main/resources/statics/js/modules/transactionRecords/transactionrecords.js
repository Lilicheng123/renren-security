var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		transactionRecords: {},
        recordData:[],
		q:{
			name:'',
		},
        //分页参数
        page: {currentPage: 0, pageSizes: [20, 50, 100], pageSize: 20, total: 0, ascending: [], descending: []},
        dialogFormVisible:false,
        form:{name:'',},
        formLabelWidth: '120px'
	},
    //页面渲染完成后执行
    mounted: function () {
        this.query();
    },

	methods: {
		query: function () {
            //查询参数
            var queryParam = this.q;
            // 分页参数
            queryParam.page = this.page.currentPage;
            queryParam.limit = this.page.pageSize;
            //ajax 请求
            var self= this;
            $.ajax({
                type: "POST",
                url: baseURL + "transactionRecords/transactionrecords/list",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(queryParam),
                success: function (r) {
                    if (r.code == 0) {
                        self.page.total = r.page.totalCount;
                        self.recordData = r.page.list;
                        self.totalCount=r.totalCount;
                    } else {
                        alert(r.msg);
                    }
                }
            });
		},
		add: function(){
			vm.dialogFormVisible = true;
			vm.title = "新增";
			vm.transactionRecords = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.transactionRecords.id == null ? "transactionRecords/transactionrecords/save" : "transactionRecords/transactionrecords/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.transactionRecords),
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
				    url: baseURL + "transactionRecords/transactionrecords/delete",
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
			$.get(baseURL + "transactionRecords/transactionrecords/info/"+id, function(r){
                vm.transactionRecords = r.transactionRecords;
            });
		},
		reload: function (event) {
            vm.page={currentPage: 0, pageSizes: [20, 50, 100], pageSize: 20, total: 0, ascending: [], descending: []};
            vm.q= {
                name: ''
            },
            this.query();
		},

        // 页数变化
        handleSizeChange: function (val) {
            this.page.pageSize = val;
            this.reload();
        },
        //当前页变化
        handleCurrentChange: function (val) {
            this.page.currentPage = val;
            this.reload();
        },
		//筛选交易方向
        filterPayStatus:function (value, row) {
            return row.status === value;
        }
	}
});