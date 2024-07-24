package com.smart.entity;


import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;


@Entity
@Table(name="EXPERIENCE")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer expId;
    private String orgName;
    private String orgPosition;
    private String orgImg;
    private String orgPositionAbout;
    
    @ManyToOne
    private User user;

    public Integer getExpId() {
        return expId;
    }

   

    public void setExpId(Integer expId) {
        this.expId = expId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgPosition() {
        return orgPosition;
    }

    public void setOrgPosition(String orgPosition) {
        this.orgPosition = orgPosition;
    }

    public String getOrgImg() {
        return orgImg;
    }

    public void setOrgImg(String orgImg) {
        this.orgImg = orgImg;
    }

    public String getOrgPositionAbout() {
        return orgPositionAbout;
    }

    public void setOrgPositionAbout(String orgPositionAbout) {
        this.orgPositionAbout = orgPositionAbout;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Experience [expId=" + expId + ", orgName=" + orgName + ", orgPosition=" + orgPosition + ", orgImg="
                + orgImg + ", orgPositionAbout=" + orgPositionAbout + ", user=" + user + "]";
    }

}
