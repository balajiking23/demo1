package com.calsoft.ecom.controller.medicaldata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import org.springframework.data.domain.Sort;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.calsoft.ecom.config.CacheServer;
import com.calsoft.ecom.model.CoreConfigData;
import com.calsoft.ecom.model.DeliveryPeople;
import com.calsoft.ecom.model.ResponseWrapper;
import com.calsoft.ecom.model.myshoppe.MyShopPrimeProduct;
import com.calsoft.ecom.model.utils.ResponseCodes02;
import com.calsoft.ecom.repository.ConfigRepository;
import com.calsoft.ecom.repository.MyShopPrimeProductJpaRepository;
import com.calsoft.ecom.service.medicaldata.MyShopPrimeService;
import com.calsoft.ecom.utils.MyShopprimeExporter;
import com.calsoft.springsearch.anotation.SearchSpec;
import com.calsoft.utils.ExcelUtils;

@RequestMapping("/api")
@RestController
public class MyShopPrimeController {

	
	@Autowired
	public MyShopPrimeService myShopPrimeService ;
	
	
	@Autowired
	private MyShopPrimeProductJpaRepository myShopPrimeProductRepository;
	
	@Autowired
	private CacheServer cacheServer;
	
	@Autowired
	private ConfigRepository configRepository;
	
	Logger logger = LoggerFactory.getLogger(MyShopPrimeService.class);
	
	@GetMapping("/catalog/myShopPrime/{id}/{token}")
	public ResponseWrapper findBymyShopPrimeId(@PathVariable(value = "id") String id,
			@PathVariable(value = "token") String token,@RequestParam(value = "url") String url) throws Exception {
		return myShopPrimeService.fetchDataFromMyShopee(id,token,url);
	}
	
	@GetMapping("/myShopPrime/page/filter")
	public Page<MyShopPrimeProduct> searchFooterIcon(@SearchSpec Specification<MyShopPrimeProduct> specs, Pageable pageable) {
		return myShopPrimeService.searchMyShopService(specs, pageable);
	}
	
	@GetMapping("/myShopPrime")
	public Page<MyShopPrimeProduct> fetchAllMyShopService(Pageable pageable) {
		return myShopPrimeService.fetchAllMyShopService(pageable);
	}
	
	@DeleteMapping("/myShopPrime/{id}")
	public ResponseWrapper deleteByProductId(@PathVariable(value = "id")Long productId) throws ParseException {
		return myShopPrimeService.deleteByProductId(productId);

	}
	
	
	private StreamingResponseBody getOutputStream(InputStream inputStream2) {
		return outputStream -> {
			int nRead;
			byte[] data = new byte[1024];
			while ((nRead = inputStream2.read(data, 0, data.length)) != -1) {
				outputStream.write(data, 0, nRead);
			}

		};
	}
	
	@GetMapping("/myShopPrime/export")
	public StreamingResponseBody exportToExcelData(HttpServletResponse response,
			@RequestHeader("x-tenant") String productId) {
		StreamingResponseBody outputStream = null;
		try {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + ExcelUtils.PRODUCT_SHOPPRIME_TABLE_TEMPLATE);
			response.setHeader("filename", ExcelUtils.PRODUCT_SHOPPRIME_TABLE_TEMPLATE);
			
		    Pageable pageableMin = PageRequest.of(0, 1000, Sort.by("productId").descending());
			Page<MyShopPrimeProduct> myShopPrimeProductPage=myShopPrimeProductRepository.findAll(pageableMin);
			List<MyShopPrimeProduct> myShopPrimeProduct = myShopPrimeProductPage.getContent();
			Object value = getThemeDetails(productId);
			MyShopprimeExporter excelExporter = new MyShopprimeExporter();
			final byte[] bytes = excelExporter.export(response, productId,myShopPrimeProduct);
			if (bytes != null && bytes.length > 0) {
				logger.info("Report generated successfully...");
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				outputStream = getOutputStream(byteArrayInputStream);
			} else {
				logger.info("Report Generation failed ");
			}
		} catch (Exception exp) {
			logger.error("Exception while trying to generate report ", exp);
			constructResponseEntity(false, ResponseCodes02.REPORT_FAILED.getCode(),
					ResponseCodes02.REPORT_FAILED.getDescription());

		}
		return outputStream;
	}

	public Object getThemeDetails(String productId) {
		Object coreConfigData = new CoreConfigData();
		try {

			String key = "coreConfig::" + productId;

			Object keys = cacheServer.get(key);
			if (keys == null) {
				coreConfigData = configRepository.getAllDetails(productId);
				cacheServer.set(key, coreConfigData);
			} else {
				coreConfigData = keys;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return coreConfigData;

	}
	
	public ResponseEntity<ResponseWrapper> constructResponseEntity(Object responseObj, String code, String message) {
		ResponseWrapper response = new ResponseWrapper(responseObj, code, message);
		ResponseEntity<ResponseWrapper> responseEntity = new ResponseEntity<ResponseWrapper>(response, HttpStatus.OK);
		return responseEntity;
	}

}
