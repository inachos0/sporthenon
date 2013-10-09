package com.sporthenon.db.entity.meta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"~TREE_ITEM\"")
public class TreeItem  {
	
	@Id
	private Integer id;
	
	@Column(name = "id_item", nullable = false)
	private Integer idItem;
	
	@Column(name = "label", length = 50, nullable = false)
	private String label;
	
	@Column(name = "level", nullable = false)
	private Integer level;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdItem() {
		return idItem;
	}

	public void setIdItem(Integer idItem) {
		this.idItem = idItem;
	}

	public String getStdLabel() {
		return this.label;
	}
	
	public String getLabel() {
		return (label != null && label.contains("'") ? label.replaceAll("'", "\\\\'") : label.replaceAll("\\s", "&nbsp;"));
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "TreeItem [id=" + id + ", idItem=" + idItem + ", label=" + label
				+ ", level=" + level + "]";
	}

}
