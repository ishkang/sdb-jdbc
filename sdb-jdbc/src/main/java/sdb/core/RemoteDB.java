package sdb.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import sdb.BusyHandler;
import sdb.Function;
import sdb.ProgressHandler;

/**
 * 
 * @author 강상훈
 *
 */
final class RemoteDB extends DB {
	private final String host;
	private final int port;
	private final String userId;
	private final String userPassword;
	
	private Socket socket;
	private BufferedOutputStream outStream;
	private BufferedInputStream inStream;
	private String clientInfo;

	public RemoteDB(String host, int port, String userId, String userPassword) throws SQLException {
		this.host = host;
		this.port = port;
		this.userId = userId;
		this.userPassword = userPassword;

		// socket = connect(host, port);
		// test_2차프로토타입();
	}

	private Socket __connect(String host, int port) throws SQLException {
		if(socket != null)
			throw new SQLException("이미 연결되어 있습니다.");
		
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			outStream = new BufferedOutputStream(socket.getOutputStream());
			inStream = new BufferedInputStream(socket.getInputStream());	
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}

		return socket;
	}

	private void writeRequest(RemoteCallRequestPacket packet) throws SQLException {
		try {
			outStream.write(packet.getStream());
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e);
		}
	}

	private int readResponseDatasLength() throws SQLException {
		// 응답
		byte[] response = null;

		// 응답 헤더
		byte responseResult = 0;
		{
			int packetLength = 2;
			response = new byte[packetLength];

			int bytesRec = 0;
			try {
				bytesRec = inStream.read(response, 0, packetLength);
			} catch (IOException e) {
				e.printStackTrace();
				throw new SQLException(e);
			}
			if (bytesRec != packetLength)
				throw new SQLException("응답헤더 읽기 실패");

			responseResult = response[1];

			response = null;
		}

		if (responseResult != 1) {
			// 서버측 오류

			// error code
			int errorCode = 0;
			if ((responseResult & 0xff) == 204) {
				int packetLength = 4;
				response = new byte[packetLength];

				int bytesRec = 0;
				try {
					bytesRec = inStream.read(response, 0, packetLength);
				} catch (IOException e) {
					e.printStackTrace();
					throw new SQLException(e);
				}
				if (bytesRec != packetLength)
					throw new SQLException("오류코드 읽기 실패");

				errorCode = NetworkStreamHelper.toInt(response, 0);

				response = null;
			}

			// error message
			String errorMessage = null;
			{
				int messageLength = 128;
				response = new byte[messageLength];

				int bytesRec = 0;
				try {
					bytesRec = inStream.read(response, 0, messageLength);
				} catch (IOException e) {
					e.printStackTrace();
					throw new SQLException(e);
				}
				if (bytesRec != messageLength)
					throw new SQLException("오류메세지 읽기 실패");

				if (response[0] != '\0') {
					int offset = 0;
					if (response[messageLength - 1] == '\0') {
						for (; offset < messageLength; offset++) {
							if (response[offset] == '\0')
								break;
						}
					} else {
						offset = messageLength;
					}

					try {
						errorMessage = new String(response, 0, offset, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
				}

				response = null;
			}

			throw new SQLException(String.format("code: %d, message: %s", errorCode, errorMessage));
		}

		// 응답 바디
		int datasLength;
		{
			// packet length: 4 bytes
			datasLength = 0;
			{
				int packetLength = 4;
				response = new byte[packetLength];

				int bytesRec = 0;
				try {
					bytesRec = inStream.read(response, 0, packetLength);
				} catch (IOException e) {
					e.printStackTrace();
					throw new SQLException(e);
				}
				if (bytesRec != packetLength)
					throw new SQLException("응답바디크기 읽기 실패");

				datasLength = NetworkStreamHelper.toInt(response, 0);
				if (datasLength <= 0)
					throw new SQLException("응답바디크기 이상");

				response = null;
			}
		}

		return datasLength;
	}

	private byte[] readResponse() throws SQLException {
		byte[] response = null;

		// 응답 바디
		{
			// 바디 길이
			int datasLength = readResponseDatasLength();

			// 바디 내용
			{
				response = new byte[datasLength];

				int bytesRec = 0;
				try {
					bytesRec = inStream.read(response, 0, datasLength);
				} catch (IOException e) {
					e.printStackTrace();
					throw new SQLException(e);
				}
				if (bytesRec != datasLength)
					throw new SQLException("응답바디 읽기 실패");
			}
		}

		return response;
	}

	/**
	 * 테스트할 원격 메소드: #45 string GetName(int type, string text): type이 1이면 ("강상훈: " +
	 * text), 2이면 ("홍길동: " + text) 반환
	 * 
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private void test_2차프로토타입() throws SQLException {
		// 테스트 데이터 구성
		int type = 1;
		String text = "1 a 결과";
		String 예상결과 = "강상훈: " + text;

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.TEST); // GetName
		packet.addInt(type);
		packet.addString(text);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("test_2차프로토타입(): %s", returnValue));
		}
	}

	@Override
	public String getClientInfo() {
		return clientInfo;
	}

	private void applyClientInfo(String clientInfo) throws SQLException {		
		if (clientInfo == null)
			return;

		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");		
		
		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.CLIENT_INFO); // 46
		packet.addString(clientInfo);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("applyClientInfo(%s): void", clientInfo);
		}
	}
	
	@Override
	protected void setClientInfo(String clientInfo) throws SQLException {
		this.clientInfo = clientInfo;
		
		if (socket != null && socket.isConnected())
			applyClientInfo(clientInfo);
	}
	
	@Override
	public synchronized void interrupt() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.INTERRUPT); // 1
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("interrupt(): void");
		}
	}

	@Override
	public synchronized void busy_timeout(int ms) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BUSY_TIMEOUT); // 2
		packet.addInt(ms);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println(String.format("busy_timeout(%d): void", ms));
		}
	}

	@Override
	public synchronized void busy_handler(BusyHandler busyHandler) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: busy_handler");
	}

	@Override
	synchronized String errmsg() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.ERRMSG); // 3
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("errmsg(): %s", returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized String libversion() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.LIBVERSION); // 4
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("libversion(): %s", returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int changes() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.CHANGES); // 5
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("changes(): %d", returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int total_changes() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.TOTAL_CHANGES); // 6
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("total_changes(): %d", returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int shared_cache(boolean enable) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.SHARED_CACHE); // 7
		packet.addBoolean(enable);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("shared_cache(%s): %d", enable, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int enable_load_extension(boolean enable) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.ENABLE_LOAD_EXTENSION); // 8
		packet.addBoolean(enable);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("enable_load_extension(%s): %d", enable, returnValue));
		}
		return returnValue;
	}

	@Override
	protected synchronized void _open(String filename, int openFlags) throws SQLException {
		if (socket == null || !socket.isConnected())
			socket = __connect(host, port);

		applyClientInfo(clientInfo);
		
		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes._OPEN); // 9
		packet.addString(filename);
		packet.addInt(openFlags);
		packet.addString(userId == null ? "" : userId);
		packet.addSHA256String(userPassword == null ? "" : userPassword);
		packet.addString(clientInfo == null ? "" : clientInfo);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println(String.format("_open(%s, %d): void", filename, openFlags));
		}
		
		System.out.println(String.format("connected: {host: %s, port: %d, user: %s, password: %d}", host, port, userId, userPassword.charAt(0) + 1));
	}

	@Override
	protected synchronized void _close() throws SQLException {
		if (!(socket == null || !socket.isConnected())) {
			// 요청
			RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes._CLOSE); // 10
			writeRequest(packet);

			// 응답
			byte[] response = readResponse();

			// 처리
			{
				// void: 2 bytes
				if (response.length != 2)
					throw new SQLException("리턴값 이상");

				//System.out.println("_close(): void");
			}
		}

		if (socket != null)
			__close();
	}

	private void __close() throws SQLException {
		try {
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				socket.shutdownInput();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SQLException(e);
			} finally {
				try {
					outStream.close();
					inStream.close();
					socket.close();
					System.out.println(String.format("disconnected: {host: %s, port: %d, user: %s}", host, port, userId));
				} catch (IOException e) {
					e.printStackTrace();
					throw new SQLException(e);
				}
				socket = null;
				inStream = null;
				outStream = null;
			}
		}
	}

	@Override
	public synchronized int _exec(String sql) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes._EXEC); // 11
		packet.addString(sql);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("_exec(%s): %d", sql, returnValue));
		}
		return returnValue;
	}

	@Override
	protected synchronized long prepare(String sql) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.PREPARE); // 12
		packet.addString(sql);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		long returnValue = 0;
		{
			// long: 8 bytes
			if (response.length != 8)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toLong(response, 0); // long (8)

			//System.out.println(String.format("prepare(%s): %d", sql, returnValue));
		}
		return returnValue;
	}

	@Override
	protected synchronized int finalize(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.FINALIZE); // 13
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("finalize(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int step(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.STEP); // 14
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("step(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int reset(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESET); // 15
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("reset(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int clear_bindings(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.CLEAR_BINDINGS); // 16
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("clear_bindings(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_parameter_count(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_PARAMETER_COUNT); // 17
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_parameter_count(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int column_count(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_COUNT); // 18
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("column_count(%d): %d", stmt, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int column_type(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_TYPE); // 19
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("column_type(%d, %d): %d", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized String column_decltype(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_DECLTYPE); // 20
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("column_decltype(%d, %d): %s", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized String column_table_name(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_TABLE_NAME); // 21
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("column_table_name(%d, %d): %s", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized String column_name(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_NAME); // 22
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("column_name(%d, %d): %s", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized String column_text(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_TEXT); // 23
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		String returnValue = null;
		{
			int offset = 0;

			// String: 4 + (n + 1) bytes
			{
				if (response.length < 4)
					throw new SQLException("리턴값 이상");

				int stringLength = NetworkStreamHelper.toInt(response, offset); // String length (4)
				offset += 4;

				if (stringLength > 0) {
					try {
						returnValue = new String(response, offset, stringLength, "UTF-8"); // String (n)
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					offset += stringLength;
					offset++; // NUL (1)
				}
			}

			//System.out.println(String.format("column_text(%d, %d): %s", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized byte[] column_blob(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_BLOB); // 24
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답 & 처리
		int arrayColumnCount;
		byte[] array;
		{
			// 응답 바디
			{
				// 바디 길이
				int datasLength = readResponseDatasLength();		

				// Array: 4 + (array column count) bytes
				if (datasLength < 4) // 4
					throw new SQLException("리턴값 이상");
				
				// 배열 길이
				// packet length: 4 bytes
				arrayColumnCount = 0;
				{
					int packetLength = 4;
					byte[] response = new byte[packetLength];

					int bytesRec = 0;
					try {
						bytesRec = inStream.read(response, 0, packetLength);
					} catch (IOException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					if (bytesRec != packetLength)
						throw new SQLException("배열크기 읽기 실패");

					arrayColumnCount = NetworkStreamHelper.toInt(response, 0);

					response = null;
				}
				
				// 배열 내용
				if (arrayColumnCount > 0)
				{
					array = new byte[arrayColumnCount];
		
					int bytesRec = 0;
					try {
						bytesRec = inStream.read(array, 0, arrayColumnCount);
					} catch (IOException e) {
						e.printStackTrace();
						throw new SQLException(e);
					}
					if (bytesRec != arrayColumnCount)
						throw new SQLException("배열 읽기 실패");
				}
				else {
					array = null;
				}
					
			}
		}		
		
		// 처리
		byte[] returnValue = array;			
		
		//System.out.println("column_blob(%d, %d): array", stmt, col);
		return returnValue;
	}

	@Override
	public synchronized double column_double(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_DOUBLE); // 25
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		double returnValue = 0;
		{
			// double: 8 bytes
			if (response.length != 8)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toDouble(response, 0); // double (8)

			//System.out.println(String.format("column_double(%d, %d): %f", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized long column_long(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_LONG); // 26
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		long returnValue = 0;
		{
			// long: 8 bytes
			if (response.length != 8)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toLong(response, 0); // long (8)

			//System.out.println(String.format("column_long(%d, %d): %d", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized int column_int(long stmt, int col) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_INT); // 27
		packet.addLong(stmt);
		packet.addInt(col);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("column_int(%d, %d): %d", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_null(long stmt, int pos) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_NULL); // 28
		packet.addLong(stmt);
		packet.addInt(pos);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_null(%d, %d): %d", stmt, col, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_int(long stmt, int pos, int v) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_INT); // 29
		packet.addLong(stmt);
		packet.addInt(pos);
		packet.addInt(v);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_int(%d, %d, %d): %d", stmt, pos, v, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_long(long stmt, int pos, long v) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_LONG); // 30
		packet.addLong(stmt);
		packet.addInt(pos);
		packet.addLong(v);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_long(%d, %d, %d): %d", stmt, pos, v, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_double(long stmt, int pos, double v) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_DOUBLE); // 31
		packet.addLong(stmt);
		packet.addInt(pos);
		packet.addDouble(v);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_double(%d, %d, %f): %d", stmt, pos, v, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_text(long stmt, int pos, String v) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_TEXT); // 32
		packet.addLong(stmt);
		packet.addInt(pos);
		packet.addString(v);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_text(%d, %d, %s): %d", stmt, pos, v, returnValue));
		}
		return returnValue;
	}

	@Override
	synchronized int bind_blob(long stmt, int pos, byte[] v) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.BIND_BLOB); // 33
		packet.addLong(stmt);
		packet.addInt(pos);
		packet.addArray(v);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		int returnValue = 0;
		{
			// int: 4 bytes
			if (response.length != 4)
				throw new SQLException("리턴값 이상");

			returnValue = NetworkStreamHelper.toInt(response, 0); // int (4)

			//System.out.println(String.format("bind_blob(%s, %d, array): %d", stmt, pos, returnValue));
		}
		return returnValue;
	}

	@Override
	public synchronized void result_null(long context) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	
		
		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_NULL); // 34
		packet.addLong(context);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_null(%d): void", context);
		}
	}

	@Override
	public synchronized void result_text(long context, String val) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_TEXT); // 35
		packet.addLong(context);
		packet.addString(val);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_text(%d, %s): void", context, val);
		}
	}

	@Override
	public synchronized void result_blob(long context, byte[] val) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_BLOB); // 36
		packet.addLong(context);
		packet.addArray(val);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_blob(%d, array): void", context);
		}
	}

	@Override
	public synchronized void result_double(long context, double val) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_DOUBLE); // 37
		packet.addLong(context);
		packet.addDouble(val);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_double(%d, %f): void", context, val);
		}
	}

	@Override
	public synchronized void result_long(long context, long val) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_LONG); // 38
		packet.addLong(context);
		packet.addLong(val);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_long(%d, %d): void", context, val);
		}
	}

	@Override
	public synchronized void result_int(long context, int val) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_INT); // 39
		packet.addLong(context);
		packet.addInt(val);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_int(%d, %d): void", context, val);
		}
	}

	@Override
	public synchronized void result_error(long context, String err) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		if (context == 0)
			return;	

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.RESULT_ERROR); // 40
		packet.addLong(context);
		packet.addString(err);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("result_error(%d, %s): void", context, err);
		}
	}

	@Override
	public synchronized String value_text(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_text");
		//return null;
	}

	@Override
	public synchronized byte[] value_blob(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_blob");
		//return null;
	}

	@Override
	public synchronized double value_double(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_double");
		//return 0;
	}

	@Override
	public synchronized long value_long(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_long");
		//return 0;
	}

	@Override
	public synchronized int value_int(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_int");
		//return 0;
	}

	@Override
	public synchronized int value_type(Function f, int arg) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: value_type");
		//return 0;
	}

	@Override
	public synchronized int create_function(String name, Function f) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: create_function");
		//return 0;
	}

	@Override
	public synchronized int destroy_function(String name) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: create_function");
	}

	@Override
	synchronized void free_functions() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		/*// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.FREE_FUNCTIONS); // 42
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		{
			// void: 2 bytes
			if (response.length != 2)
				throw new SQLException("리턴값 이상");
			
			//System.out.println("free_functions(): void");
		}*/
	}

	@Override
	public synchronized int backup(String dbName, String destFileName, ProgressObserver observer) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: backup");
		//return 0;
	}

	@Override
	public synchronized int restore(String dbName, String sourceFileName, ProgressObserver observer) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: restore");
		//return 0;
	}

	@Override
	public synchronized void register_progress_handler(int vmCalls, ProgressHandler progressHandler) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: register_progress_handler");
	}

	@Override
	public synchronized void clear_progress_handler() throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		throw new SQLException("구현되지 않았습니다.: clear_progress_handler");
	}

	@Override
	synchronized boolean[][] column_metadata(long stmt) throws SQLException {
		if (socket == null || !socket.isConnected())
			throw new SQLException("연결되어 있지 않습니다.");

		// 요청
		RemoteCallRequestPacket packet = new RemoteCallRequestPacket(RemoteMethodCodes.COLUMN_METADATA); // 44
		packet.addLong(stmt);
		writeRequest(packet);

		// 응답
		byte[] response = readResponse();

		// 처리
		boolean[][] returnValue;
		{
			int offset = 0;
			
			// Array: 4 + 4 + (array row count * array column count) bytes
			{
				if (response.length < 8) // 4 + 4
					throw new SQLException("리턴값 이상");

				int arrayRowCount = NetworkStreamHelper.toInt(response, offset); // int (4)
				offset += 4;

				int arrayColumnCount = NetworkStreamHelper.toInt(response, offset); // int (4)
				offset += 4;
				
				if (arrayRowCount * arrayColumnCount == 0) {
					if (arrayRowCount != arrayColumnCount)
						throw new SQLException("리턴값 이상");
					
					returnValue = null;
				}
				else {
					returnValue = new boolean[arrayRowCount][];

					for (int row = 0; row < arrayRowCount; row++) {
						returnValue[row] = new boolean[arrayColumnCount];
						for (int col = 0; col < arrayColumnCount; col++) {
							returnValue[row][col] = response[offset] == 1;
							offset++;
						}
					}
				}
			}
			
			//System.out.println("column_metadata(%d): array", stmt);
		}
		return returnValue;
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if (socket != null)
			__close();

		super.finalize();
	}

}
