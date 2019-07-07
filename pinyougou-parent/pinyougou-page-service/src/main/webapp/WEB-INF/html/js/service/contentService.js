app.service('contentService',function($http){


    //根据分类广告查询id
    this.findByCategoryId=function(categoryId){
        return $http.get('content/findByCategoryId.do?categoryId='+categoryId);
    }


});