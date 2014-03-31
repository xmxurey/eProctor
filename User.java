package eProctor;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;

import javax.swing.*;

import java.awt.*;


public class User {

	private int userID;
	private String password;
	private String email;
	private String name;
	private boolean isStudent=true;
	
	public User(int u, String p, String e, String n){
		userID = u;
		password = p;
		email = e;
		name = n;
	}
	
	//get and set methods
	public void setUserID(int u){
		this.userID = u;
	}
	public int getUserID(){
		return this.userID;
	}
	public void setPassword(String p){
		this.password = p;
	}
	public String getPassword(){
		return this.password;
	}
	public void setEmail(String e){
		this.email = e;
	}
	public String getEmail(){
		return this.email;
	}
	public void setName(String n){
		this.name = n;
	}
	public String getName(){
		return this.name;
	}
	public void setStudent(boolean n){
		this.isStudent = n;
	}
	public boolean isStudent(){
		return this.isStudent;
	}

}
