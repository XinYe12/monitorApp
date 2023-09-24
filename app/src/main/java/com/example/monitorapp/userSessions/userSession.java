package com.example.monitorapp.userSessions;

import android.util.Log;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.entity.User;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class userSession {
    private static final String TAG = "mysql-user";
    private static int userStatus = -1;
    private DBConnection dbConnection = new DBConnection();

    public boolean select(String username) throws Exception{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            conn = dbConnection.getConn();
            stmt = conn.createStatement();
            String query = "SELECT * FROM admin_user_info WHERE idname = " + username;
            rs = stmt.executeQuery(query);
            if (rs.next()){
                return true;
            }else{
                return false;
            }

        }catch(Exception  e){
            Log.e(TAG, e.getMessage());
            return false;

        }finally {
            dbConnection.release(conn, stmt, rs);
        }

    }

    public int determineLoginStatus(String username, String password){
        Connection con = null;
        HashMap<String, Object> map = new HashMap<>();
        DBConnection dbConnection = new DBConnection();
        con = dbConnection.getConn();


        try{
            String query = "SELECT * FROM admin_user_info WHERE idname = ?";
            if (con != null) {
                PreparedStatement preStmt = con.prepareStatement(query);
                if(preStmt != null){
                    Log.d(TAG, "username" + username);
                    preStmt.setString(1, username);
                    ResultSet result = preStmt.executeQuery();
                    int count = result.getMetaData().getColumnCount();
                    while (result.next()){
                        for (int i=1; i<=count; i++){
                            String field = result.getMetaData().getColumnName(i);
                            map.put(field, result.getString(field));
                        }
                    }

                    if (map.size() != 0){
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String key: map.keySet()){
                            if(key.equals("password")){
                                if(password.equals(map.get(key))){
                                    userStatus = 1;             //login succeeded
                                }else{
                                    userStatus = 2;             //login failed with wrong password
                                }

                            }
                        }
                    }else{
                        userStatus = 3;         //username doesn't exist
                    }
                }
                Log.d(TAG, "GOOD CONNECTION HERE");
                con.close();
                preStmt.close();

            }else{

                //connection cannot be made, assign -1 to user status, login failed
                userStatus = -1;
            }
        }catch(Exception e){
            Log.e(TAG, "Login failed with" + e.getMessage());
            userStatus = -1;
        }

        return userStatus;
    }
    public boolean register(User user){
        boolean valid = false;

        HashMap<String, Object> map = new HashMap<>();
        Connection conn = dbConnection.getConn();
        try{
            String query = "INSERT INTO admin_user_info(idname, password,identity, company, address, telephone, email ) VALUES (?, ?,?,?,?,?,? )";
            if (conn != null){
                PreparedStatement preStmt = conn.prepareStatement(query);
                if(preStmt != null){
                    preStmt.setString(1, user.getUsername());
                    preStmt.setString(2, user.getPassword());
                    preStmt.setString(3, user.getUserType());
                    preStmt.setString(4, user.getCompany());
                    preStmt.setString(5, user.getAddress());
                    preStmt.setString(6, user.getTelephone());
                    preStmt.setString(7, user.getEmail());
                    int result = preStmt.executeUpdate();
                    if(result > 0){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "Register failed with: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return valid;
    }

}
