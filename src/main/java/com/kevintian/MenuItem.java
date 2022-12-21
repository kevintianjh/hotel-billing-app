package com.kevintian;


public class MenuItem {
	private int Id;
	private String name;
	private double price;
	
	public MenuItem(int id, String name, double price) {
		setId(id);
		setName(name);
		setPrice(price);
	}
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public synchronized String getName() {
		return name;
	}
	public synchronized void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	} 
}

