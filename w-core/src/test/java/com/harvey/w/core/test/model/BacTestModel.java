package com.harvey.w.core.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.harvey.w.core.model.BaseModelClass;

/**
 * Model class for bac_info
 * 
 */
@Entity
@Table(name = "bac_test")
@DynamicInsert
@DynamicUpdate
public class BacTestModel extends BaseModelClass {

    private static final long serialVersionUID = 1L;

    /**
     * BAC_ID
     * 
     */
    private java.lang.Integer bacId;

    @Column(name = "BAC_ID")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public java.lang.Integer getBacId() {
        return this.bacId;
    }

    public void setBacId(java.lang.Integer bacId) {
        this.bacId = bacId;
        super.addValidField("bacId");
    }

    /**
     * name
     * 
     */
    private java.lang.String bacName;

    @Column(name = "bac_name")
    public java.lang.String getBacName() {
        return this.bacName;
    }

    public void setBacName(java.lang.String bacName) {
        this.bacName = bacName;
        super.addValidField("bacName");
    }

    private java.lang.String bacDesc;

    @Column(name = "bac_desc")
    public java.lang.String getBacDesc() {
        return bacDesc;
    }

    public void setBacDesc(java.lang.String bacDesc) {
        this.bacDesc = bacDesc;
        super.addValidField("bacDesc");
    }

    /**
     * sys_version
     * 
     */
    private Integer sysVersion;

    @Column(name = "sys_version")
    @Version
    public Integer getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(Integer sysVersion) {
        this.sysVersion = sysVersion;
        super.addValidField("sysVersion");
    }

    private Integer bacValue;

    @Column(name = "bac_value")
    public Integer getBacValue() {
        return bacValue;
    }

    public void setBacValue(Integer bacValue) {
        this.bacValue = bacValue;
        super.addValidField("bacValue");
    }

}