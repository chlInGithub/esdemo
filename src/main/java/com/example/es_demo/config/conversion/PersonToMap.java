package com.example.es_demo.config.conversion;

import java.util.HashMap;
import java.util.Map;

import com.example.es_demo.model.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class PersonToMap implements Converter<Person, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Person source) {
        Map map = new HashMap();
        map.put("id", source.getId());
        map.put("name", source.getName());
        map.put("age", source.getAge());
        return map;
    }
}
