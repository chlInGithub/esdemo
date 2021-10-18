package com.example.es_demo.repository;

import java.util.List;

import com.example.es_demo.model.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * domain object 域对象仓库，相当于mybatis的mapper，作为数据连接层。
 * <br/>
 * doc存储，搜索
 */
public interface PersonRepository extends CrudRepository<Person, String> {

    /**
     * name模糊查询，age倒序
     * @param name
     * @return
     */
    List<Person> findByNameLikeOrderByAgeDesc(String name);

    /**
     * name模糊查询 且 age上限，age倒序
     * @return
     */
    List<Person> findByNameLikeAndAgeLessThanOrderByAgeDesc(String name, int age);

    /**
     * count条件
     * @return
     */
    int countByAgeIsLessThan(int age);

    /**
     * delete条件
     */
    void deleteByName(String name);

    /**
     * 分页查询
     * @param name
     * @param pageable
     * @return
     */
    List<Person> findByNameLike(String name, Pageable pageable);

    /**
     * 取top N 条记录
     * @param name
     * @return
     */
    List<Person> findTop3ByNameLikeOrderByAge(String name);
    // ...
}
