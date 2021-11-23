package com.advance.dataloader.repo.base.impl;

import com.advance.dataloader.repo.base.CommonCustomRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @Description	: 公共自定义Repository 实现
 * @ClassName		: CommonCustomRepositoryImpl
 * @date 			: 2017年4月21日 上午10:44:30 
 * 
 * @param <T>
 * @param <ID>
 */
public class CommonCustomRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements CommonCustomRepository<T, ID> {

	/**
	 * @param domainClass
	 * @param em
	 */
	public CommonCustomRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		// TODO Auto-generated constructor stub
	}

	// protected final EntityManager entityManager;


	public CommonCustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		// TODO Auto-generated constructor stub
	}

}