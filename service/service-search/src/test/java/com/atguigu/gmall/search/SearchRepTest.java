package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.biz.SearchBizService;
import com.atguigu.gmall.search.respository.PersonRepository;
import com.atguigu.gmall.search.vo.SearchParamVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class SearchRepTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void test01() {
        Person person = new Person();
        person.setId(1L);
        person.setUserName("常森");
        person.setAddress("西安市");
        person.setBirthDay(new Date());
        person.setAge(19);
        personRepository.save(person);
        System.out.println("保存成功");

        person.setId(2L);
        person.setUserName("常木");
        person.setAddress("北京市");
        person.setBirthDay(new Date());
        person.setAge(19);
        personRepository.save(person);

        person.setId(3L);
        person.setUserName("常林");
        person.setAddress("上海市");
        person.setBirthDay(new Date());
        person.setAge(19);
        personRepository.save(person);

        person.setId(4L);
        person.setUserName("常木林");
        person.setAddress("武汉市");
        person.setBirthDay(new Date());
        person.setAge(19);
        personRepository.save(person);

    }

    @Test
    public void testSearch() {
        // // 按照id查询
        // Person person = personRepository.findById(3L).get();
        // System.out.println(person);
        //
        // // 按照名字和年龄查询
        // List<Person> list = personRepository.getAllByUserNameLikeAndAgeGreaterThanEqual("3", 19);
        // list.forEach(System.out::println);
        //
        // // 按照生日查询
        // List<Person> list1 = personRepository.findAllByBirthDayBetween(new Date(), new Date());
        // list1.forEach(System.out::println);

        // 按照地址查询
        Long aLong = personRepository.countAllByAddressLike("西安市");
        System.out.println("西安有: " + aLong);


    }

    @Autowired
    private SearchBizService searchBizService;

    @Test
    public void testSearchBizService() {
        SearchParamVo searchParamVo = new SearchParamVo();
        // 分类
        searchParamVo.setCategory3Id(61L);
        // 属性条件
        // searchParamVo.setProps(new String[]{"4:256GB:机身存储","2: 6.95英寸及以上:屏幕尺寸"});
        // 品牌
        // searchParamVo.setTrademark("2:华为");
        searchBizService.search(searchParamVo);
    }
}
