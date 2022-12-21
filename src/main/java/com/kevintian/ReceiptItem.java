package com.kevintian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import com.google.gson.Gson; 

public class ReceiptItem {
	private MenuItem menuItem;
	private int quantity;
	
	public ReceiptItem(MenuItem menuItem, int quantity) {
		setMenuItem(menuItem);
		setQuantity(quantity);
	}
	
	public MenuItem getMenuItem() {
		return menuItem;
	}
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	} 
	
	public double getSubTotal() { 
		return Double.parseDouble(String.format("%.2f", getQuantity()*getMenuItem().getPrice()));
	}
	
	public static double getReceiptItemListTotal(List<ReceiptItem> list) {
		double total = 0;
		
		for(ReceiptItem item : list) {
			total += item.getSubTotal();
		}
		
		return total;
	}
	
	public static String receiptItemMapToJson(Map<Integer, ReceiptItem> map) {
		Gson gson = new Gson();
		ArrayList<ReceiptItem> list = new ArrayList<ReceiptItem>(map.values());
		String jsonStr = gson.toJson(list);
		return jsonStr;
	}
	 
	public static Map<Integer, ReceiptItem> jsonToReceiptItemMap(String jsonStr) {
		
		Map<Integer, ReceiptItem> receiptItemMap = new HashMap<Integer, ReceiptItem>();
		Gson gson = new Gson();
		 
		@SuppressWarnings("rawtypes")
		List rawList = gson.fromJson(jsonStr, List.class);
		
		for(Object rawItem : rawList) {
			String tmpStr = gson.toJson(rawItem);
			ReceiptItem item = gson.fromJson(tmpStr, ReceiptItem.class);
			receiptItemMap.put(item.getMenuItem().getId(), item);
		}
		
		return receiptItemMap;
	}
}
