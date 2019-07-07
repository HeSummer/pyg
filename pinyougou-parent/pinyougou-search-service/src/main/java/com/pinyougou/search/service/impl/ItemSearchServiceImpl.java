package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     *  搜索
     *
     * */
    @Override
    public Map<String, Object> search(Map searchMap) {

        //处理搜索内容包含空格问题
        String keywords =(String)searchMap.get("keywords");

        searchMap.put("keywords",keywords.replace(" ", ""));//将字符串中所有的空格去掉



        Map<String, Object> map = new HashMap();


        //1:查询列表
        map.putAll(searchList(searchMap));

        //2.根据关键字查询商品分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3.查询品牌和规格列表
        String categoryName=(String)searchMap.get("category");//获得分类名称
        if(!"".equals(categoryName)){//如果有分类名称
            map.putAll(searchBrandAndSpecList(categoryName));
        }else{//如果没有分类名称，按照第一个查询
            if (categoryList.size()>0){
                String name=(String) categoryList.get(0);
                map.putAll(searchBrandAndSpecList(name));
            }
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 删除数据
     * @param goodsIdList
     */
    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("删除商品 ID"+goodsIdList);
        Query query=new SimpleQuery();
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    /***
     * 根据关键字搜索列表
     * 高亮搜索
     * @param searchMap
     * */
    private Map searchList(Map searchMap){

        Map<String, Object> map = new HashMap();

        HighlightQuery query=new SimpleHighlightQuery();

        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项

        //按照关键字搜索
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.2 按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3 按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4 按规格筛选
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map)searchMap.get("spec");
            for(String key:specMap.keySet()){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5 按价格筛选
       if(!"".equals(searchMap.get("price"))){
           String[] price = ((String)searchMap.get("price")).split("-");
           //如果区间起点不等于 0
           if (!price[0].equals("0")){
               Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);  // greaterThanEqual  大于等于
               FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
               query.addFilterQuery(filterQuery);
           }
           //如果区间终点不等于*
           if(!price[1].equals("*") ){
               Criteria filterCriteria=new Criteria("item_price").lessThanEqual(price[1]);  // greaterThanEqual  大于等于
               FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
               query.addFilterQuery(filterQuery);
           }
       }

        //1.6  分页查询
        Integer pageNo=(Integer)searchMap.get("pageNo");//提取页码
        if(pageNo==null){
            pageNo=1;   //默认第一页
        }

        Integer pageSize=(Integer) searchMap.get("pageSize");//记录每页数
        if (pageSize==null){
            pageSize=20;//默认20
        }

        query.setOffset((pageNo-1)*pageSize);//从第几页开始查  起始页= （当前页-1）* 每页显示的记录数
        query.setRows(pageSize); //设置查询多少行数据

        //1.7 排序
        String sortValue=(String)searchMap.get("sort");         // ASC  升序   DESC 降序
        String sortField=(String)searchMap.get("sortField");    // 排序字段

        if(sortValue!=null && !sortValue.equals("")){
            if (sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);   //Sort.Direction.ASC 选择排序的方式是升序
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);   //Sort.Direction.ASC 选择排序的方式是升序
                query.addSort(sort);
            }
        }


        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //循环高亮入口集合(每条记录)
        List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();
        for(HighlightEntry<TbItem> h : highlighted){

            // List<HighlightEntry.Highlight> highlights = h.getHighlights();  highlights  是list 是因为高亮域的个数
            //List<String> snipplets = highlights.get(0).getSnipplets();   snipplets 是list 是因为每个域可能有多个值，比如复制域
            TbItem item = h.getEntity();//获得元实体类
            if (h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size() >0){//避免错误
                item.setTitle( h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }

        }

        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());  //返回总页数
        map.put("total",page.getTotalElements());    //返回总记录数
        return map;
    }

    /**
     * 查询分类列表
     * @param searchMap
     * Category 类别
     * @return
     */

    private List searchCategoryList(Map searchMap){
        List list=new ArrayList();
        //为了方便这里用solr的分组来对类别进行分类

        Query query=new SimpleQuery();

        //按条件查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分页组
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> item = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for(GroupEntry<TbItem> entity :content){
                list.add(entity.getGroupValue());//将分组结果的名称封装到返回值中
        }

        return list;
    }


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        Map map=new HashMap();
        //获得到模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if(typeId!=null){
        //根据模板ID获得品牌
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);//返回值添加品牌列表
        map.put("brandList", brandList);//返回值添加品牌列表
            System.out.println("brandList : "+brandList);
        //根据模板 ID 查询规格列表
        List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);//返回值添加品牌列表
        map.put("specList",specList);
            System.out.println("specList : "+specList);
        }

        return map;
    }







}
