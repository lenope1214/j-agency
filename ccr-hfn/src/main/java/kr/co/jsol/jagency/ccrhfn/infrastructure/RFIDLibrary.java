package kr.co.jsol.jagency.ccrhfn.infrastructure;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface RFIDLibrary extends Library {
    // 플랫폼이 윈도우면 msvcrt를, 아니면 ...
    // 싱글톤 관리
    RFIDLibrary INSTANCE = (RFIDLibrary) Native.load((Platform.isWindows() ? "CCR_32" : "elseDLLFileName"), RFIDLibrary.class);

    // USB포트 연결된 DEVICE가 있는지 검사
    public boolean ccr_device_find();

    // 송신 프로토콜 ASCII로 변환된 HEX코드 38바이트 ( HEX코드로는 19바이트 ), 수신 프로토콜 포인터.
    public void ccr_data_transceive_ex(byte[] in, byte[] out);

    public void ccr_data_transceive_ex(String in, byte[] out);

    public boolean ccr_data_transceive_ex(String[] in, byte[] out);
//    public void ccr_data_transceive_ex(String[] in, byte[] out);

}
