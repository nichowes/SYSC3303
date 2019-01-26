package src.net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Common {
	public static byte[] intToByteArray(int value){
	    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
	}

	public static int byteArrayToInt(byte[] byteArray){
	    return ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
}
