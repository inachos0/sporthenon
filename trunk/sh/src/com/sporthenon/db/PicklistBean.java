package com.sporthenon.db;

public class PicklistBean {

	private int value;
	
	private String text;
	
	private Object param;
	
	public PicklistBean() {
	}

	public PicklistBean(int value, String text, Object param) {
		this.value = value;
		this.text = text;
		this.param = param;
	}
	
	public PicklistBean(int value, String text){
		this.value = value;
		this.text = text;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof PicklistBean ? (this.value == ((PicklistBean)o).getValue()) : false);
	}

	@Override
	public String toString() {
		return this.text;
		//return "[" + this.value + "] " + this.text + (this.param != null ? " (" + this.param + ")" : "");
	}
	
}
