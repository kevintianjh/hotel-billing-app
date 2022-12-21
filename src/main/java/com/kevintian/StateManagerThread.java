package com.kevintian;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StateManagerThread extends Thread {
	
	private String custName;
	private StateManager stateManager = new StateManager();  
	private boolean processUpdate = false;
	private Map<Integer, ReceiptItem> receiptItemMap = null;
	private boolean toEnd = false;
	
	public StateManagerThread(String custName) {
		setDaemon(true);
		setPriority(8);
		this.custName = custName;
	}
	
	public Map<Integer, ReceiptItem> firstLoadReceiptItemMap() {
		Map<Integer, ReceiptItem> ret = null;
		
		try {
			this.stateManager.connect();
			ret = this.stateManager.queryState(this.custName);
		}
		catch(SQLException e) {
			ret = new HashMap<Integer, ReceiptItem>();
		}
		
		this.receiptItemMap = ret;
		
		return ret;
	} 
	
	public void setProcessUpdate(boolean processUpdate) {
		this.processUpdate = processUpdate;
	}

	public void end() {
		this.toEnd = true;
	}
	
	public void clearState() {
		this.stateManager.clearState(this.custName);
	}
	
	public void close() {
		this.stateManager.close();
	}
	
	public void saveState() {
		this.stateManager.saveState(this.custName, receiptItemMap); 
	}
	
	@Override
	public void run() { 
		try {
			while(!toEnd) {
				if(this.processUpdate) {
					this.stateManager.saveState(this.custName, receiptItemMap); 
					this.processUpdate = false;
				}
				
				if(!toEnd) {
					Thread.sleep(2000);
				} 
			}
		}
		catch(InterruptedException e) {
			throw new RuntimeException(e);
		} 
	}
}
