
package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Links;

public interface LinkRepository extends JpaRepository<Links,Integer> {

    @Query("from Links as c where c.user.id =:userId")
    public Page<Links> findLinksByUser(@Param("userId")Integer userId,Pageable pageable ) ; 

    // userid from function findContactByUser is to give to query
}
