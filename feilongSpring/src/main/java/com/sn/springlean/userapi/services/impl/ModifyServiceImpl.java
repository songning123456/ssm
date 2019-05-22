package com.sn.springlean.userapi.services.impl;

import com.sn.springlean.framework.annotation.SnService;
import com.sn.springlean.userapi.services.ModifyService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@SnService
public class ModifyServiceImpl implements ModifyService {

	/**
	 * 增加
	 */
	@Override
	public String add(String name,String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	@Override
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	@Override
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
