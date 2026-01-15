package com.bogue.assignment.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bogue.assignment.api.dto.PointDto;

@Mapper
public interface PointMapper {
 
    void insertPoint(PointDto dto);

    PointDto selectPoint(Long pointId);

    List<PointDto> selectUsablePoint(Long userId);
	
    void updateRemainAmount(Long pointId, Long amount);
}
