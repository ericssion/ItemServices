package com.item.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class Item{
    public String username;
    public String userType;
    public String email;
    public String firstName;
    public String lastName;
    public String hireDate;
    public List<Positions> positions;
    public List<Role> roles;
    public List<Titles> titles;
}
