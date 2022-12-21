package com.kevintian;
 
import java.text.DecimalFormat; 
import java.util.HashMap; 
import java.util.Map;
import java.util.Scanner;

public class LaunchApplication {
	
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	public static void main(String[] args) throws InterruptedException { 
		
		MenuItem[] menuItemList = {
			new MenuItem(1, "Bottled Water", 10),
			new MenuItem(2, "Pepsi", 25),
			new MenuItem(3, "Pizza", 50),
			new MenuItem(4, "Fries", 35),
			new MenuItem(5, "Burger", 60),
			new MenuItem(10, "Steak", 90),
			new MenuItem(15, "Fish & Chips", 25),
			new MenuItem(25, "Soup of the day", 5),
			new MenuItem(30, "Chicken chop", 20),
			new MenuItem(45, "Chicken Rice", 10)
		};
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to Hotel WOW");
		String custName = null;
		
		while(true) {
			System.out.println("Dear customer, please enter your name");
			custName = scanner.nextLine();
			custName = custName.trim();
			
			if(custName.length() > 0 && custName.length() <= 40) { 
				break;
			}
		} 
		
		StateManagerThread smt = new StateManagerThread(custName);
		Map<Integer, ReceiptItem> receiptItemMap = smt.firstLoadReceiptItemMap();
		Map<Integer, MenuItem> menuItemsMap = new HashMap<Integer, MenuItem>();
		
		smt.start();
		
		String menuPrintout = printMenu(menuItemList, menuItemsMap);
		 
		while(true) {
			System.out.println("Dear " + custName + ", below is the menu"); 
			System.out.println(menuPrintout);
			
			String itemIdSelStr;
			int itemIdSel = 0;  
			MenuItem tmpMenuItem = null;  
			int flowPath = 1;
			
			while(true) {
				System.out.println("Enter the Item ID (or enter 'e' to edit, 's' to save and exit, 'b' to bill)"); 
				itemIdSelStr = scanner.nextLine(); 
				
				if(itemIdSelStr.equals("e")) {
					flowPath = 2;
					break;
				} 
				else if(itemIdSelStr.equals("b")) {
					flowPath = 3;
					break;
				} 
				else if(itemIdSelStr.equals("s")) {
					flowPath = 4;
					break;
				}
				
				try {  
					itemIdSel = Integer.parseInt(itemIdSelStr);
					tmpMenuItem = menuItemsMap.get(itemIdSel); 
					
					if(tmpMenuItem == null) {
						System.out.println("Please enter a valid Item ID"); 
						continue;
					}
					
					break;
				}
				catch(Exception e) {
					System.out.println("Please enter a valid Item ID"); 
				} 
			} 
			 
			if(flowPath == 1) {
				processAddToReceipt(custName, tmpMenuItem, receiptItemMap, scanner, smt); 
				tmpMenuItem = null;
			}
			else if(flowPath == 2) { 
				processEditReceipt(custName, receiptItemMap, scanner, smt);
			}	
			else if(flowPath == 3) {
				break;
			} 
			else if(flowPath == 4) {
				smt.end();
				smt.join(); 
				
				receiptItemMap.clear();
				break;
			}
		}
		
		if(receiptItemMap.size() > 0) {
			printFinalReceipt(scanner, custName, receiptItemMap, smt);
		}
		
		scanner.close(); 
		smt.close();
	}
	
	public static void processEditReceipt(String custName, Map<Integer, ReceiptItem> receiptItemMap, Scanner scanner, StateManagerThread smt) {
		if(receiptItemMap.size() == 0) {
			System.out.println("Nothing in current bill to edit");
			return;
		}
		
		System.out.println(generateCurrentReceiptStr(receiptItemMap));  
		
		ReceiptItem receiptItem = null;
		int itemIdSel;
		int quantitySel;
		
		while(true) {
			try {
				System.out.println("Enter the Item ID to edit");
				
				itemIdSel = Integer.parseInt(scanner.nextLine()); 
				receiptItem = receiptItemMap.get(itemIdSel);
				
				if(receiptItem == null) {
					System.out.println("Please enter a valid Item ID");
				}
				else {
					break;
				}
			}
			catch(Exception e) {
				System.out.println("Please enter a valid Item ID");
			}
		}
		
		while(true) {
			System.out.println("Enter the quantity");
			
			try {
				quantitySel = Integer.parseInt(scanner.nextLine());
				
				if(quantitySel < 0) {
					System.out.println("Please enter a valid quantity");
					continue;
				}
				
				break;
			}
			catch(Exception e) {
				System.out.println("Please enter a valid quantity");
			} 
		} 
		
		if(quantitySel == 0) {
			receiptItemMap.remove(itemIdSel);
		}
		else {
			receiptItem.setQuantity(quantitySel);
		}
		
		smt.setProcessUpdate(true);
		
		System.out.println("Successfully updated quantity");  
	}
	
	public static void processAddToReceipt(String custName, MenuItem tmpMenuItem, Map<Integer, ReceiptItem> receiptItemMap, Scanner scanner, StateManagerThread smt) {
		
		int quantitySel; 
		
		while(true) {
			System.out.println("Enter the quantity");
			
			try {
				quantitySel = Integer.parseInt(scanner.nextLine());
				
				if(quantitySel <= 0) {
					System.out.println("Please enter a valid quantity");
					continue;
				}
				
				break;
			}
			catch(Exception e) {
				System.out.println("Please enter a valid quantity");
			} 
		} 
		
		ReceiptItem rItem = receiptItemMap.get(tmpMenuItem.getId());
		
		if(rItem == null) {
			rItem = new ReceiptItem(tmpMenuItem, quantitySel);
			receiptItemMap.put(tmpMenuItem.getId(), rItem);
		}
		else {
			rItem.setQuantity(rItem.getQuantity()+quantitySel);
		}
		
		smt.setProcessUpdate(true);
		
		System.out.println("itemName: " + tmpMenuItem.getName() + " itemPrice: " + tmpMenuItem.getPrice() + " subTotal: " + String.format("%.2f", (tmpMenuItem.getPrice()*quantitySel)));
	}
	
	public static String printMenu(MenuItem[] menuItemList, Map<Integer, MenuItem> menuItemMap) {
		StringBuilder ret = new StringBuilder();
		
		String seperatorStr = String.format("%60s", "");
		seperatorStr = seperatorStr.replace(' ', '-');
		ret.append(seperatorStr);
		
		ret.append("\n");
		
		String tmpStr = String.format("%-20s", "itemID");
		ret.append(tmpStr);
		tmpStr = String.format("%-20s", "itemName");
		ret.append(tmpStr);
		tmpStr = String.format("%-20s", "itemPrice");
		ret.append(tmpStr);
		
		ret.append("\n");
		
		ret.append(seperatorStr);
		
		ret.append("\n");
		
		for(MenuItem menuItem : menuItemList) {
			menuItemMap.put(menuItem.getId(), menuItem);
			
			tmpStr = String.format("%-20s", menuItem.getId());
			ret.append(tmpStr);
			tmpStr = String.format("%-20s", menuItem.getName());
			ret.append(tmpStr);
			tmpStr = String.format("%-20s", menuItem.getPrice());
			ret.append(tmpStr);
			
			ret.append("\n");
		}
		
		ret.append(seperatorStr);
		
		return ret.toString();
	}
	
	public static String generateCurrentReceiptStr(Map<Integer, ReceiptItem> receiptItemMap) {
		StringBuilder retStr = new StringBuilder();
		
		String seperatorStr = String.format("%100s", "");
		seperatorStr = seperatorStr.replace(' ', '-');
		retStr.append(seperatorStr);
		
		retStr.append("\n");
		
		retStr.append(String.format("%-20s", "itemID"));
		retStr.append(String.format("%-20s", "itemName"));
		retStr.append(String.format("%-20s", "itemPrice"));
		retStr.append(String.format("%-20s", "quantity"));
		retStr.append(String.format("%-20s", "subTotal")); 
		
		retStr.append("\n");
		
		retStr.append(seperatorStr);
		
		retStr.append("\n");
		
		
		for(Map.Entry<Integer, ReceiptItem> receiptItem : receiptItemMap.entrySet()) { 
			double subTotal = receiptItem.getValue().getSubTotal();
			
			retStr.append(String.format("%-20s", receiptItem.getValue().getMenuItem().getId()));
			retStr.append(String.format("%-20s", receiptItem.getValue().getMenuItem().getName()));
			retStr.append(String.format("%-20s", receiptItem.getValue().getMenuItem().getPrice()));
			retStr.append(String.format("%-20s", receiptItem.getValue().getQuantity()));
			retStr.append(String.format("%-20s", subTotal));
			
			retStr.append("\n");
		}
		 
		retStr.append(seperatorStr);
		
		return retStr.toString();
	}
	
	public static double getTotalAmt(Map<Integer, ReceiptItem> receiptItemMap) {
		
		double total = 0;
		
		for(Map.Entry<Integer, ReceiptItem> receiptItem : receiptItemMap.entrySet()) { 
			total+=receiptItem.getValue().getSubTotal();
		}
		
		return total;
	}
	  
	public static void printFinalReceipt(Scanner scanner,String custName, Map<Integer, ReceiptItem> receiptItemMap, StateManagerThread smt) throws InterruptedException {
		  
		System.out.println(generateCurrentReceiptStr(receiptItemMap));
		double total = getTotalAmt(receiptItemMap);
		System.out.println("Dear " + custName + ", your total bill amount is: " + total);
		
		double tipAmt = 0;
		boolean toContinue = true;
		
		while(toContinue) {
			System.out.println("Please enter the tip");
			try {
				tipAmt = Double.parseDouble(scanner.nextLine());
				
				if(tipAmt >= 0) {
					toContinue = false;
				}
				else {
					System.out.println("Please enter a valid tip amount");
				}
			}
			catch(Exception e) {
				System.out.println("Please enter a valid tip amount");
			}
		}
		
		tipAmt = Double.parseDouble(df.format(tipAmt));
		
		total+=tipAmt;
		
		double gstAmt = Double.parseDouble(df.format(total*0.07));
		
		total += gstAmt;
		
		System.out.println("GST: " + gstAmt);
	
		System.out.println("The total amount payable is: " + total); 
		
		smt.end();
		smt.join();
		smt.clearState(); 
	}
}
