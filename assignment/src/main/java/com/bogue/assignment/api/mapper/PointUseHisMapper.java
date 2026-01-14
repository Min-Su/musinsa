package com.bogue.assignment.api.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bogue.assignment.api.dto.PointUseHisDto;

@Mapper
public interface PointUseHisMapper {

    void insertPointUseHis(PointUseHisDto dto);

    List<PointUseHisDto> selectPointUseHis(Long usePointId);
	
    void deletePointUseHis(Long usePointId);
    
    void updateAmount(Long usePointId, Long amount);
}
