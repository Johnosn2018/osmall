package com.imooc.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.item.service.ItemsService;
import com.imooc.web.service.CulsterService;

@Service("buyService")
public class CulsterServiceImpl implements CulsterService {
	
	final static Logger log = LoggerFactory.getLogger(CulsterServiceImpl.class);
	
	@Autowired
	private ItemsService itemService;


	
	@Override
	public void doBuyItem(String itemId) {
		// 减少库存
		itemService.displayReduceCounts(itemId, 1);

	}
	

	
}

