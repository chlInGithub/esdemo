package com.example.es_demo.repository;

import java.util.List;

import com.example.es_demo.model.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * domain object 域对象仓库，相当于mybatis的mapper，作为数据连接层。
 * <br/>
 * doc存储，搜索
 */
public interface PersonRepository extends CrudRepository<Person, String> {

    List<Person> findByNameLike(String name);

    // ...
}
