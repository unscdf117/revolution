package com.unsc.shard.service.impl;

import com.unsc.shard.bean.Address;
import com.unsc.shard.mapper.AddressMapper;
import com.unsc.shard.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AddressServiceImpl implements AddressService {
	
	@Resource
	private AddressMapper addressMapper;

	@Override
	public void save(Address address) {
		// TODO Auto-generated method stub
		addressMapper.save(address);	
	}

	@Override
	public Address get(Long id) {
		// TODO Auto-generated method stub
		return addressMapper.get(id);
	}

}
