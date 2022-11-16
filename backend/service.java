package com.calsoft.ecom.service.medicaldata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.calsoft.ecom.HttpDownloadUtility;
import com.calsoft.ecom.model.ResponseWrapper;
import com.calsoft.ecom.model.myshoppe.Breadcrumb;
import com.calsoft.ecom.model.myshoppe.BuyerPriceDetails;
import com.calsoft.ecom.model.myshoppe.DescItems;
import com.calsoft.ecom.model.myshoppe.MyShopPrimeProduct;
import com.calsoft.ecom.model.myshoppe.ReviewList;
import com.calsoft.ecom.model.myshoppe.SizeChart;
import com.calsoft.ecom.model.myshoppe.Thumbnail;
import com.calsoft.ecom.model.utils.ResponseCodes02;
import com.calsoft.ecom.repository.BuyerPriceDetailsJpaRepository;
import com.calsoft.ecom.repository.MyShopPrimeProductJpaRepository;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyShopPrimeService {

	@Autowired
	private MyShopPrimeProductJpaRepository myShopPrimeProductJpaRepository;

	@Autowired
	private BuyerPriceDetailsJpaRepository buyerPriceDetailsJpaRepository;

	public ResponseWrapper fetchDataFromMyShopee(String id, String token, String url3) throws Exception {
		String url = "https://myshopprime.com/api/" + url3 + "listing?type=shop&sortBy=recency&pageNo=" + id + "&token="
				+ token;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		HttpEntity entity = new HttpEntity(headers);
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		if (result.getStatusCode() == HttpStatus.OK) {

			JSONObject myObject = new JSONObject(result.getBody());

			JSONObject jsonObject = (JSONObject) myObject;

			Object dataObject = jsonObject.get("data");

			JSONObject mainDataObject = (JSONObject) dataObject;

			JSONArray resellingProductArray = (JSONArray) mainDataObject.get("resellingProducts");

			System.out.println(resellingProductArray);

			for (Object resellingProductObject : resellingProductArray) {

				JSONObject myJSONObject = (JSONObject) resellingProductObject;
				String id1 = myJSONObject.getString("id");
				List<MyShopPrimeProduct> productList = myShopPrimeProductJpaRepository.findByMsid(id1);
				if (productList.isEmpty()) {
					String url1 = "https://myshopprime.com/api/product/details/" + id1;
					RestTemplate restTemplate1 = new RestTemplate();
					HttpHeaders headers1 = new HttpHeaders();
					headers.add("Content-Type", "application/json");
					headers.add("Accept", "application/json");
					HttpEntity entity1 = new HttpEntity(headers);
					ResponseEntity<String> result1 = restTemplate.exchange(url1, HttpMethod.GET, entity1, String.class);

					if (result1.getStatusCode() == HttpStatus.OK) {

						JSONObject myObject1 = new JSONObject(result1.getBody());

						Object myObject12 = myObject1.get("product");

						Gson gson = new Gson();

						Map<String, Object> mapObj = gson.fromJson(myObject12.toString(), Map.class);

						System.out.println(mapObj);

						setProductData(mapObj, url1, id1, token);
					}

				} else {
					MyShopPrimeProduct myShopPrimeProduct = productList.get(0);
					List<BuyerPriceDetails> buyerPriceDetails = buyerPriceDetailsJpaRepository.findByBuyerName(token);
					if (buyerPriceDetails.isEmpty()) {
						setBuyerPriceDetails(myShopPrimeProduct.getBuyerPrice(), myShopPrimeProduct,
								token, 1);
					}
				}

			}

		}

		return null;
	}

	private void setProductData(Map<String, Object> myObject122, String url, String id1, String token)
			throws JSONException, IOException, ParseException {

		MyShopPrimeProduct myShopPrimeProduct = new MyShopPrimeProduct();

		if (myObject122.get("productId") != null) {
			myShopPrimeProduct.setProductId(Long.valueOf(myObject122.get("productId").toString().split("\\.")[0]));
		}

		if (myObject122.get("productId") != null) {
			myShopPrimeProduct.setMspid(Long.valueOf(myObject122.get("productId").toString().split("\\.")[0]));
		}

		myShopPrimeProduct.setMsid(String.valueOf(id1));

		if (myObject122.get("showAdminRejected") != null) {
			myShopPrimeProduct.setShowAdminRejected(Boolean.valueOf(myObject122.get("showAdminRejected").toString()));
		}

		if (myObject122.get("showsizechart") != null) {
			myShopPrimeProduct.setShowSizeChart(Boolean.valueOf(myObject122.get("showsizechart").toString()));
		}

		if (myObject122.get("freeDelivery") != null) {
			myShopPrimeProduct.setFreeDelivery(myObject122.get("freeDelivery").toString());
		}

		if (myObject122.get("isAllowEdit") != null) {
			myShopPrimeProduct.setAllowEdit(Boolean.valueOf(myObject122.get("isAllowEdit").toString()));
		}

		if (myObject122.get("score") != null) {
			myShopPrimeProduct.setScore(Double.valueOf(myObject122.get("score").toString()));
		}

		if (myObject122.get("createdAt") != null) {
			myShopPrimeProduct.setCreatedAt(myObject122.get("createdAt").toString());
		}

		if (myObject122.get("buyerPrice") != null) {
			myShopPrimeProduct.setBuyerPrice(myObject122.get("buyerPrice").toString());
		}

		if (myObject122.get("fbPageShare") != null) {
			myShopPrimeProduct.setFbPageShare((myObject122.get("fbPageShare").toString()));
		}

		if (myObject122.get("id") != null) {
			myShopPrimeProduct.setPrimaryId((myObject122.get("id").toString()));
		}

		if (myObject122.get("shopId") != null) {
			myShopPrimeProduct.setShopId(Double.valueOf(myObject122.get("shopId").toString()));
		}

		if (myObject122.get("availableQty") != null) {
			myShopPrimeProduct.setAvailableQty(Double.valueOf(myObject122.get("availableQty").toString()));
		}

		if (myObject122.get("sku") != null) {
			myShopPrimeProduct.setSku((myObject122.get("sku").toString()));
		}

		if (myObject122.get("productDescription") != null) {
			myShopPrimeProduct.setProductDescription((myObject122.get("productDescription").toString()));
		} else {
			myShopPrimeProduct.setProductDescription((myObject122.get("productTitle").toString()));
		}
		if (myObject122.get("updatedAt") != null) {
			myShopPrimeProduct.setUpdatedAt((myObject122.get("updatedAt").toString()));
		}
		Map<String, Object> thumnail = (Map<String, Object>) myObject122.get("thumbnail");

		myShopPrimeProduct.setImageUrl((thumnail.get("imageUrl").toString()));

		if (myObject122.get("colorText") != null) {
			myShopPrimeProduct.setColorText((myObject122.get("colorText").toString()));
		}

		if (myObject122.get("isAdminRejected") != null) {
			myShopPrimeProduct.setAdminRejected(Boolean.valueOf(myObject122.get("isAdminRejected").toString()));
		}

		if (myObject122.get("shippingCharges") != null) {
			myShopPrimeProduct.setShippingCharges(Double.valueOf(myObject122.get("shippingCharges").toString()));
		}

		if (myObject122.get("displayBuyerPrice") != null) {
			myShopPrimeProduct.setDisplayBuyerPrice((myObject122.get("displayBuyerPrice").toString()));
		}

		if (myObject122.get("productSlug") != null) {
			myShopPrimeProduct.setProductSlug((myObject122.get("productSlug").toString()));
		}

		if (myObject122.get("sellerMappingId") != null) {
			myShopPrimeProduct.setSellerMappingId(Double.valueOf(myObject122.get("sellerMappingId").toString()));
		}

		if (myObject122.get("showContact") != null) {
			myShopPrimeProduct.setShowContact(Boolean.valueOf(myObject122.get("showContact").toString()));
		}

		if (myObject122.get("colorHex") != null) {
			myShopPrimeProduct.setColorHex((myObject122.get("colorHex").toString()));
		}

		if (myObject122.get("status") != null) {
			myShopPrimeProduct.setStatus((myObject122.get("status").toString()));
		}

		if (myObject122.get("deliveryTime") != null) {
			myShopPrimeProduct.setDeliveryTime((myObject122.get("deliveryTime").toString()));
		}

		if (myObject122.get("shortUrl") != null) {
			myShopPrimeProduct.setShortUrl((myObject122.get("shortUrl").toString()));
		}

		if (myObject122.get("showUnAvailable") != null) {
			myShopPrimeProduct.setShowUnAvailable(Boolean.valueOf(myObject122.get("showUnAvailable").toString()));
		}

		if (myObject122.get("categoryName") != null) {
			myShopPrimeProduct.setCategoryName((myObject122.get("categoryName").toString()));
		}

		if (myObject122.get("returnText") != null) {
			myShopPrimeProduct.setReturnText((myObject122.get("returnText").toString()));
		}

		if (myObject122.get("isCopiedProduct") != null) {
			myShopPrimeProduct.setCopiedProduct(Boolean.valueOf(myObject122.get("isCopiedProduct").toString()));
		}

		if (myObject122.get("isProductInCart") != null) {
			myShopPrimeProduct.setProductInCart(Boolean.valueOf(myObject122.get("isProductInCart").toString()));
		}

		if (myObject122.get("parentParentCategoryId") != null) {
			myShopPrimeProduct
					.setParentParentCategoryId(Double.valueOf(myObject122.get("parentParentCategoryId").toString()));
		}

		if (myObject122.get("percentageOff") != null) {
			myShopPrimeProduct.setPercentageOff(Double.valueOf(myObject122.get("percentageOff").toString()));
		}

		if (myObject122.get("mrp") != null) {
			myShopPrimeProduct.setMrp(Double.valueOf(myObject122.get("mrp").toString()));
		}

		if (myObject122.get("productTitle") != null) {
			myShopPrimeProduct.setProductTitle((myObject122.get("productTitle").toString()));
		}

		if (myObject122.get("shortUrlkey") != null) {
			myShopPrimeProduct.setShortUrlkey((myObject122.get("shortUrlkey").toString()));
		}

		if (myObject122.get("codAvailable") != null) {
			myShopPrimeProduct.setCodAvailable(Boolean.valueOf(myObject122.get("codAvailable").toString()));
		} else {
			myShopPrimeProduct.setCodAvailable(false);
		}

		if (myObject122.get("showPrimeAssured") != null) {
			myShopPrimeProduct.setShowPrimeAssured(Boolean.valueOf(myObject122.get("showPrimeAssured").toString()));
		}

		if (myObject122.get("totalViews") != null) {
			myShopPrimeProduct.setTotalViews(Double.valueOf(myObject122.get("totalViews").toString()));
		}

		if (myObject122.get("parentCategoryId") != null) {
			myShopPrimeProduct.setParentCategoryId(Double.valueOf(myObject122.get("parentCategoryId").toString()));
		}

		if (myObject122.get("categoryId") != null) {
			myShopPrimeProduct.setCategoryId(Double.valueOf(myObject122.get("categoryId").toString()));
		}

		if (myObject122.get("showBuy") != null) {
			myShopPrimeProduct.setShowBuy(Boolean.valueOf(myObject122.get("showBuy").toString()));
		}

		myShopPrimeProduct.setShoppeeProductUrl(url);

		DescItems descItems = new DescItems();
		setDescItems(myObject122, descItems, myShopPrimeProduct);
		setReviewList(myObject122, myShopPrimeProduct);
		setBreadcrumbsList(myObject122, myShopPrimeProduct);
		setthumbnailsList(myObject122, myShopPrimeProduct);

		String buyerPrice = myObject122.get("displayBuyerPrice").toString();

		setBuyerPriceDetails(buyerPrice, myShopPrimeProduct, token, 0);
		boolean size = Boolean.valueOf(myObject122.get("showsizechart").toString());

		if (size) {
			setSizeChart(myObject122, myShopPrimeProduct);
		}

		myShopPrimeProductJpaRepository.save(myShopPrimeProduct);

	}

	private void setBuyerPriceDetails(String buyerPrice, MyShopPrimeProduct myShopPrimeProduct, String token,
			int value) {

		if (value == 0) {

			BuyerPriceDetails buyerPriceDetails = new BuyerPriceDetails();

			buyerPriceDetails.setBuyerId(0);

			buyerPriceDetails.setBuyerName(token);

			buyerPriceDetails.setBuyerPrice(buyerPrice);

			buyerPriceDetails.setMyShopPrimeProduct(myShopPrimeProduct);

			myShopPrimeProduct.getBuyerPriceDetails().add(buyerPriceDetails);

		} else {

			BuyerPriceDetails buyerPriceDetails = new BuyerPriceDetails();

			buyerPriceDetails.setBuyerId(0);

			buyerPriceDetails.setBuyerName(token);

			buyerPriceDetails.setBuyerPrice(buyerPrice);

			buyerPriceDetails.setMyShopPrimeProduct(myShopPrimeProduct);

			myShopPrimeProduct.getBuyerPriceDetails().add(buyerPriceDetails);

			myShopPrimeProductJpaRepository.save(myShopPrimeProduct);

		}

	}

	private void setSizeChart(Map<String, Object> myObject122, MyShopPrimeProduct myShopPrimeProduct) {

		@SuppressWarnings("unchecked")
		List<String> sizeChartArray = (List<String>) myObject122.get("sizeChartrows");
		List<SizeChart> sizeChartList = new ArrayList<SizeChart>();

		for (Object sizeChartObject : sizeChartArray) {

			String size[] = sizeChartObject.toString().split(",");
			SizeChart sizeChart = new SizeChart();
			String sizeCharta = size[0].substring(1);
			sizeChart.setSize(sizeCharta);
			sizeChart.setSizeChartId(0);
			sizeChart.setValue(size[1].substring(0, size[1].length() - 1));
			sizeChart.setMyShopPrimeProduct(myShopPrimeProduct);
			sizeChartList.add(sizeChart);
		}

		myShopPrimeProduct.setSizeChart(sizeChartList);

	}

	private void setthumbnailsList(Map<String, Object> myObject122, MyShopPrimeProduct myShopPrimeProduct)
			throws JSONException, IOException {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> thumbnailsArray = (List<Map<String, Object>>) myObject122.get("thumbnails");
		List<Thumbnail> thumbnailList = new ArrayList<Thumbnail>();

		if (thumbnailsArray != null) {

			for (Map<String, Object> thumbnailObject : thumbnailsArray) {
				JSONObject thumbnailsArrayObject = new JSONObject(thumbnailObject);
				Thumbnail thumbnail = new Thumbnail();
				thumbnail.setThumbnailId(0);
				thumbnail.setImageUrl(thumbnailsArrayObject.get("imageUrl").toString());
				thumbnail.setOrigin80ImageUrl(thumbnailsArrayObject.get("origin80ImageUrl").toString());
				thumbnail.setNaturalWidth(
						Long.valueOf(thumbnailsArrayObject.get("naturalWidth").toString().split("\\.")[0]));
				thumbnail.setNaturalHeight(
						Long.valueOf(thumbnailsArrayObject.get("naturalHeight").toString().split("\\.")[0]));
				thumbnail.setImageName(setImageName(thumbnailsArrayObject.get("imageUrl").toString()));
				thumbnail.setMyShopPrimeProduct(myShopPrimeProduct);
				thumbnailList.add(thumbnail);
				storeImage(thumbnailsArrayObject.get("imageUrl").toString(), myShopPrimeProduct.getSku(),
						thumbnail.getImageName());
			}
			myShopPrimeProduct.setThumbnail(thumbnailList);

		} else {
			String imageUrl = myShopPrimeProduct.getImageUrl();

			Thumbnail thumbnail = new Thumbnail();
			thumbnail.setThumbnailId(0);

			thumbnail.setThumbnailId(0);
			thumbnail.setImageUrl(imageUrl);
			thumbnail.setOrigin80ImageUrl(imageUrl);
			thumbnail.setNaturalWidth(Long.valueOf(300));
			thumbnail.setNaturalHeight(Long.valueOf(300));
			thumbnail.setImageName(setImageName(imageUrl));
			thumbnail.setMyShopPrimeProduct(myShopPrimeProduct);
			thumbnailList.add(thumbnail);
			storeImage(imageUrl, myShopPrimeProduct.getSku(), thumbnail.getImageName());

		}

	}

	private static final String EXTERNAL_FILE_PATH = "C:/myshoppee/";

	private void storeImage(String imageUrl, String sku, String imageName) throws IOException {

		checkAndCreate(EXTERNAL_FILE_PATH + sku);

		String saveDir = EXTERNAL_FILE_PATH + sku;
		HttpDownloadUtility.downloadFile1(imageUrl, saveDir, imageName);

	}

	public static void checkAndCreate(String folderPath) throws IOException {
		Path path = Paths.get(folderPath);
		if (!Files.exists(path)) {
			Files.createDirectory(path);
			log.info("Directory created...{}", folderPath);
		} else {
			log.info("Directory already exists");
		}

	}

	private String setImageName(String imageName) {

		String arr1[] = imageName.split("/");

		String finalValue = arr1[arr1.length - 1];

		String arr2[] = finalValue.split("\\?");

		finalValue = arr2[0];

		return finalValue;
	}

	private void setBreadcrumbsList(Map<String, Object> myObject122, MyShopPrimeProduct myShopPrimeProduct) {

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> breadCrumpArray = (List<Map<String, Object>>) myObject122.get("breadcrumbs");

		breadCrumpArray.remove(0);

		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();

		for (Map<String, Object> breadCrumpArrayObject : breadCrumpArray) {

			Breadcrumb breadcrumb = new Breadcrumb();

			breadcrumb.setName(breadCrumpArrayObject.get("name").toString());

			breadcrumb.setUrl(breadCrumpArrayObject.get("url").toString());

			breadcrumb.setBreadCrumnId(0);

			breadcrumb.setProductId(myShopPrimeProduct.getProductId());

			breadcrumb.setMyShopPrimeProduct(myShopPrimeProduct);

			breadcrumbs.add(breadcrumb);
		}
		myShopPrimeProduct.setBreadcrumbs(breadcrumbs);

	}

	private void setReviewList(Map<String, Object> myObject122, MyShopPrimeProduct myShopPrimeProduct) {
		List<ReviewList> reviewList = new ArrayList<ReviewList>();

		@SuppressWarnings("unchecked")
		Map<String, Object> thumnailObject = (Map<String, Object>) myObject122.get("reviews");

		@SuppressWarnings("unchecked")
		Map<String, Object> reviewObject = (Map<String, Object>) thumnailObject.get("ratingAndImages");

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> ratingAndImagesObject = (List<Map<String, Object>>) reviewObject.get("reviewList");

		for (Map<String, Object> dataArrayObject : ratingAndImagesObject) {

			ReviewList reviewListObject = new ReviewList();

			if (dataArrayObject.get("reviewerName") != null) {
				reviewListObject.setReviewerName(dataArrayObject.get("reviewerName").toString());
			}
			if (dataArrayObject.get("createdDate") != null) {
				reviewListObject.setCreatedDate(dataArrayObject.get("createdDate").toString());
			}
			reviewListObject.setProductId(myShopPrimeProduct.getProductId());
			if (dataArrayObject.get("reviewWithEmoji") != null) {
				reviewListObject.setReviewWithEmoji(dataArrayObject.get("reviewWithEmoji").toString());
			}
			if (dataArrayObject.get("review") != null) {
				reviewListObject.setReview(dataArrayObject.get("review").toString());
			}
			if (dataArrayObject.get("displayDate") != null) {
				reviewListObject.setDisplayDate(dataArrayObject.get("displayDate").toString());
			}
			if (dataArrayObject.get("rating") != null) {
				reviewListObject.setRating(Double.valueOf(dataArrayObject.get("rating").toString()));
			}
			if (dataArrayObject.get("id") != null) {
				reviewListObject.setReviewId(Long.valueOf(dataArrayObject.get("id").toString().split("\\.")[0]));
			}
			if (dataArrayObject.get("reviewerImage") != null) {
				reviewListObject.setReviewerImage(dataArrayObject.get("reviewerImage").toString());
			}
			if (dataArrayObject.get("userId") != null) {
				reviewListObject.setUserId(Long.valueOf(dataArrayObject.get("userId").toString().split("\\.")[0]));
			}
			reviewListObject.setReviewId(0);
			reviewListObject.setMyShopPrimeProduct(myShopPrimeProduct);
			reviewList.add(reviewListObject);

		}

		myShopPrimeProduct.setReviewList(reviewList);

	}

	private void setDescItems(Map<String, Object> myObject122, DescItems descItems,
			MyShopPrimeProduct myShopPrimeProduct) {

		Map<String, Object> thumnailObject = (Map<String, Object>) myObject122.get("descItems");

		if (thumnailObject.get("Fabric") != null) {
			descItems.setFabric(thumnailObject.get("Fabric").toString());
		}
		if (thumnailObject.get("Type") != null) {
			descItems.setType(thumnailObject.get("Type").toString());

		}
		if (thumnailObject.get("Design Type") != null) {
			descItems.setDesignType(thumnailObject.get("Design Type").toString());
		}
		if (thumnailObject.get("Fit Type") != null) {
			descItems.setFitType(thumnailObject.get("Fit Type").toString());
		}
		if (thumnailObject.get("Country Of Origin") != null) {
			descItems.setCountryOfOrigin(thumnailObject.get("Country Of Origin").toString());
		}

		if (thumnailObject.get("type") != null) {
			descItems.setType(thumnailObject.get("type").toString());
		}

		if (thumnailObject.get("length") != null) {
			descItems.setLength(thumnailObject.get("length").toString());
		}
		if (thumnailObject.get("width") != null) {
			descItems.setWidth(thumnailObject.get("width").toString());
		}
		if (thumnailObject.get("style") != null) {
			descItems.setStyle(thumnailObject.get("style").toString());
		}
		descItems.setProductId(myShopPrimeProduct.getProductId());

		descItems.setMyShopPrimeProduct(myShopPrimeProduct);
		myShopPrimeProduct.setDescItems(descItems);
	}

	public Page<MyShopPrimeProduct> searchMyShopService(Specification<MyShopPrimeProduct> specs, Pageable pageable) {
		return myShopPrimeProductJpaRepository.findAll(Specification.where(specs), pageable);
	}

	public Page<MyShopPrimeProduct> fetchAllMyShopService(Pageable pageable) {
		return myShopPrimeProductJpaRepository.findAll(pageable);
	}

	public ResponseWrapper deleteByProductId(Long productId) {
		try {
			myShopPrimeProductJpaRepository.deleteByProductId(productId);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper(false, ResponseCodes02.DELETE_SHOPPEE_FAILURE.getCode(),
					ResponseCodes02.DELETE_SHOPPEE_FAILURE.getDescription());
		}
		return new ResponseWrapper(true, ResponseCodes02.DELETE_SHOPPEE_SUCCESS.getCode(),
				ResponseCodes02.DELETE_SHOPPEE_SUCCESS.getDescription());
	}

}
