package com.szmachine.service;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageRowBounds;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public interface BaseService<T, Id> {
    int save(T t);

    int update(T t);

    T getById(Id id);

    List<T> getAll();

    int saveAll(List<T> list);

    List<T> searchByExample(Example example);

    PageInfo<T> pagenate(PageRowBounds rowBounds, T t);

    PageInfo<T> pagenate(PageRowBounds rowBounds, Example example);
}
