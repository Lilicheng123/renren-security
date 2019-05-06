var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		orderInfo: {},
        goodsData:[], //商品列表
        addVisible:false,
        payDialogVisible:false, //支付dialog
        //查询参数
		q: {
			orderNo: '',
			memberName: '',
			payment:'',
            startTime: '',
            endTime: '',
            createTime:null,
            productKey:'' //商品搜索关键字
		},
		//订单信息
        orderData:[],
        order:{orderInfo:{},orderItemData:[]},
		//商品明细
		orderItemData:[],
		//分页参数
        page: {currentPage: 0, pageSizes: [20, 50, 100], pageSize: 20, total: 0, ascending: [], descending: []},
	},
	//页面渲染完成后执行
    mounted: function () {
        this.query();
    },
    computed:{
	    total:function () {
            let total=0;
            for(var i=0;i<this.orderItemData.length;i++){
                total=Number(this.orderItemData[i].amount)+total;
            }
            return total;
        }
    },
	methods: {
		query: function () {
            //查询参数
            var queryParam = this.q;
            // 录入时间
            var createTime = queryParam.createTime;
            if (!isBlank(createTime)){
                queryParam.startTime = createTime[0] + ' 00:00:00';
                queryParam.endTime = createTime[1] + ' 23:59:59';
            }
            // 分页参数
            queryParam.page = this.page.currentPage;
            queryParam.limit = this.page.pageSize;
            //ajax 请求
            var self= this;
            $.ajax({
                type: "POST",
                url: baseURL + "orderInfo/orderinfo/list",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify(queryParam),
                success: function (r) {
                    if (r.code == 0) {
                        self.page.total = r.page.totalCount;
                        self.orderData = r.page.list;
                        self.totalCount=r.totalCount;
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        reset:function(){
		    vm.page={currentPage: 0, pageSizes: [20, 50, 100], pageSize: 20, total: 0, ascending: [], descending: []};
		    vm.q={orderNo: '',
                memberName: '',
                payment:'',
                startTime: '',
                endTime: '',
                createTime:null};
		    this.query();
		},
		add: function(){
			var url="product/product/productlist";
			var data={"key":vm.q.productKey};
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function(r){
                    if(r.code === 0){
                        vm.goodsData=r.goodsData;
                        vm.orderInfo = {};
                        vm.addVisible = true;
                    }else{
                        alert(r.msg);
                    }
                }
            });
		},
		//查询商品
        queryGoods: function(){
                var url="product/product/productlist";
                var data={"key":vm.q.productKey};
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function(r){
                        if(r.code === 0){
                            vm.goodsData=r.goodsData;
                        }else{
                            alert(r.msg);
                        }
                    }
                });
        },



		update: function (event) {
			var orderNo = getSelectedRow();
			if(orderNo == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(orderNo)
		},
		save: function (event) {
			var url = "orderInfo/orderinfo/save";
			vm.order.orderItemData=vm.orderItemData;
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.order),
			    success: function(r){
			    	if(r.code === 0){
			    	    vm.orderInfo=r.orderInfo;
                        vm.addVisible=false;
                        Vue.nextTick(function () {
                            vm.payDialogVisible=true;
                        },2000)
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var orderNos = getSelectedRows();
			if(orderNos == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "orderInfo/orderinfo/delete",
                    contentType: "application/json",
				    data: JSON.stringify(orderNos),
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
		getInfo: function(orderNo){
			$.get(baseURL + "orderInfo/orderinfo/info/"+orderNo, function(r){
                vm.orderInfo = r.orderInfo;
            });
		},
		reload: function (event) {
			vm.reset();
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
		//筛选订单支付状态
        filterPayStatus:function (value, row) {
            return row.payStatus === value;
        },
        //新增商品
        addProduct:function (row) {
            var item={skuId:'',name:'',detailedinfo:'',quantity:1,price:'',amount:'',goodsImgUrl:''};
            var self =this;
            item.price=row.price;
            item.amount=(item.quantity*item.price).toFixed(2);
            item.skuId=row.skuId;
            item.detailedinfo=row.detailedinfo;
            item.name=row.name;
            item.goodsImgUrl=row.goodsImgUrl;
            self.orderItemData.push(item);
        },
        //新增，修改商品及其数量
        changeNums:function (row,index) {
		   row.amount=(row.price*row.quantity).toFixed(2);
        },
	}
});