package sdb.core;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 * 
 * @author 강상훈
 *
 */
class RemoteCallRequestPacket {
	final int HEADER_LENGTH = 2;
	final int DUMMY_LENGTH = 1;
	final int PACKET_MIN_LENGTH = 7; // 최소 길이 (group + item + dummy + body length)

	static final byte group = 3;
	final byte item;

	int length; // body length

	byte[] stream;

	public RemoteCallRequestPacket(byte item) {
		this.item = item;

		stream = new byte[PACKET_MIN_LENGTH];
		stream[0] = group; 	// group (1)
		stream[1] = item; 	// item (1)
		stream[2] = 0; 		// dummy (1)
	}

	public byte[] getStream() {
		return stream;
	}

	void addNumber(byte[] rawValue, int rawValueLength) {
		int lastOffset = stream.length;

		int newLength = length + rawValueLength;
		byte[] newStream = new byte[PACKET_MIN_LENGTH + newLength];
		System.arraycopy(stream, 0, newStream, 0, lastOffset);

		int offset = 3; // 최초 시작 위치

		System.arraycopy(NetworkStreamHelper.fromNumber(newLength), 0, newStream, offset, 4); 	// datas length (4)
		offset += 4;

		offset = lastOffset; // 삽입할 위치

		System.arraycopy(rawValue, 0, newStream, offset, rawValueLength); 						// number (n)
		offset += rawValueLength;

		length = newLength;
		stream = newStream;
	}

	public void addBoolean(boolean rawValue) {
		addNumber(NetworkStreamHelper.fromNumber(rawValue), 1); // boolean
	}

	public void addInt(int rawValue) {
		addNumber(NetworkStreamHelper.fromNumber(rawValue), 4); // int
	}

	public void addLong(long rawValue) {
		addNumber(NetworkStreamHelper.fromNumber(rawValue), 8); // long
	}

	public void addDouble(double rawValue) {
		addNumber(NetworkStreamHelper.fromNumber(rawValue), 8); // double
	}

	public void addString(String value) throws SQLException {
		byte[] rawValue = null;
		try {
			rawValue = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
		int rawValueLength = rawValue.length; // 한글: 3bytes, 그외: 1byte

		int lastOffset = stream.length;

		int newLength = length + 4 + (rawValueLength + 1); // 1: NUL
		byte[] newStream = new byte[PACKET_MIN_LENGTH + newLength];
		System.arraycopy(stream, 0, newStream, 0, lastOffset);

		int offset = 3; // 최초 시작 위치

		System.arraycopy(NetworkStreamHelper.fromNumber(newLength), 0, newStream, offset, 4); 		// datas length (4)
		offset += 4;

		offset = lastOffset; // 삽입할 위치

		System.arraycopy(NetworkStreamHelper.fromNumber(rawValueLength), 0, newStream, offset, 4); 	// string length (4)
		offset += 4;

		System.arraycopy(rawValue, 0, newStream, offset, rawValueLength); 							// string (n)
		offset += rawValueLength;

		newStream[offset++] = (byte) '\0'; 															// NUL (1)

		length = newLength;
		stream = newStream;
	}

	public void addArray(byte[] value) {
		int rawValueLength = value.length;

		int lastOffset = stream.length;

		int newLength = length + 4 + rawValueLength;
		byte[] newStream = new byte[PACKET_MIN_LENGTH + newLength];
		System.arraycopy(stream, 0, newStream, 0, lastOffset);

		int offset = 3; // 최초 시작 위치

		System.arraycopy(NetworkStreamHelper.fromNumber(newLength), 0, newStream, offset, 4); 		// datas length (4)
		offset += 4;

		offset = lastOffset; // 삽입할 위치

		System.arraycopy(NetworkStreamHelper.fromNumber(value.length), 0, newStream, offset, 4);	// array column count (4)
		offset += 4;

		System.arraycopy(value, 0, newStream, offset, rawValueLength); 								// array (n)
		offset += rawValueLength;

		length = newLength;
		stream = newStream;
	}
}