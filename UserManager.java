package eProctor;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.*;

import javax.swing.*;

import java.awt.*;

public class UserManager {

    public static User authenticate(String userName, String pass) {
        //check if authenticate
        String url = "jdbc:mysql://" + Protocol.serverAddr + ":3306/";
        String dbName = "cz2006?";
        String driver = "com.mysql.jdbc.Driver";
        String username = "user=root&";
        String password = "password=pass";

        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url + dbName + username + password);

            //get user and password
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM  User WHERE userName='" + userName + "' and password='" + pass + "'");
            boolean login = false;

            int userID = 0;
            int role = 0;
            String email = "";
            String name = "";

            while (res.next()) {
                login = true;
                userID = res.getInt("userID");
                role = res.getInt("role");
                email = res.getString("email");
                name = res.getString("name");
            }

            if (login) {
                System.out.println("Login Success");

                if (role == 0) {
                    String matricNo = "";
                    String course = "";

                    //get student info
                    res = st.executeQuery("SELECT * FROM student inner join course on student.courseID = course.courseID where userID ='" + userID + "'");
                    while (res.next()) {
                        matricNo = res.getString("matricnumber");
                        course = res.getString("coursename");
                    }
                    User u = new Student(userID, pass, email, name, matricNo, course);
                    res.close();
                    st.close();
                    conn.close();
                    return u;
                } else if (role == 1) {
                    String faculty = "";
                    //get invigilator info
                    res = st.executeQuery("SELECT * FROM  invigilator WHERE userID='" + userID + "'");
                    while (res.next()) {
                        faculty = res.getString("faculty");
                    }
                    User u = new Invigilator(userID, pass, email, name, faculty);
                    u.setStudent(false);
                    res.close();
                    st.close();
                    conn.close();
                    return u;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Username/Password is incorrect. Please try again.", "Login Fail", JOptionPane.ERROR_MESSAGE);
                res.close();
                st.close();
                conn.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
