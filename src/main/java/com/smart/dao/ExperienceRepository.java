package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Experience;

public interface ExperienceRepository extends JpaRepository<Experience,Integer> {

    @Query("from Experience as c where c.user.id =:userId")
    // public List<Contact> findContactByUser(@Param("userId")Integer userId) ; 
    // current page and and contact per page
    public Page<Experience> findExperienceByUser(@Param("userId")Integer userId,Pageable pageable ) ; 

    // userid from function findContactByUser is to give to query
}
