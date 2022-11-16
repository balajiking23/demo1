package com.calsoft.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.calsoft.ecom.model.myshoppe.BuyerPriceDetails;

public interface BuyerPriceDetailsJpaRepository  extends JpaRepository<BuyerPriceDetails, Long>,JpaSpecificationExecutor<BuyerPriceDetails> {

	
	List<BuyerPriceDetails> findByBuyerName(String buyerName);
}
