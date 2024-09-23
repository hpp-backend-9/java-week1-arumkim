package io.hhplus.tdd.point.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;

/**
 * The type Point service.
 */
@Service
@RequiredArgsConstructor
public class PointService {

	private final UserPointTable userPointTable;
	private final PointHistoryTable pointHistoryTable;

	/**
	 * 특정 유저 포인트 조회
	 *
	 * @param id the id
	 * @return the user point
	 */
	public long getUserPoint(long id) {
		Optional<UserPoint> userPoint = Optional.ofNullable(userPointTable.selectById(id));

		if (userPoint.isPresent()) {
			UserPoint point = userPoint.get();

			return point.point();
		} else {
			throw new RuntimeException("회원 정보가 존재 하지 않습니다.");
		}
	}

	public void validateUserExists(long userId) {
		UserPoint userPoint = userPointTable.selectById(userId);

		if (ObjectUtils.isEmpty(userPoint)) {
			throw new RuntimeException("회원 정보가 존재 하지 않습니다.");
		}
	}
}
