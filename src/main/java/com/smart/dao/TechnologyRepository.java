
package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.smart.entity.Technology;

public interface TechnologyRepository extends JpaRepository<Technology,Integer> {

    @Query("from Technology as c where c.user.id =:userId")
    // public List<Contact> findContactByUser(@Param("userId")Integer userId) ; 
    // current page and and contact per page
    public Page<Technology> findTechnologyByUser(@Param("userId")Integer userId,Pageable pageable ) ; 

    // userid from function findContactByUser is to give to query
}
