package com.unsc.shard.service;

import com.unsc.shard.bean.Address;

public interface AddressService {

	void save(Address address);

	Address get(Long id);
}
