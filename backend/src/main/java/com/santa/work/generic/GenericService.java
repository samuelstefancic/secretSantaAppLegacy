package com.santa.work.generic;

import java.util.List;

public interface GenericService <T, ID>{
    T save(T entity);
    T findById(ID id);
    T update(T entity);
    void delete(ID id);
    List<T> findAll();
}
