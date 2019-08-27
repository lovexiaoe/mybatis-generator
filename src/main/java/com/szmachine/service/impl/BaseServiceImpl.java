package com.szmachine.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageRowBounds;
import com.szmachine.dao.BaseMapper;
import com.szmachine.service.BaseService;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

public abstract class BaseServiceImpl<T,M extends BaseMapper, Id> implements BaseService<T, Id> {

    protected abstract M getMapper();

    @Override
    @Transactional
    public int save(T t) {
        return getMapper().insertSelective(t);
    }

    @Override
    @Transactional
    public int saveAll(List<T> list) {
        return getMapper().insertList(list);
    }

    @Override
    @Transactional
    public int update(T t) {
        return getMapper().updateByPrimaryKey(t);
    }

    @Override
    public T getById(Id id) {
        return (T) getMapper().selectByPrimaryKey(id);
    }

    @Override
    public List<T> getAll() {
        return getMapper().selectAll();
    }

    @Override
    public List<T> searchByExample(Example example) {
        return Collections.emptyList();
    }

    @Override
    public PageInfo<T> pagenate(PageRowBounds rowBounds, T t) {
        List<T> all = getMapper().selectByRowBounds(t, rowBounds);
        PageInfo<T> pageInfo = new PageInfo<>(all);
        return pageInfo;
    }

    @Override
    public PageInfo<T> pagenate(PageRowBounds rowBounds, Example example) {
        List<T> all = getMapper().selectByExampleAndRowBounds(example, rowBounds);
        PageInfo<T> pageInfo = new PageInfo<>(all);
        return pageInfo;

    }

}
