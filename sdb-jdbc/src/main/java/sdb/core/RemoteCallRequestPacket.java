package sdb.core;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Base64;

import sdb.util.KISA_SHA256;

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

	public void addSHA256String(String value) throws SQLException {		
		byte[] plainStream = null;
		try {
			plainStream = value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		byte[] encryptedStream = new byte[32]; // 32 ?
		KISA_SHA256.SHA256_Encrpyt(plainStream, plainStream.length, encryptedStream);
		
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < encryptedStream.length; i++) {
			hexString.append(Integer.toHexString(0xFF & encryptedStream[i]));
		}
		addString(hexString.toString());
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