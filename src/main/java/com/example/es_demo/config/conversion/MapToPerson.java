package com.example.es_demo.config.conversion;

import java.util.Map;

import com.example.es_demo.model.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class MapToPerson implements Converter<Map<String, Object>, Person> {

    @Override
    public Person convert(Map<String, Object> source) {
        Person person = new Person();
        person.setName(source.get("name").toString());
        person.setId(source.get("id").toString());
        person.setAge((Integer) source.get("age"));
        return person;
    }
}
