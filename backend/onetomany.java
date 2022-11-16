package com.calsoft.ecom.model.myshoppe;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "desc_items")
@Getter
@Setter
public class DescItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "desc_items_id")
	public long descItemsId;

	@Column(name = "fabric")
	public String fabric;

	@Column(name = "type")
	public String type;

	@Column(name = "design_Type")
	public String designType;

	@Column(name = "fit_type")
	public String fitType;

	@Column(name = "country_of_origin")
	public String countryOfOrigin;

	@Column(name = "product_id", insertable = false, updatable = false)
	private Long productId;

	@Column(name = "length")
	public String length;

	@Column(name = "width")
	public String width;
	
	@Column(name = "style")
	public String style;

	@JsonBackReference
	@OneToOne
	@JoinColumn(name = "product_id")
	private MyShopPrimeProduct myShopPrimeProduct;
}