package com.example.es_demo.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.example.es_demo.model.Person;
import com.example.es_demo.repository.PersonRepository;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    RestHighLevelClient restHighLevelClient;

    @Resource
    PersonRepository personRepository;

    private final String indexName = "es_person";

    /**
     * 搜索
     */
    @RequestMapping("searchPerson")
    public String searchPerson(String name){
        List<Person> byNameLike = personRepository.findByNameLikeOrderByAgeDesc(name);
        List<Person> byNameLikeAndAgeLessThanOrderByAgeDesc = personRepository
                .findByNameLikeAndAgeLessThanOrderByAgeDesc(name, 5);
        int i = personRepository.countByAgeIsLessThan(5);
        //personRepository.deleteByName(name+1);
        Pageable pageable0 = PageRequest.of(0,5);
        Pageable pageable1 = PageRequest.of(1,5);
        List<Person> byNameLikePage0 = personRepository.findByNameLike(name, pageable0);
        List<Person> byNameLikePage1 = personRepository.findByNameLike(name, pageable1);
        List<Person> top3ByNameLikeOrderByAge = personRepository.findTop3ByNameLikeOrderByAge(name);
        return byNameLike.size() + "";
    }

    /**
     * 保存doc
     */
    @RequestMapping("savePerson")
    public String savePerson(String name){
        List<Person> sources = new ArrayList();
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            String tempName = name + i;
            String id = tempName.hashCode() + "";
            person.setName(tempName);
            person.setId(id);
            person.setAge(i);

            personRepository.deleteById(id);

            sources.add(person);
        }
        Iterable<Person> people = personRepository.saveAll(sources);
        return "ok";
    }
}
