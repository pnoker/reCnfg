package com.rcw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlserver {
	private Connection connection = null;
	public Statement statement = null;
	private ResultSet result = null;

	public Sqlserver() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;databaseName=ShiHua;user=sa;password=yangfan";
			connection = DriverManager.getConnection(url);
			statement = connection.createStatement();
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} catch (ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		try {
			result = statement.executeQuery(sql);
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
		}
		return result;
	}

	public int executeUpdate(String sql) throws SQLException {
		int updatenum = 0;
		try {
			updatenum = statement.executeUpdate(sql);
			return updatenum;
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
		}
		return updatenum;
	}

	public void free() throws SQLException {
		try {
			if (result != null)
				result.close();
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			System.out.println("ERROR:" + se.getMessage());
		}
	}

}
