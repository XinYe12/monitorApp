package com.example.monitorapp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DBConnection {
    public static String dataList = null;
    private static final String TAG = "mysql", TAGNot = "notification";

    private static Connection conn = null;



    private String result = null;
    public static Connection getConn(){
        int port = 15100;
        String ip = "106.14.33.159", dbname="fjdata", url="jdbc:mysql://"+ip+ ":" + port +"/"+dbname + "?useUnicode=true&characterEncoding=UTF-8" , user="kehu", password="112233";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "loaded succesfully");
            conn = DriverManager.getConnection(url, user, password);

        }catch(ClassNotFoundException e){
            Log.e(TAG,"load failed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
    public static void release(Connection conn, Statement st, ResultSet rs) throws Exception{
        if (rs != null){
            rs.close();
        }
        if (st != null){
            st.close();
        }
        if (conn != null){
            conn.close();
        }
    }
    //overloading the release function
    public static void release(Connection conn, PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<HashMap<String, Object>> getInfo(String name) throws SQLException{
        List<HashMap<String,Object>> list= new ArrayList<>();
        getConn();
        Statement stmt = conn.createStatement();
        String query = "SELECT id, deviceid, PLC_ip FROM state_info";
        ResultSet resultSet = stmt.executeQuery(query);
        if (resultSet != null){
            while(resultSet.next()){
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", resultSet.getString("id"));
                map.put("device id", resultSet.getString("deviceid"));
                map.put("PLC address", resultSet.getString("PLC_ip"));
                list.add(map);
            }
        }else{
            return null;
        }
        return list;
    }
    public static void insert(String id, String deviceid, String PLC_address) throws SQLException{
        getConn();
        PreparedStatement preStmt = null;

        try{
            String query = "INSERT INTO state_info (id, deviceid, PLC_ip) VALUES (?, ?, ?)";
            preStmt = conn.prepareStatement(query);
            preStmt.setString(1, id);
            preStmt.setString(2, deviceid);
            preStmt.setString(3, PLC_address);
            preStmt.executeUpdate();
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }finally{
            if(preStmt != null){
                preStmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }


    }
    public static void delete(String name) throws SQLException{
        getConn();
        Statement stmt = conn.createStatement();
        String query = "DELETE FROM state_info WHERE id = " + name ;
        Log.d(TAG, query);
        stmt.execute(query);
        Log.d(TAG, "deletion succeeds");
    }
    public static void update(String name) throws SQLException{
        getConn();
        Statement stmt = conn.createStatement();
        String query = "UPDATE company_device_items set company_name = \"G\" where device_items = ; " + name;
        stmt.execute(query);
    }


    public static String getCompanyName(String username){
        getConn();
        String companyName = null;

            String query = "SELECT company FROM admin_user_info WHERE idname = ?";
        try(PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    companyName = resultSet.getString("company");
                }
            }
        }catch(SQLException e){
            Log.e(TAG, "get company name failed: " + e.getMessage());
        }
        return companyName;
    }

    public static List<String> getThresholdData(String companyName, String username) {
        List<String> thresholdData = new ArrayList<>();
        getConn();
        try {
             // Your existing connection retrieval logic
            String query = "SELECT id FROM duplicate_threshold WHERE company = ? AND username = ?";

            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, companyName);
                statement.setString(2, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String thresholdValue = resultSet.getString("id");
                        thresholdData.add(thresholdValue);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        return thresholdData;
    }

    //these methods are for thresholds updating purpose
    public static ResultSet getDataById(int id) throws SQLException {
        Connection conn = getConn();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM duplicate_threshold WHERE id = ?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }
    public static ResultSet getDefaultDataByID(int id) throws SQLException {
        Connection conn = getConn();
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM threshold WHERE id = ?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }
    public static void updateAttributeValueById(int id, Map<String, String> attributeUpdates) {
        PreparedStatement preparedStatement = null;
        Log.d(TAG, "defaulting id: " + id);
        try {
            getConn();
            StringBuilder updateQuery = new StringBuilder("UPDATE duplicate_threshold SET ");

            for (Map.Entry<String, String> entry : attributeUpdates.entrySet()) {
                String attributeName = entry.getKey();
                updateQuery.append(attributeName).append(" = ?, ");
            }

            // Remove the trailing comma and space
            if (!attributeUpdates.isEmpty()) {
                updateQuery.delete(updateQuery.length() - 2, updateQuery.length());
            }

            // Add the WHERE clause
            updateQuery.append(" WHERE id = ?");

            preparedStatement = conn.prepareStatement(updateQuery.toString());

            int parameterIndex = 1;
            for (Map.Entry<String, String> entry : attributeUpdates.entrySet()) {
                String attributeValue = entry.getValue();
                preparedStatement.setString(parameterIndex, attributeValue);
                parameterIndex++;
            }

            preparedStatement.setInt(parameterIndex, id);
            System.out.println("Generated Query: " + preparedStatement.toString());
            preparedStatement.executeUpdate();
            Log.d(TAG, "thresholds setting update successful");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(conn, preparedStatement);
        }
    }

    //these methods are for fragmentnotification (data monitor) 数据监控
    public static List<HashMap<String, Object>> getStateInfo(String companyName) throws SQLException {
        List<HashMap<String, Object>> list = new ArrayList<>();
        getConn();
        Statement stmt = conn.createStatement();
        String query = "SELECT id, runorstop, time_device FROM state_info WHERE company=\"无锡精恩风机有限公司\"";
        ResultSet resultSet = stmt.executeQuery(query);

        if (resultSet != null) {
            while (resultSet.next()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", resultSet.getString("id"));
                map.put("runorstop", resultSet.getString("runorstop"));
                map.put("time_device", resultSet.getString("time_device"));

                list.add(map);
            }
        } else {
            return null;
        }
        return list;
    }


    /**
     *  These methods are for drawing plots for scatterplot.java
     *  retrieves the combined key of class and deviceid
     */
    public static String getCombinedValueForId(String id) throws Exception {
        String combinedValue = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Get a database connection using your existing getConn() method
            connection = getConn();

            // Define the SQL query
            String sqlQuery = "SELECT CONCAT(class, deviceid) AS combined_value " +
                    "FROM state_info " +
                    "WHERE id = ?";

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sqlQuery);

            // Set the ID parameter in the query
            preparedStatement.setString(1, id);

            // Execute the query and retrieve the result set
            resultSet = preparedStatement.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                combinedValue = resultSet.getString("combined_value");
                Log.d(TAG, "combined value class+id: " + combinedValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Release resources using release() method
            release(connection, preparedStatement, resultSet);
        }
        return combinedValue;

    }
    /**
     *
     *  use combined value key to retrieve data from according table
     */
    public static Map<String, List<String>> retrieveDataFromCombinedID(String combinedValue) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<String, List<String>> columnData = new HashMap<>();

        try {
            // Get a database connection using your existing getConn() method
            connection = getConn();

            // Define the SQL query to select data from the table
            String sqlQuery = "SELECT * FROM " + combinedValue;

            // Create a prepared statement
            preparedStatement = connection.prepareStatement(sqlQuery);

            //make the fetch size bigger
            preparedStatement.setFetchSize(1000);

            // Execute the query and retrieve the result set
            resultSet = preparedStatement.executeQuery();

            // Get the column names
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                columnData.put(columnName, new ArrayList<>());
            }

            // Populate the lists with data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    columnData.get(columnName).add(columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Release resources using your release() method
            release(connection, preparedStatement, resultSet);
        }

        return columnData;
    }

    public static List<String> selectTemperatureAndVibrationById(int id) {
        List<String> result = new ArrayList<>();

        // SQL query to select temperature and vibration from the duplicate_threshold table
        String sql = "SELECT tempreture, vibration, rpm FROM duplicate_threshold WHERE id = ?";

        try (Connection conn = getConn();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            // Set the ID parameter in the SQL query
            preparedStatement.setInt(1, id);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a record was found
            if (resultSet.next()) {
                // Retrieve temperature and vibration values from the result set
                String temperature = resultSet.getString("temperature");
                String vibration = resultSet.getString("vibration");

                // Add temperature and vibration values to the result list
                result.add(temperature);
                result.add(vibration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<String, Integer> getIdsByCompany(String company) {
        Map<String, Integer> idMap = new HashMap<>();
        Log.d(TAG, "companyName: " + company);
        // SQL query to select IDs with a specific "company" value along with "class" and "deviceid"
        String sql = "SELECT id, class, deviceid FROM state_info WHERE company = ?";

        try (Connection conn = getConn(); // Replace with your method to get a database connection
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            // Set the company parameter in the SQL query
            preparedStatement.setString(1, company);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Iterate through the result set and add entries to the map
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String classValue = resultSet.getString("class");
                String deviceId = resultSet.getString("deviceid");

                // Combine "class" and "deviceid" into a single key
                String combinedKey = classValue + "-" + deviceId;

                // Put the combined key and ID into the map
                idMap.put(combinedKey, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idMap;
    }
    public static LiveData<List<Map<String, String>>> retrieveAlertsLiveData(String company) {
        MutableLiveData<List<Map<String, String>>> alertsLiveData = new MutableLiveData<>();

        new Thread(() -> {
            List<Map<String, String>> alertsList = new ArrayList<>();

            String sql = "SELECT * FROM update_alertlist WHERE company = ?";

            try (Connection conn = getConn();
                 PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

                preparedStatement.setString(1, company);
                ResultSet resultSet = preparedStatement.executeQuery();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, String> alertMap = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = resultSet.getString(i);
                        alertMap.put(columnName, columnValue);
                        //Log.d(TAG, "column: " +columnName+columnValue);
                    }
                    alertsList.add(alertMap);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Update LiveData with the retrieved data
            alertsLiveData.postValue(alertsList);
        }).start();

        return alertsLiveData;
    }

    public static void deleteItemsFromDatabase(List<String> errorCodesToDelete) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = getConn();

            // Create the SQL query to delete items based on error codes
            String deleteQuery = "DELETE FROM update_alertlist WHERE id = ?";
            Log.d(TAG, "deleting id: " + errorCodesToDelete);
            // Prepare the statement
            preparedStatement = conn.prepareStatement(deleteQuery);

            // Loop through the error codes and execute the delete statement for each code
            for (String errorCode : errorCodesToDelete) {
                preparedStatement.setString(1, errorCode);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources using your release methods
            release(conn, preparedStatement);
        }
    }


    }










