package com.unsc.shard.web.api;

import com.unsc.shard.bean.Address;
import com.unsc.shard.service.AddressService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class AddressController {
	
	@Resource
	private AddressService addressService;
	
	@RequestMapping("/address/save")
	@ResponseBody
	public String save(Address address) {
		addressService.save(address);
		return "success";
	}
	
	@RequestMapping("/address/get/{id}")
	@ResponseBody
	public Address get(@PathVariable Long id) {
		return addressService.get(id);
	}
}
