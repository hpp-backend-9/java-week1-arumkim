package io.hhplus.tdd.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;

class PointServiceTest {

	@Mock
	private UserPointTable userPointTable;

	@Mock
	private PointHistoryTable pointHistoryTable;

	@InjectMocks
	private PointService pointService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this); // Mockito 초기화
	}

	/**
	 * 유저 정보가 존재하지 않을 때 포인트 조회 실패 테스트
	 */
	@Test
	public void 포인트조회_유저없음_실패() {
		// given
		long userId = 1L;
		when(userPointTable.selectById(userId)).thenReturn(null);

		// when & then
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			pointService.point(userId);
		});
		assertEquals("유저 정보가 존재 하지 않습니다.", exception.getMessage());
	}

	/**
	 * 포인트 내역이 없을 때 실패 테스트
	 */
	@Test
	public void 포인트내역조회_내역없음_실패() {
		// given
		long userId = 1L;
		when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(null);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			pointService.histories(userId);
		});
		assertEquals("유저 포인트 내역이 존재하지 않습니다.", exception.getMessage());
	}

	/**
	 * 포인트 충전 시 유저 정보가 없을 때 실패 테스트
	 */
	@Test
	public void 포인트충전_유저없음_실패() {
		// given
		long userId = 1L;
		long amount = 1000L;
		when(userPointTable.selectById(userId)).thenReturn(null);

		// when & then
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			pointService.charge(userId, amount);
		});
		assertEquals("유저 정보가 존재 하지 않습니다.", exception.getMessage());
	}

	/**
	 * 포인트 충전 시 잘못된 금액(음수) 입력 시 실패 테스트
	 */
	@Test
	public void 포인트충전_음수금액_실패() {
		// given
		long userId = 1L;
		long amount = -500L;
		UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			pointService.charge(userId, amount);
		});
		assertEquals("충전 금액포인트는는 0보다 커야 합니다.", exception.getMessage());
	}

	/**
	 * 포인트 사용 시 유저 정보가 없을 때 실패 테스트
	 */
	@Test
	public void 포인트사용_유저없음_실패() {
		// given
		long userId = 1L;
		long amount = 500L;
		when(userPointTable.selectById(userId)).thenReturn(null);

		// when & then
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			pointService.usePoint(userId, amount);
		});
		assertEquals("유저 정보가 존재 하지 않습니다.", exception.getMessage());
	}

	/**
	 * 포인트 사용 시 잘못된 금액(음수) 입력 시 실패 테스트
	 */
	@Test
	public void 포인트사용_음수금액_실패() {
		// given
		long userId = 1L;
		long amount = -500L;
		UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			pointService.usePoint(userId, amount);
		});
		assertEquals("사용 포인트는 0보다 커야 합니다.", exception.getMessage());
	}

	/**
	 * 포인트 사용 시 잔액 부족할 때 실패 테스트
	 */
	@Test
	public void 포인트사용_잔액부족_실패() {
		// given
		long userId = 1L;
		long amount = 1500L;
		UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when & then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			pointService.usePoint(userId, amount);
		});
		assertEquals("포인트가 부족합니다.", exception.getMessage());
	}
}