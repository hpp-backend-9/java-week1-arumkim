package io.hhplus.tdd.point.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
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
	 * Point long.
	 *
	 * @param userId the user id
	 * @return the long
	 */
	public long point(long userId) {
		UserPoint userPoint = userPointTable.selectById(userId);

		if (ObjectUtils.isEmpty(userPoint)) {
			throw new NullPointerException("유저 정보가 존재 하지 않습니다.");
		}

		return userPoint.point();
	}

	/**
	 * Histories list.
	 *
	 * @param userId the user id
	 * @return the list
	 */
	public List<PointHistory> histories(long userId) {
		List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
		if (ObjectUtils.isEmpty(histories)) {
			throw new IllegalArgumentException("유저 포인트 내역이 존재하지 않습니다.");
		}

		return histories;
	}

	/**
	 * 특정 유저의 포인트를 충전하는 기능
	 * @param userId the user id
	 * @param amount the amount
	 * @return user point
	 */
	public UserPoint charge(long userId, long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
		}

		UserPoint point = userPointTable.selectById(userId);
		if (ObjectUtils.isEmpty(point)) {
			throw new NullPointerException("유저 정보가 존재 하지 않습니다.");
		}

		// 포인트 충전
		point = userPointTable.insertOrUpdate(userId, point.point() + amount);

		// 포인트 충전 내역 저장
		pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

		return point;
	}

	/**
	 * 특정 유저의 포인트를 사용하는 기능
	 * @param userId the userId
	 * @param amount the amount
	 * @return user point
	 */
	public UserPoint usePoint(long userId, long amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
		}

		UserPoint point = userPointTable.selectById(userId);
		if (ObjectUtils.isEmpty(point)) {
			throw new NullPointerException("유저 정보가 존재 하지 않습니다.");
		}

		if (point.point() < amount) {
			throw new IllegalArgumentException("잔여 포인트가 부족합니다.");
		}

		// 포인트 차감
		point = userPointTable.insertOrUpdate(userId, point.point() - amount);

		// 포인트 충전 내역 저장
		pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

		return point;
	}
}
