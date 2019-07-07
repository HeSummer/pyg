package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {


    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;


    /**
     * 导入商品数据
     */
    public void importItemData(){
        dele();
        int num=0;//记录数
        //查询出所有信息
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //插入进solr

        System.out.println("开始插入数据。。。");
        for (TbItem item:tbItems){
            num=num+1;
            System.out.println(item.getTitle());
            Map specMap= JSON.parseObject(item.getSpec());//将 spec 字段中的 json 字符串转换为 map
            item.setSpecMap(specMap);//给带注解的字段赋值
        }

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
        System.out.println("插入数据结束,共计"+num+"条数据！！");
    }

    public static void main(String[] args) {
        ApplicationContext context=new
                ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil= (SolrUtil) context.getBean("solrUtil");
        //solrUtil.importItemData();
        solrUtil.dele();
    }



  public void dele(){
      System.out.println("开始清除...");
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
        solrTemplate.commit();
      System.out.println("清除完毕...");
    }


}


