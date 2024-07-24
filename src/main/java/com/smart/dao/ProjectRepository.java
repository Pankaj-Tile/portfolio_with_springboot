
package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Project;

public interface ProjectRepository extends JpaRepository<Project,Integer> {

    @Query("from Project as c where c.user.id =:userId")
    // public List<Contact> findContactByUser(@Param("userId")Integer userId) ; 
    // current page and and contact per page
    public Page<Project> findProjectByUser(@Param("userId")Integer userId,Pageable pageable ) ; 

    // userid from function findContactByUser is to give to query
}
