package knuh.rfid;

public class JNIRFID {	
	public native void test();
	public native boolean ccr_device_find();

	public native boolean ccr_data_transceive_ex(byte a[]);

	static {
		System.out.println("now loading CCR_32...");
		try {
			System.loadLibrary("CCR_32");
//			Runtime.getRuntime().load("d:/git/MyRepo/javaDirectory_32bit/rfid/CCR_32.dll");
		}catch(Exception e) {
			e.printStackTrace();
		}
//		
		System.out.println("loading has been Finished!");
	}
}