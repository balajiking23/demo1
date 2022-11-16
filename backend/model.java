package com.calsoft.ecom.model.myshoppe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "my_shop_prime_product")
@Getter
@Setter
public class MyShopPrimeProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	public long productId;

	@Column(name = "mspid")
	public long mspid;

	@Column(name = "msid")
	public String msid;

	@Column(name = "show_admin_rejected")
	public boolean showAdminRejected;

	@Column(name = "show_size_chart")
	public boolean showSizeChart;

	@OneToOne(mappedBy = "myShopPrimeProduct", cascade = CascadeType.ALL)
	@JsonManagedReference
	public DescItems descItems;

	@OneToMany(mappedBy = "myShopPrimeProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<BuyerPriceDetails> buyerPriceDetails = new ArrayList<BuyerPriceDetails>();

	@Column(name = "free_delivery")
	public String freeDelivery;

	@Column(name = "is_allow_edit")
	public boolean isAllowEdit;

	@Column(name = "score")
	public double score;

	@Column(name = "created_at")
	public String createdAt;

	@OneToMany(mappedBy = "myShopPrimeProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ReviewList> reviewList = new ArrayList<ReviewList>();

//	public Options options;

	@Column(name = "buyer_price")
	public String buyerPrice;

	@Column(name = "fb_page_share")
	public String fbPageShare;

	@Column(name = "primary_id")
	public String primaryId;

	@Column(name = "shop_id")
	public Double shopId;

	@Column(name = "available_qty")
	public double availableQty;

	@Column(name = "sku")
	public String sku;

	@Column(name = "product_description")
	public String productDescription;

	@Column(name = "updated_at")
	public String updatedAt;

	// public Thumbnail thumbnail;

	@Column(name = "image_url")
	public String imageUrl;

	@Column(name = "color_text")
	public String colorText;

	@Column(name = "is_admin_rejected")
	public boolean isAdminRejected;

	@Column(name = "shipping_charges")
	public Double shippingCharges;

	@Column(name = "display_buyer_price")
	public String displayBuyerPrice;

	@Column(name = "product_slug")
	public String productSlug;

	@Column(name = "seller_mapping_id")
	public double sellerMappingId;

	@Column(name = "show_contact")
	public boolean showContact;

	@Column(name = "color_hex")
	public String colorHex;

	@OneToMany(mappedBy = "myShopPrimeProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

	@Column(name = "status")
	public String status;

	// public Shop shop;

	@Column(name = "delivery_time")
	public String deliveryTime;

	@Column(name = "short_url")
	public String shortUrl;

	@Column(name = "show_un_available")
	public boolean showUnAvailable;

	@Column(name = "category_name")
	public String categoryName;

	@Column(name = "return_text")
	public String returnText;

	@Column(name = "is_copied_product")
	public boolean isCopiedProduct;

	@Column(name = "is_product_in_cart")
	public boolean isProductInCart;

	@Column(name = "concat_title")
	public String concatTitle;

	@Column(name = "parent_parent_categoryId")
	public double parentParentCategoryId;

	@Column(name = "percentage_off")
	public double percentageOff;

	@Column(name = "mrp")
	public double mrp;

	@Column(name = "product_title")
	public String productTitle;

	@Column(name = "short_url_key")
	public String shortUrlkey;

	@Column(name = "cod_available")
	public boolean codAvailable;

	@Column(name = "show_prime_assured")
	public boolean showPrimeAssured;

	@Column(name = "total_views")
	public double totalViews;

	@Column(name = "parent_category_id")
	public Double parentCategoryId;

	@Column(name = "sale_timer")
	public long saleTimer;

	@OneToMany(mappedBy = "myShopPrimeProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Thumbnail> thumbnail = new ArrayList<Thumbnail>();

	@Column(name = "category_id")
	public Double categoryId;

	@Column(name = "show_buy")
	public boolean showBuy;

	@Column(name = "shoppee_product_url")
	public String shoppeeProductUrl;

	@OneToMany(mappedBy = "myShopPrimeProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<SizeChart> sizeChart = new ArrayList<SizeChart>();

}
