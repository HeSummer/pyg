package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;




    //生成html
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //创建连接对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //创建模板
            Template template = configuration.getTemplate("item.ftl");
            //创建数据源map
            HashMap dataModel = new HashMap();
            //加载商品数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //2.加载商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pagedir+goodsId+".html"),"UTF-8");
            //生成html
            template.process(dataModel, out);
            //关闭流
            out.close();

            return true;



        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
