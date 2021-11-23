package com.advance.dataloader.repo.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;


/**
 * @Description	: 公共自定义Repository
 * @ClassName		: CommonCustomRepository 
 * @author			: Advance
 * @date 			: 2017年4月21日 上午10:45:10 
 * 
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface CommonCustomRepository<T, ID extends Serializable>
	extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}