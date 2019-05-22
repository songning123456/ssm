package com.sn.springlean.userapi.services;

/**
 * 增删改业�?
 * @author Tom
 *
 */
public interface ModifyService {

	/**
	 * 增加
	 */
	String add(String name, String addr);
	
	/**
	 * 修改
	 */
	String edit(Integer id, String name);
	
	/**
	 * 删除
	 */
	String remove(Integer id);
	
}
