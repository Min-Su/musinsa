package com.bogue.assignment.api.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bogue.assignment.api.dto.UserPointDto;

@Mapper
public interface UserPointMapper {

	UserPointDto selectUserPoint(Long userId);

	void insertUserPoint(UserPointDto dto);

    void updateBalanceToUserPoint(UserPointDto dto);
	
}
