package kr.co.jsol.jagency.common.infrastructure.exception.entity;

import kr.co.jsol.jagency.common.infrastructure.exception.CustomException;
import org.springframework.http.HttpStatus;

public abstract class MifareClassic1KException {
    public static class NotReadableTrailBlock extends CustomException {
        public NotReadableTrailBlock() {
            this(null);
        }

        public NotReadableTrailBlock(Throwable e) {
            super("MC1K-0010", "읽을 수 없는 Block 입니다.", HttpStatus.BAD_REQUEST, e);
        }
    }
}
