var app = angular.module('pinyougou',[]);//定义品优购模块  不分页的

//$sce 编写过滤器

app.filter('trustHtml',['$sce',function ($sce) {//引入filter 服务
    return function (data) {//传入参数时被过滤的内容
        return $sce.trustAsHtml(data);//返回的是过滤后的内容  也是html信任的内容
    }


}]);