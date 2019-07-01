 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadService,itemCatService,typeTemplateService){

	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}


	//新增
	$scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象
		serviceObject=goodsService.add( $scope.entity  );//增加
		serviceObject.success(
			function(response){
				if(response.success){
					alert(response.message);
					//重新清空列表
					$scope.entity={};
				    editor.html('');//清空富文本编辑器
				}else{
					alert(response.message);
				}
			}
		);
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	/**
	 * 上传图片
	 */
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if(response.success){//如果上传成功，则取出url
				$scope.image_entity.url=response.message;
				}else{
					alert(response.message);
				}
			}
		).error(function () {
			alert("上传失败！");
		});
	}



	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面结构实体
	//添加图片列表
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}



	//列表中移除图片
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}

	//读取一级分类
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List=response;
			}
		);
	}


	//读取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		//根据选择的值查找耳机分类
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
			}
		);
	});

	//读取三级分类
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
		//根据选择的值查找耳机分类
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat3List=response;
			}
		);
	});


//三级分类选择后 读取模板 ID
	$scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {
		//alert(newValue);
		itemCatService.findOne(newValue).success(
			function(response){
				$scope.entity.goods.typeTemplateId=response.typeId; //更新模板 ID
			}
		);
	});


	/*//读取模板信息
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		//根据选择的值查找二分类
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate=response;//获取类型模板
				$scope.typeTemplate.brandIds=JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
			}
		);
	});*/

	$scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(
			function(response){
				$scope.typeTemplate=response;//获取类型模板
				$scope.typeTemplate.brandIds=JSON.parse( $scope.typeTemplate.brandIds);//品牌列表
				//扩展属性列表
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
			}
		);


		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList=response;
			}
		);

	});


	//选择根据详细规格
	$scope.updateSpecAttribute=function ($event,name,value) {
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if (object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);//添加规格XX的值
			}else{//取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);//去掉规格XX的值
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
					$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push(
				{"attributeName":name,"attributeValue":[value]}
			);
		}
	}




	//创建 SKU 列表
	$scope.createItemList=function () {
		//创建一个模板的列表
		$scope.entity.itemList=[{spec:{},price:0,num:99,status:'0',isDefault:'0'}];//初始化
		//点击的规格信息
		var items= $scope.entity.goodsDesc.specificationItems;
		//循环
		for(var i=0;i< items.length;i++){
			$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
		}
	}

	//创建新的
	addColumn=function (list,columnName,conlumnValues) {
		var newList=[];//先定义一个新的集合
		for(var i=0;i<list.length;i++){
			//获得旧的列表
			var oldRow=list[i];
			for(var j=0;j<conlumnValues.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);//将新列表添加进去
			}
		}
		return newList;
	}





});	
