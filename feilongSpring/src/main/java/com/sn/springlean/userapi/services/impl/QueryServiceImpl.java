package com.sn.springlean.userapi.services.impl;

import com.sn.springlean.framework.annotation.SnService;
import com.sn.springlean.userapi.services.QueryService;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 * @author Tom
 *
 */
@SnService
public class QueryServiceImpl implements QueryService {

	/**
	 * 查询
	 */
	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
