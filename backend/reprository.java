package com.calsoft.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.calsoft.ecom.model.myshoppe.MyShopPrimeProduct;

@Repository
public interface MyShopPrimeProductJpaRepository extends JpaRepository<MyShopPrimeProduct, Long>, JpaSpecificationExecutor<MyShopPrimeProduct> {

	List<MyShopPrimeProduct> findByMsid(String id1);

	List<MyShopPrimeProduct> findByProductId(Long productId);
	
	@Transactional
	@Modifying // to mark delete or update query
	@Query(value = "DELETE FROM MyShopPrimeProduct e WHERE e.productId = ?1")
	void deleteByProductId(Long productId);

}
