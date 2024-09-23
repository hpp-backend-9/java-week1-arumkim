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
	 * 회원 포인트 조회 성공 케이스
	 * 작성 이유 : 성공 케이스에 대하여 확인
	 */
	@Test
	public void testGetUserPoint() {
		//given
		long userId = 1L;
		UserPoint userPoint = new UserPoint(userId, 5000, 0);
		when(userPointTable.selectById(userId)).thenReturn(userPoint);

		//when
		long points = pointService.getUserPoint(userId);

		//then
		assertEquals(5000, points);
		verify(userPointTable, times(1)).selectById(userId); // selectById가 1번 호출되었는지 확인
	}

	/**
	 * 회원 포인트 조회 실패 케이스 1
	 * 유저 정보가 없는 경우
	 * 이유 : 유저 정보가 없는 경우 유효성 처리를 위한 테스트
	 */

	@Test
	public void fail_getUserPoint() {
		//given
		long userId = 1L;
		when(userPointTable.selectById(userId)).thenReturn(null); // 유저 정보가 없는 경우

		//when
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			pointService.validateUserExists(userId);
		});

		//then
		assertEquals("회원 정보가 존재 하지 않습니다.", exception.getMessage());
		verify(userPointTable, times(1)).selectById(userId); // selectById가 1번 호출되었는지 확인
	}

}