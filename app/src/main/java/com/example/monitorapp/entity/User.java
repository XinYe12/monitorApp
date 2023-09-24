package com.example.monitorapp.entity;

public class User {
    private int id;
    private String company;
    private String username;
    private String password;
    private String address;
    private String email;
    private String telephone;
    private String userType; //admin is 1, normal user is 2
    public User(){

    }
    public User (int id, String company,String username, String password, String address, String email,String telephone, String userType){
        this.id = id;
        this.company = company;
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
        this.userType = userType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }
}
