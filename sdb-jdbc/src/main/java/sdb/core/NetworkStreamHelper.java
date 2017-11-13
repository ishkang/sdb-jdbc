package sdb.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;

/**
 * Endian: Little Endian
 * 
 * @author 강상훈
 *
 */
class NetworkStreamHelper {

	public static byte[] fromNumber(boolean value) {
		return new byte[] { (byte) (value ? 1 : 0) };
	}

	public static byte[] fromNumber(int value) {
		byte[] bytes = new byte[] { (byte) (value & 0xff), (byte) ((value >> 8) & 0xff), (byte) ((value >> 16) & 0xff), (byte) ((value >> 24) & 0xff) };
		/*int value2 = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		if (value != value2)
			System.err.println(String.format("변환 오류: %d, %d", value, value2));*/
		return bytes;
	}

	public static byte[] fromNumber(long value) {
		byte[] bytes = new byte[] { (byte) (value & 0xff), (byte) ((value >> 8) & 0xff), (byte) ((value >> 16) & 0xff), (byte) ((value >> 24) & 0xff), (byte) ((value >> 32) & 0xff), (byte) ((value >> 40) & 0xff), (byte) ((value >> 48) & 0xff), (byte) ((value >> 56) & 0xff) };
		/*long value2 = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
		if (value != value2)
			System.err.println(String.format("변환 오류: %d, %d", value, value2));*/
		return bytes;
	}

	public static byte[] fromNumber(double value) {
		long l = Double.doubleToRawLongBits(value);
		return fromNumber(l);
	}

	public static int toInt(byte bytes[], int offset) {
		int value = ((((int) bytes[offset + 0]) & 0xff) | (((int) (bytes[offset + 1]) & 0xff) << 8) | (((int) (bytes[offset + 2]) & 0xff) << 16) | (((int) (bytes[offset + 3]) & 0xff) << 24));
		/*int value2 = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
		if (value != value2)
			System.err.println(String.format("변환 오류: %d, %d", value, value2));*/
		return value;
	}

	public static long toLong(byte bytes[], int offset) {
		long value = ((((long) bytes[offset + 0]) & 0xff) | (((long) (bytes[offset + 1]) & 0xff) << 8) | (((long) (bytes[offset + 2]) & 0xff) << 16) | (((long) (bytes[offset + 3]) & 0xff) << 24) | (((long) (bytes[offset + 4]) & 0xff) << 32) | (((long) (bytes[offset + 5]) & 0xff) << 40) | (((long) (bytes[offset + 6]) & 0xff) << 48) | (((long) (bytes[offset + 7]) & 0xff) << 56));
		/*long value2 = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
		if (value != value2)
			System.err.println(String.format("변환 오류: %d, %d", value, value2));*/
		return value;
	}

	public static double toDouble(byte bytes[], int offset) {
		long l = toLong(bytes, offset);
		return Double.longBitsToDouble(l);
	}

}
