package com.smart.entity;


import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;


@Entity
@Table(name="LINKS")
public class Links {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer linkId;
  
    private String linkName;
    private String linkUrl;
    private String linkImg;
    
    @ManyToOne
    private User user;

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkImg() {
        return linkImg;
    }

    public void setLinkImg(String linkImg) {
        this.linkImg = linkImg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Links [linkId=" + linkId + ", linkName=" + linkName + ", linkUrl=" + linkUrl + ", linkImg=" + linkImg
                + ", user=" + user + "]";
    }

    


}
