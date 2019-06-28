//控制层
app.controller('brandController' ,function($scope,$controller   ,brandService){

    $controller('baseController',{$scope:$scope});//继承

    //查询品牌列表
    $scope.findAll=function() {
        brandService.findAll.success(
            function(response){
                $scope.list=response;
            }

        );
    };


    //分页
    $scope.findPage=function(page,size){
        brandService.findPage(page,size).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //保存
    $scope.save=function(id){
        var Object=brandService.add($scope.entity); //方法名称
        if($scope.entity.id!=null){//如果有id就是修改
            var Object=brandService.update($scope.entity);//则执行修改方法
        }
        Object.success(
            function (response) {
                if (response.success){
                    $scope.reloadList();//增加成功，重新加载
                } else{
                    alert(response.message);
                }
            }
        );
    }

    //查询实体
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function(response){
                $scope.entity= response;
            }
        );
    }



    //批量删除操作
    $scope.dele=function(){
        //获取选中的复选框
        brandService.dele($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity={};
    //条件查询
    $scope.search=function(page,size){
        brandService.search(page,size,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }


});
