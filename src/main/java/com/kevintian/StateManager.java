package com.kevintian;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.HashMap; 
import java.util.Map;

public class StateManager {
	
	private Connection connection;
	
	public void connect() throws SQLException {
		if(this.connection == null || this.connection.isClosed()) { 
			this.connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/hotel_app","root","password");
		}  
	}
	
	public void close() {
		try {
			if(this.connection != null) {
				this.connection.close();
			}
		}
		catch(SQLException e) {}
	}
	
	public Map<Integer, ReceiptItem> queryState(String name) {
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<Integer, ReceiptItem> retMap = null;
		
		if(this.connection == null) {
			return new HashMap<Integer, ReceiptItem>();
		}
		
		try {  
			stmt = this.connection.prepareStatement("SELECT state FROM saved_state WHERE name = ?");
			stmt.setString(1, name);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				retMap = ReceiptItem.jsonToReceiptItemMap(rs.getString("state"));
			}
			else {
				retMap = new HashMap<Integer, ReceiptItem>();
			} 
		}
		catch(SQLException e) { 
			retMap = new HashMap<Integer, ReceiptItem>(); 
		}
		catch (Exception e) { 
			retMap = new HashMap<Integer, ReceiptItem>(); 
		}
		finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				
				if(rs != null) {
					rs.close();
				}
			}
			catch(SQLException e) {}
		} 
		  
		return retMap;
	}
	
	public void saveState(String name, Map<Integer, ReceiptItem> receiptItemMap) { 
		String sql = "INSERT INTO saved_state(state, name) VALUES(?, ?)";
		PreparedStatement stmt = null;
		
		if(this.connection == null) {
			return;
		}
		
		try {
			if(receiptItemMap.size() == 0) {
				clearState(name);
				return;
			}
			
			if(doesStateExist(name)) {
				sql = "UPDATE saved_state SET state = ? WHERE name = ?";
			}
			
			String jsonStr = ReceiptItem.receiptItemMapToJson(receiptItemMap);
			stmt = this.connection.prepareStatement(sql);
			stmt.setString(1, jsonStr);
			stmt.setString(2, name);
			
			stmt.executeUpdate();
			stmt.close();
		}
		catch(SQLException e) { 
		} 
		finally {
			try {
				if(stmt != null) {
					stmt.close();
				} 
			}
			catch(SQLException e) {
				System.out.println("Cannot close resources");
			}
		}
	}
	
	public boolean doesStateExist(String name) throws SQLException { 
		boolean ret = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		if(this.connection == null) {
			throw new SQLException("Connection unavailable!");
		}
		
		try {
			stmt = this.connection.prepareStatement("SELECT COUNT(*) FROM saved_state WHERE name = ?");
			stmt.setString(1, name);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				int count = rs.getInt(1);
				if(count >= 1) {
					ret = true;
				}
			} 
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
				
				if(rs != null) {
					rs.close();
				}
			}
			catch(SQLException e) {
				System.out.println("Cannot close resources");
			}
		} 
		
		return ret;
	}
	
	public void clearState(String custName) {
		
		PreparedStatement stmt = null;
		
		if(this.connection == null) {
			return;
		}
		
		try { 
			stmt = this.connection.prepareStatement("DELETE FROM saved_state WHERE name=?");
			stmt.setString(1, custName);
			stmt.executeUpdate(); 
		}
		catch(SQLException e) { 
		}
		finally {
			try {
				if(stmt != null) {
					stmt.close();
				} 
			}
			catch(SQLException e) { 
			}
		}
	}
}
