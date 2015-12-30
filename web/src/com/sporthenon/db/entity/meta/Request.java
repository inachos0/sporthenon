package com.sporthenon.db.entity.meta;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "\"~Request\"")
public class Request {

	@Id
	@SequenceGenerator(name = "seq_request", sequenceName = "\"~SeqRequest\"")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_request")
	private Integer id;
	
	@Column(name = "\"type\"")
	private String type;
	
	@Column(name = "params", length = 50)
	private String params;
	
	@Column(name = "date", length = 20)
	private Timestamp date;

	public Integer getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getParams() {
		return params;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
	
}