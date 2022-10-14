package com.atguigu.gmall.search.respository;


import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PersonRepository extends ElasticsearchRepository<Person, Long> {

    /**
     * 根据名字和年龄大于查询
     *
     * @param userName
     * @param age
     * @return
     */
    List<Person> getAllByUserNameLikeAndAgeGreaterThanEqual(String userName, Integer age);

    /**
     * 根据生日查询
     *
     * @return
     */
    List<Person> findAllByBirthDayBetween(Date birthDay1, Date birthDay2);

    /**
     * 根据地址查询人数
     *
     * @param address
     * @return
     */
    Long countAllByAddressLike(String address);

}
