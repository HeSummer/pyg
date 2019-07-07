/*

import domain.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    //添加 一个信息
    public void test(){
        TbItem item = new TbItem();
        item.setId(1L);
        item.setBrand("小米");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("小米旗舰店");
        item.setTitle("小米7");
        item.setPrice(new BigDecimal(3000));
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }


    @Test
    //通过主键来查询
    public void selectId(){
        TbItem byId = solrTemplate.getById(1, TbItem.class);
        System.out.println(byId.getBrand()+"=="+byId.getTitle()+"=="+byId.getPrice());
    }

    @Test
    //批量插入
    public void insertInto(){
        for(int x=0;x<1000;x++){
            TbItem item = new TbItem();
            item.setId(new Long(x));
            item.setBrand("小米");
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setSeller("小米旗舰店");
            item.setTitle("小米"+x);
            item.setPrice(new BigDecimal(3000+x));
            solrTemplate.saveBean(item);
            solrTemplate.commit();
        }


    }



    @Test
    //分页查询代码
    public void testpageQuery(){

        Query query=new SimpleQuery("*:*");//全部
        query.setOffset(20);//开始的索引
        query.setRows(20);//每页显示的记录数
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数："+page.getTotalElements());
        List<TbItem> list=page.getContent();
        showList(list);

    }

    //分页条件查询
    @Test
    public void findpageQuery(){
        Query query=new SimpleQuery("*:*");
        query.setOffset(0);
        query.setRows(100);

        Criteria criteria=new Criteria("item_title").contains("22");
        query.addCriteria(criteria);

        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> list = tbItems.getContent();
        showList(list);
    }




    private void showList(List<TbItem> list){
        for(TbItem item:list){
            System.out.println(item.getTitle() +item.getPrice());
        }
    }


    @Test
    //删除全部
    public void dele(){
        Query query=new SimpleQuery("*:*");

        solrTemplate.delete(query);
        solrTemplate.commit();

    }

}

*/
