package io.hhplus.tdd.point;

public record UserPoint(
    long id,
    long point,
    long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    // 포인트 충전
    public UserPoint charge(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        return new UserPoint(this.id, this.point + amount, System.currentTimeMillis());
    }

    // 포인트를 사용
    public UserPoint use(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.point < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        return new UserPoint(this.id, this.point - amount, System.currentTimeMillis());
    }
}
