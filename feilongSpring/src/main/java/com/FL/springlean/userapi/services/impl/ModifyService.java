package com.FL.springlean.userapi.services.impl;

import com.FL.springlean.framework.annotation.FLService;
import com.FL.springlean.userapi.services.IModifyService;

/**
 * 增删改业务
 * @author Tom
 *
 */
@FLService
public class ModifyService implements IModifyService {

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
