package com.calsoft.ecom.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.calsoft.ecom.model.myshoppe.Breadcrumb;
import com.calsoft.ecom.model.myshoppe.MyShopPrimeProduct;
import com.calsoft.ecom.model.myshoppe.SizeChart;
import com.calsoft.ecom.model.myshoppe.Thumbnail;

@Component
public class MyShopprimeExporter {

	HSSFWorkbook workbook = new HSSFWorkbook();
	HSSFSheet sheet = workbook.createSheet("My-Shoppe_Prouct_Data");

	private void writeHeaderLine1() {
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		createCell(row, 0, "product_id", style);
		createCell(row, 1, "product_title", style);
		createCell(row, 2, "sku", style);
		createCell(row, 3, "product_description", style);
		createCell(row, 4, "special_price", style);
		createCell(row, 5, "available_qty", style);
		createCell(row, 6, "shoppee_product_url", style);
		createCell(row, 7, "image_url", style);
		createCell(row, 8, "status", style);
		createCell(row, 9, "category_name", style);
		createCell(row, 10, "cod_available_a", style);
		createCell(row, 11, "delivery_time_a", style);
		createCell(row, 12, "return_text_a", style);
		createCell(row, 13, "show_size_chart_a", style);
		createCell(row, 14, "free_delivery_a", style);
		createCell(row, 15, "color_hex_a", style);
		createCell(row, 16, "color_text_a", style);
		createCell(row, 17, "product_slug", style);
		createCell(row, 18, "category_list_Names", style);
		createCell(row, 19, "imageList", style);
		createCell(row, 20, "fabric_a", style);
		createCell(row, 21, "type_a", style);
		createCell(row, 22, "designType_a", style);
		createCell(row, 23, "fitType_a", style);
		createCell(row, 24, "countryOfOrigin_a", style);
		createCell(row, 25, "length_a", style);
		createCell(row, 26, "width_a", style);
		createCell(row, 27, "style_a", style);
		createCell(row, 28, "size_a", style);
		createCell(row, 29, "price", style);
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		// sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines1(List<MyShopPrimeProduct> myShopPrimeProduct, String productId) {

		int[] rowCount = { 0 };
		CellStyle style = workbook.createCellStyle();

		myShopPrimeProduct.parallelStream().forEachOrdered(product -> {
			if (!product.showSizeChart) {
				Row row = sheet.createRow(rowCount[0]++);
				setSales(style, row, product, "0");
			} else {

				for (SizeChart myShopPrimeProduct2 : product.getSizeChart()) {
					Row row = sheet.createRow(rowCount[0]++);
					setSales(style, row, product, myShopPrimeProduct2.getSize());
				}

			}

		});

	}

	private void setSales(CellStyle style, Row row, MyShopPrimeProduct product, String value) {
		int columnCount = 0;
		createCell(row, columnCount++, product.getProductId(), style);
		createCell(row, columnCount++, product.getProductTitle(), style);
		createCell(row, columnCount++, product.getSku(), style);
		createCell(row, columnCount++, product.getProductDescription(), style);
		createCell(row, columnCount++, getTotal(product.getBuyerPrice()), style);
		createCell(row, columnCount++, product.getAvailableQty(), style);
		createCell(row, columnCount++, product.getShoppeeProductUrl(), style);
		createCell(row, columnCount++, product.getImageUrl(), style);
		createCell(row, columnCount++, product.getStatus(), style);
		createCell(row, columnCount++, product.getCategoryName(), style);
		createCell(row, columnCount++, product.isCodAvailable(), style);
		createCell(row, columnCount++, product.getDeliveryTime(), style);
		createCell(row, columnCount++, product.getReturnText(), style);
		createCell(row, columnCount++, product.isShowSizeChart(), style);
		createCell(row, columnCount++, product.getFreeDelivery(), style);
		createCell(row, columnCount++, product.getColorHex(), style);
		createCell(row, columnCount++, product.getColorText(), style);
		createCell(row, columnCount++, product.getProductSlug(), style);
		createCell(row, columnCount++, getCategoryIdList(product), style);
		createCell(row, columnCount++, getImageList(product), style);
		createCell(row, columnCount++, product.getDescItems().getFabric(), style);
		createCell(row, columnCount++, product.getDescItems().getType(), style);
		createCell(row, columnCount++, product.getDescItems().getDesignType(), style);
		createCell(row, columnCount++, product.getDescItems().getFitType(), style);
		createCell(row, columnCount++, product.getDescItems().getCountryOfOrigin(), style);
		createCell(row, columnCount++, product.getDescItems().getLength(), style);
		createCell(row, columnCount++, product.getDescItems().getWidth(), style);
		createCell(row, columnCount++, product.getDescItems().getStyle(), style);
		createCell(row, columnCount++, value, style);
		createCell(row, columnCount++, getTotal(product.getBuyerPrice())*2, style);

	}

	private Double getTotal(String buyerPrice) {
		double finalPrice = 0.0;
		try {

			buyerPrice = buyerPrice.replace(",","");

			finalPrice = Double.valueOf(buyerPrice);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return finalPrice;
	}

	private Object getImageList(MyShopPrimeProduct product) {
		String thumbnailImages = null;

		int i = 0;

		if (!product.getThumbnail().isEmpty()) {
			for (Thumbnail thumbnailObject : product.getThumbnail()) {

				if (i == 0) {
					thumbnailImages = thumbnailObject.getImageName();
				} else {
					thumbnailImages = thumbnailImages + "," + thumbnailObject.getImageName();
				}

				i++;

			}
		} else {
			thumbnailImages = setImageName(product.getImageUrl());
		}

		return thumbnailImages;
	}

	private String setImageName(String imageName) {

		String arr1[] = imageName.split("/");

		String finalValue = arr1[arr1.length - 1];

		String arr2[] = finalValue.split("\\?");

		finalValue = arr2[0];

		return finalValue;
	}

	private Object getCategoryIdList(MyShopPrimeProduct product) {

		String categoryNames = null;

		int i = 0;
		for (Breadcrumb breadCrumbObject : product.getBreadcrumbs()) {

			if (i == 0) {
				categoryNames = breadCrumbObject.getName();
			} else {
				categoryNames = categoryNames + "," + breadCrumbObject.getName();
			}

			i++;

		}
		return categoryNames;
	}

	public byte[] export(HttpServletResponse response, String productId, List<MyShopPrimeProduct> myShopPrimeProduct)
			throws IOException {

		writeHeaderLine1();

		writeDataLines1(myShopPrimeProduct, productId);

		ServletOutputStream outputStream = response.getOutputStream();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.writeTo(outputStream);

		try {
			workbook.write(baos);
		} finally {
			baos.close();
		}
		byte[] bytes = baos.toByteArray();
		return bytes;

	}

}
