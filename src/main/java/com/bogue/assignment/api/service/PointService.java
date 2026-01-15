package com.bogue.assignment.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bogue.assignment.api.dto.PointDto;
import com.bogue.assignment.api.dto.PointUseHisDto;
import com.bogue.assignment.api.dto.UserPointDto;
import com.bogue.assignment.api.mapper.PointMapper;
import com.bogue.assignment.api.mapper.PointUseHisMapper;
import com.bogue.assignment.api.mapper.UserPointMapper;
import com.bogue.assignment.config.PointConfigProperties;

@Service
public class PointService {

    private final UserPointMapper userPointMapper;
    private final PointMapper pointMapper;
    private final PointUseHisMapper pointUseHisMapper;
    private final PointConfigProperties config;	
	
    @Autowired
	public PointService(UserPointMapper userPointMapper, PointMapper pointMapper, PointUseHisMapper pointUseHisMapper,
			PointConfigProperties config) {
		super();
		this.userPointMapper = userPointMapper;
		this.pointMapper = pointMapper;
		this.pointUseHisMapper = pointUseHisMapper;
		this.config = config;
	}
    
    private final String POINT_TYPE_EARN = "EARN";
    private final String POINT_TYPE_USE = "USE";
    private final String POINT_FG_MANUAL = "MANUAL";
    private final String POINT_FG_DEFAULT = "DEFAULT";

    @Transactional
    public void earn(Long userId, long amount, boolean manual) {
    	// 포인트 값 검증
        if (amount <= 0 || amount > config.getMaxEarnOnce()) {
            throw new IllegalArgumentException();
        }

        // 사용자 포인트 잔액 조회
        UserPointDto userPointDto = userPointMapper.selectUserPoint(userId);
        // 조회 내용이 없다면 신규 사용자 포인트 등록 
        if (userPointDto == null) {
        	userPointDto = new UserPointDto();
        	userPointDto.setUserId(userId);
        	userPointDto.setPointTotal(amount);
        	userPointMapper.insertUserPoint(userPointDto);
        }

        // 최대 포인트 적립한도 검
        if (userPointDto.getPointTotal() + amount > config.getMaxTotalBalance()) {
            throw new IllegalStateException();
        }

        PointDto pointDto = new PointDto();
        pointDto.setUserId(userId);
        pointDto.setPointType(POINT_TYPE_EARN);
        pointDto.setPointFg(manual ? POINT_FG_MANUAL : POINT_FG_DEFAULT);
        pointDto.setOriginalAmount(amount);
        pointDto.setRemainAmount(amount);
        pointDto.setExpiredDt(
                LocalDateTime.now().plusDays(config.getDefaultExpireDays())
        );

        // 포인트 내역 입력
        pointMapper.insertPoint(pointDto);
        userPointDto.increase(amount);
        // 사용자 포인트 잔액 업데이트
        userPointMapper.updateBalanceToUserPoint(userPointDto);
    }

    @Transactional
    public void use(Long userId, String orderNo, long amount) {

    	// 사용자 포인트 잔액 조회
        UserPointDto userPointDto = userPointMapper.selectUserPoint(userId);
        // 사용 가능 포인트 조회
        List<PointDto> pointDtoList = pointMapper.selectUsablePoint(userId);

        long remain = amount;

        PointDto pointDto = new PointDto();
        pointDto.setUserId(userId);
        pointDto.setPointType(POINT_TYPE_USE);
        pointDto.setOriginalAmount(amount);
        pointDto.setRemainAmount(0);
        pointDto.setExpiredDt(
                LocalDateTime.now().plusDays(config.getDefaultExpireDays())
        );
        pointMapper.insertPoint(pointDto);

        for (PointDto earn : pointDtoList) {
            if (remain == 0) break;

            long used = earn.consume(remain);

            PointUseHisDto pointUseHisDto = new PointUseHisDto();
            pointUseHisDto.setUsePointId(pointDto.getPointId());
            pointUseHisDto.setEarnPointId(earn.getPointId());
            pointUseHisDto.setOrderNo(orderNo);
            pointUseHisDto.setAmount(used);
            pointUseHisMapper.insertPointUseHis(pointUseHisDto);

            remain -= used;
        }

        if (remain > 0) throw new IllegalStateException("잔액 부족");

        userPointDto.decrease(amount);
        userPointMapper.updateBalanceToUserPoint(userPointDto);
    }
    
    @Transactional
    public void cancelUse(Long userId, Long usePointId, long cancelAmount) {

        // 사용 내역 조회
        List<PointUseHisDto> pointUseHisDtoList = pointUseHisMapper.selectPointUseHis(usePointId);

        if (pointUseHisDtoList.isEmpty()) {
            throw new IllegalStateException("취소할 사용 이력 없음");
        }

        long remain = cancelAmount;
        long totalRestore = 0;

        for (PointUseHisDto pointUseHisDto : pointUseHisDtoList) {
            if (remain <= 0) break;

            long restoreAmount = Math.min(pointUseHisDto.getAmount(), remain);

            PointDto pointDto = pointMapper.selectPoint(pointUseHisDto.getEarnPointId());

            // 만료 여부 체크 
            if (pointDto.isExpired()) {
                // 기간만료 내역 신규 적립
                PointDto newEarn = new PointDto();
                newEarn.setUserId(userId);
                newEarn.setPointType(POINT_TYPE_EARN);
                newEarn.setPointFg(POINT_FG_DEFAULT);
                newEarn.setOriginalAmount(restoreAmount);
                newEarn.setRemainAmount(restoreAmount);
                newEarn.setExpiredDt(
                        LocalDateTime.now().plusDays(config.getDefaultExpireDays())
                );
                pointMapper.insertPoint(newEarn);
            } else {
                // 기간만료 아닐시, 기존 적립 복구
            	pointDto.restore(restoreAmount);
                pointMapper.updateRemainAmount(usePointId, restoreAmount);
            }

            // 사용 이력 차감 or 삭제
            if (pointUseHisDto.getAmount() == restoreAmount) {
                pointUseHisMapper.deletePointUseHis(pointUseHisDto.getUsePointId());
            } else {
            	pointUseHisDto.setAmount(pointUseHisDto.getAmount() - restoreAmount);
                pointUseHisMapper.updateAmount(pointUseHisDto.getUsePointId(), pointUseHisDto.getAmount());
            }

            remain -= restoreAmount;
            totalRestore += restoreAmount;
        }

        if (remain > 0) {
            throw new IllegalStateException("취소 금액 초과");
        }

        // 사용자 잔액 복구
        UserPointDto userPointDto = userPointMapper.selectUserPoint(userId);
        userPointDto.increase(totalRestore);
        userPointMapper.updateBalanceToUserPoint(userPointDto);

        // 취소 포인트  기록 (USE_CANCEL)
        PointDto cancelPoint = new PointDto();
        cancelPoint.setUserId(userId);
        cancelPoint.setPointType("USE_CANCEL");
        cancelPoint.setOriginalAmount(totalRestore);
        cancelPoint.setRemainAmount(0);
        cancelPoint.setExpiredDt(
                LocalDateTime.now().plusDays(config.getDefaultExpireDays())
        );
        pointMapper.insertPoint(cancelPoint);
    }
    
    @Transactional
    public void cancelEarn(Long userId, Long earnPointId) {

        PointDto earnPoint = pointMapper.selectPoint(earnPointId);

        if (!POINT_TYPE_EARN.equals(earnPoint.getPointType())) {
            throw new IllegalStateException("적립 포인트만 취소 가능");
        }

        // 일부라도 사용된 경우 취소 불가
        if (earnPoint.getRemainAmount() != earnPoint.getOriginalAmount()) {
            throw new IllegalStateException("이미 사용된 적립 포인트");
        }

        long cancelAmount = earnPoint.getRemainAmount();

        // 적립 포인트 소진 처리
        pointMapper.updateRemainAmount(earnPoint.getPointId(), earnPoint.consume(cancelAmount));

        // 사용자 잔액 차감
        UserPointDto userPointDto = userPointMapper.selectUserPoint(userId);
        userPointDto.decrease(cancelAmount);
        userPointMapper.updateBalanceToUserPoint(userPointDto);

        // 취소 포인트 기록 (EARN_CANCEL)
        PointDto cancelPoint = new PointDto();
        cancelPoint.setUserId(userId);
        cancelPoint.setPointType("EARN_CANCEL");
        cancelPoint.setOriginalAmount(cancelAmount);
        cancelPoint.setRemainAmount(0);
        cancelPoint.setExpiredDt(
                LocalDateTime.now().plusDays(config.getDefaultExpireDays())
        );
        pointMapper.insertPoint(cancelPoint);
    }
}
