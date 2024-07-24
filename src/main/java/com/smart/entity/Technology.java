package com.smart.entity;

import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;



@Entity
@Table(name="TECHNOLOGY")
public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer techId;
    private String techName;
    private String techDesc;
    private String techImg;
    @ManyToOne
    private User user;
    public Integer getTechId() {
        return techId;
    }
    public void setTechId(Integer techId) {
        this.techId = techId;
    }
    public String getTechName() {
        return techName;
    }
    public void setTechName(String techName) {
        this.techName = techName;
    }
    public String getTechDesc() {
        return techDesc;
    }
    public void setTechDesc(String techDesc) {
        this.techDesc = techDesc;
    }
    public String getTechImg() {
        return techImg;
    }
    public void setTechImg(String techImg) {
        this.techImg = techImg;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "Technology [techId=" + techId + ", techName=" + techName + ", techDesc=" + techDesc + ", techImg="
                + techImg + ", user=" + user + "]";
    }

    


}
