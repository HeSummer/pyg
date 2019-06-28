package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 * @author Aming.he
 * @Time 2019.6.24
 * */
public interface BrandService {

    //查询全部商品品牌
    public List<TbBrand> findAll();

    /**
     * 品牌分页查询
     * @param pageNum 当前页
     * @param pageSize 每页记录数
     * */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 品牌分页查询
     * @param tbBrand  条件数据
     * @param pageNum  当前页
     * @param pageSize 每页记录数
     * */
    public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    /**
     * 品牌增加
     * @param  tbBrand 品牌对象
     * @Retrun  Result 返回对象
     * */
    public void add(TbBrand tbBrand);

    /**
     * 品牌修改
     * @param  tbBrand 品牌对象
     * @Retrun  Result 返回对象
     * */
    public void update(TbBrand tbBrand);


    /**
     * 根据 ID 获取实体
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long [] ids);


    /**
     * 为模板查询品牌名称
     * 列表数据
     * */
    public List<Map> selectOptionList();
}
