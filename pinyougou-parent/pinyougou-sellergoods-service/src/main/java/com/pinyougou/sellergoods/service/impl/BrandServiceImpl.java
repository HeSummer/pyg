package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrandExample.Criteria;
@Service
public class BrandServiceImpl  implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        //使用分页插件
        PageHelper.startPage(pageNum,pageSize);//分页
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }



    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        //分页
        PageHelper.startPage(pageNum, pageSize);
        //条件查询对象
        TbBrandExample example=new TbBrandExample();
        Criteria criteria = example.createCriteria();
        //封装查询条件
        if(brand!=null){
            if(brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if(brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }





    /**
     * 新增品牌
     * @param tbBrand
     *
     * */
    @Override
    public void add(TbBrand tbBrand){
          brandMapper.insert(tbBrand);
    }

    /**
     * 修改品牌
     * @param tbBrand
     * */
    @Override
    public void update(TbBrand tbBrand){
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 根据 ID 获取实体
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id){
        return brandMapper.selectByPrimaryKey(id);
    }


    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(Long [] ids){
            for(long l:ids){
                brandMapper.deleteByPrimaryKey(l);
            }
    }
    /**
     * 为模板查询品牌名称
     * 列表数据
     * */
    @Override
    public List<Map> selectOptionList() {

        return brandMapper.selectOptionList();

    }


}
