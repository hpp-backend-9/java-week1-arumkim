package io.hhplus.tdd.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
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
	 * 포인트 내역 조회 성공 케이스
	 */
	@Test
	public void 포인트조회_성공() {
		// given
		long userId = 1L;
		long point = 1000L;
		UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when
		long retrievedPoint = pointService.point(userId);

		// then
		verify(userPointTable).selectById(userId);
		assertEquals(point, retrievedPoint);
	}

	/**
	 * 포인트 조회 시 유저 정보가 없을때 실패 케이스
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
	 * 포인트 내역 조회 성공 케이스
	 */
	@Test
	public void 포인트내역조회_성공() {
		// given
		long userId = 1L;
		List<PointHistory> histories = List.of(
			new PointHistory(1L, userId, 500L, TransactionType.CHARGE, System.currentTimeMillis()),
			new PointHistory(2L, userId, 200L, TransactionType.USE, System.currentTimeMillis())
		);

		when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(histories);

		// when
		List<PointHistory> resultHistories = pointService.histories(userId);

		// then
		verify(pointHistoryTable).selectAllByUserId(userId);
		assertEquals(histories.size(), resultHistories.size());
		assertEquals(histories.get(0).amount(), resultHistories.get(0).amount());
		assertEquals(histories.get(1).amount(), resultHistories.get(1).amount());
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
	 * 포인트 충전 성공 케이스
	 */
	@Test
	public void 포인트충전_성공() {
		// given
		long userId = 1L;
		long point = 500L;
		long amount = 300L;
		UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when
		UserPoint updatedUserPoint = pointService.charge(userId, amount);

		// then
		verify(userPointTable).selectById(userId);
		verify(pointHistoryTable).insert(eq(userId), eq(point + amount), eq(TransactionType.CHARGE), anyLong());
		assertEquals(point + amount, updatedUserPoint.point());
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
		assertEquals("충전 포인트 0보다 커야 합니다.", exception.getMessage());
	}

	/**
	 * 포인트 사용 성공 테스트
	 */
	@Test
	public void 포인트사용_성공() {
		// given
		long userId = 1L;
		long point = 500L;  // 유저 초기 잔고 설정
		long amount = 200L;     // 사용할 포인트 금액 설정
		UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

		// Mocking - 유저가 정상적으로 조회됨
		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		// when
		UserPoint updatedUserPoint = pointService.usePoint(userId, amount);

		// then
		verify(userPointTable).selectById(userId);
		verify(pointHistoryTable).insert(eq(userId), eq(point - amount), eq(TransactionType.USE), anyLong());

		// 실제 잔고에서 예상대로 포인트가 차감되는지 확인
		assertEquals(point - amount, updatedUserPoint.point());
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