package com.smart.entity;


import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;



@Entity
@Table(name="PROJECT")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer projectId;
    private String projectName;
    private String projectDesc;
    private String projectUrl;
    private String projectSource;

    @ManyToOne
    private User user;


    public Integer getProjectId() {
        return projectId;
    }
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getProjectDesc() {
        return projectDesc;
    }
    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }
    public String getProjectUrl() {
        return projectUrl;
    }
    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
    public String getProjectSource() {
        return projectSource;
    }
    public void setProjectSource(String projectSource) {
        this.projectSource = projectSource;
    }
    @Override
    public String toString() {
        return "Project [projectId=" + projectId + ", projectName=" + projectName + ", projectDesc=" + projectDesc
                + ", projectUrl=" + projectUrl + ", projectSource=" + projectSource + ", user=" + user + "]";
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    


}
