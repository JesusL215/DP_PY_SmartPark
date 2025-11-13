package com.smartpark.estacionamiento.model.dao;
import java.util.List;
public interface IDAO<T, K> {
    T get(K id);
    List<T> getAll();
    void save(T t);
    void update(T t);
    void delete(T t);
}