package com.example.casemodule6.repository;

import com.example.casemodule6.model.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IHouseRepository extends JpaRepository<House, Long> {


    @Query(value = "select * from houses where status_house_id =1 order by count_rent DESC limit 5", nativeQuery = true)
    Iterable<House> find5HouseTopRent();

    @Query(value = "select * from houses where status_house_id =1 order by RAND() limit 9", nativeQuery = true)
    Iterable<House> random9House();

    @Query(value = "call search_house(?1,?2,?3,?4,?5)", nativeQuery = true)
    Iterable<House> search9House(String city, String bedroom, String bathroom, String price, String type);

    @Query(value = "select * from houses where user_id = ?1", nativeQuery = true)
    Iterable<House> findAllByUserId(Long id);




}
