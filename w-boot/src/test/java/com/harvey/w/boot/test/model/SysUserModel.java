package com.harvey.w.boot.test.model;

import java.io.Serializable;
import com.harvey.w.core.model.BaseModelClass;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * Model class for sys_user
 * 会员表
 */
@Entity
@Table(name = "sys_user")
@DynamicInsert
@DynamicUpdate
public class SysUserModel extends BaseModelClass implements Serializable {

    private static final long serialVersionUID = 1L;
    
    
  
    /** user_id
    *会员Id
    */ 
    private java.lang.Integer userId;
    
    @Column(name = "user_id")
    @Id @GeneratedValue(strategy = GenerationType.AUTO)   
    public java.lang.Integer getUserId(){
        return this.userId;
    }
    
    public void setUserId(java.lang.Integer userId){
        this.userId = userId;
        super.addValidField("userId");
    }
    
  
    /** username
    *会员账号
    */ 
    private java.lang.String username;
    
    @Column(name = "username")
    public java.lang.String getUsername(){
        return this.username;
    }
    
    public void setUsername(java.lang.String username){
        this.username = username;
        super.addValidField("username");
    }
    
  
    /** user_cname
    *用户名
    */ 
    private java.lang.String userCname;
    
    @Column(name = "user_cname")
    public java.lang.String getUserCname(){
        return this.userCname;
    }
    
    public void setUserCname(java.lang.String userCname){
        this.userCname = userCname;
        super.addValidField("userCname");
    }
    
  
    /** password
    *登录密码
    */ 
    private java.lang.String password;
    
    @Column(name = "password")
    public java.lang.String getPassword(){
        return this.password;
    }
    
    public void setPassword(java.lang.String password){
        this.password = password;
        super.addValidField("password");
    }
    
  
    /** phone
    *电话号码
    */ 
    private java.lang.String phone;
    
    @Column(name = "phone")
    public java.lang.String getPhone(){
        return this.phone;
    }
    
    public void setPhone(java.lang.String phone){
        this.phone = phone;
        super.addValidField("phone");
    }
    
  
    /** email
    *邮箱
    */ 
    private java.lang.String email;
    
    @Column(name = "email")
    public java.lang.String getEmail(){
        return this.email;
    }
    
    public void setEmail(java.lang.String email){
        this.email = email;
        super.addValidField("email");
    }
    
  
    /** is_admin
    *是否为管理员
    */ 
    private java.lang.String isAdmin;
    
    @Column(name = "is_admin")
    public java.lang.String getIsAdmin(){
        return this.isAdmin;
    }
    
    public void setIsAdmin(java.lang.String isAdmin){
        this.isAdmin = isAdmin;
        super.addValidField("isAdmin");
    }
    
  
    /** create_time
    *注册日期
    */ 
    private java.util.Date createTime;
    
    @Column(name = "create_time")
    public java.util.Date getCreateTime(){
        return this.createTime;
    }
    
    public void setCreateTime(java.util.Date createTime){
        this.createTime = createTime;
        super.addValidField("createTime");
    }
    
  
    /** user_status
    *会员状态
    */ 
    private java.lang.String userStatus;
    
    @Column(name = "user_status")
    public java.lang.String getUserStatus(){
        return this.userStatus;
    }
    
    public void setUserStatus(java.lang.String userStatus){
        this.userStatus = userStatus;
        super.addValidField("userStatus");
    }
    
  
    /** user_image
    *会员头像
    */ 
    private java.lang.Integer userImage;
    
    @Column(name = "user_image")
    public java.lang.Integer getUserImage(){
        return this.userImage;
    }
    
    public void setUserImage(java.lang.Integer userImage){
        this.userImage = userImage;
        super.addValidField("userImage");
    }
    
  
    /** user_type
    *会员类型
    */ 
    private java.lang.Integer userType;
    
    @Column(name = "user_type")
    public java.lang.Integer getUserType(){
        return this.userType;
    }
    
    public void setUserType(java.lang.Integer userType){
        this.userType = userType;
        super.addValidField("userType");
    }
    
  
    /** user_balance
    *会员积分
    */ 
    private java.lang.Double userBalance;
    
    @Column(name = "user_balance")
    public java.lang.Double getUserBalance(){
        return this.userBalance;
    }
    
    public void setUserBalance(java.lang.Double userBalance){
        this.userBalance = userBalance;
        super.addValidField("userBalance");
    }
    
  
    /** user_sum
    *消费金额
    */ 
    private java.lang.Double userSum;
    
    @Column(name = "user_sum")
    public java.lang.Double getUserSum(){
        return this.userSum;
    }
    
    public void setUserSum(java.lang.Double userSum){
        this.userSum = userSum;
        super.addValidField("userSum");
    }
    
  
    /** user_audited
    *审核状态
    */ 
    private java.lang.Byte userAudited;
    
    @Column(name = "user_audited")
    public java.lang.Byte getUserAudited(){
        return this.userAudited;
    }
    
    public void setUserAudited(java.lang.Byte userAudited){
        this.userAudited = userAudited;
        super.addValidField("userAudited");
    }
    
  
    /** ent_person
    *企业负责人
    */ 
    private java.lang.String entPerson;
    
    @Column(name = "ent_person")
    public java.lang.String getEntPerson(){
        return this.entPerson;
    }
    
    public void setEntPerson(java.lang.String entPerson){
        this.entPerson = entPerson;
        super.addValidField("entPerson");
    }
    
  
    /** ent_person_phone
    *企业负责人电话
    */ 
    private java.lang.String entPersonPhone;
    
    @Column(name = "ent_person_phone")
    public java.lang.String getEntPersonPhone(){
        return this.entPersonPhone;
    }
    
    public void setEntPersonPhone(java.lang.String entPersonPhone){
        this.entPersonPhone = entPersonPhone;
        super.addValidField("entPersonPhone");
    }
    
  
    /** ent_name
    *企业名称
    */ 
    private java.lang.String entName;
    
    @Column(name = "ent_name")
    public java.lang.String getEntName(){
        return this.entName;
    }
    
    public void setEntName(java.lang.String entName){
        this.entName = entName;
        super.addValidField("entName");
    }
    
  
    /** ent_address
    *企业地址
    */ 
    private java.lang.String entAddress;
    
    @Column(name = "ent_address")
    public java.lang.String getEntAddress(){
        return this.entAddress;
    }
    
    public void setEntAddress(java.lang.String entAddress){
        this.entAddress = entAddress;
        super.addValidField("entAddress");
    }
    
  
    /** cur_city
    *当前城市
    */ 
    private java.lang.Integer curCity;
    
    @Column(name = "cur_city")
    public java.lang.Integer getCurCity(){
        return this.curCity;
    }
    
    public void setCurCity(java.lang.Integer curCity){
        this.curCity = curCity;
        super.addValidField("curCity");
    }
    
  
    /** last_access
    *最后访问时间
    */ 
    private java.util.Date lastAccess;
    
    @Column(name = "last_access")
    public java.util.Date getLastAccess(){
        return this.lastAccess;
    }
    
    public void setLastAccess(java.util.Date lastAccess){
        this.lastAccess = lastAccess;
        super.addValidField("lastAccess");
    }
    
  
    /** auth_token_salt
    *访问令牌
    */ 
    private java.lang.String authTokenSalt;
    
    @Column(name = "auth_token_salt")
    public java.lang.String getAuthTokenSalt(){
        return this.authTokenSalt;
    }
    
    public void setAuthTokenSalt(java.lang.String authTokenSalt){
        this.authTokenSalt = authTokenSalt;
        super.addValidField("authTokenSalt");
    }
    
}