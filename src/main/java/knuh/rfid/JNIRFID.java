package knuh.rfid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JNIRFID {	
	public native void test();
	public native boolean ccr_device_find();

	public native boolean ccr_data_transceive_ex(byte a[]);

	static {
		System.out.println("now loading CCR_32...");
		try {
			System.loadLibrary("CCR_32");
		}catch(Exception e) {
			e.printStackTrace();
			log.error("CCR_32.dll 파일 로딩 실패");
			System.exit(0);
		}
//		
		System.out.println("loading has been Finished!");
	}
}