package sdb.core;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import sdb.core.DB.ProgressObserver;

import sdb.BusyHandler;
import sdb.Function;
import sdb.ProgressHandler;

final class RemoteDB extends DB {

	@Override
	public synchronized void interrupt() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void busy_timeout(int ms) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void busy_handler(BusyHandler busyHandler) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	synchronized String errmsg() throws SQLException {
		return utf8ByteArrayToString(errmsg_utf8());
	}

	synchronized byte[] errmsg_utf8() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String libversion() throws SQLException {
		return utf8ByteArrayToString(libversion_utf8());
	}

	synchronized byte[] libversion_utf8() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized int changes() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int total_changes() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int shared_cache(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int enable_load_extension(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected synchronized void _open(String filename, int openFlags) throws SQLException {
		_open_utf8(stringToUtf8ByteArray(filename), openFlags);
	}

	synchronized void _open_utf8(byte[] fileUtf8, int openFlags) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	protected synchronized void _close() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized int _exec(String sql) throws SQLException {
		return _exec_utf8(stringToUtf8ByteArray(sql));
	}

	synchronized int _exec_utf8(byte[] sqlUtf8) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected synchronized long prepare(String sql) throws SQLException {
		return prepare_utf8(stringToUtf8ByteArray(sql));
	}

	synchronized long prepare_utf8(byte[] sqlUtf8) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected synchronized int finalize(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int step(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int reset(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int clear_bindings(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_parameter_count(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int column_count(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int column_type(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized String column_decltype(long stmt, int col) throws SQLException {
		return utf8ByteArrayToString(column_decltype_utf8(stmt, col));
	}

	synchronized byte[] column_decltype_utf8(long stmt, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String column_table_name(long stmt, int col) throws SQLException {
		return utf8ByteArrayToString(column_table_name_utf8(stmt, col));
	}

	synchronized byte[] column_table_name_utf8(long stmt, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String column_name(long stmt, int col) throws SQLException {
		return utf8ByteArrayToString(column_name_utf8(stmt, col));
	}

	synchronized byte[] column_name_utf8(long stmt, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String column_text(long stmt, int col) throws SQLException {
		return utf8ByteArrayToString(column_text_utf8(stmt, col));
	}

	synchronized byte[] column_text_utf8(long stmt, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized byte[] column_blob(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized double column_double(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized long column_long(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int column_int(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_null(long stmt, int pos) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_int(long stmt, int pos, int v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_long(long stmt, int pos, long v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_double(long stmt, int pos, double v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_text(long stmt, int pos, String v) throws SQLException {
		return bind_text_utf8(stmt, pos, stringToUtf8ByteArray(v));
	}

	synchronized int bind_text_utf8(long stmt, int pos, byte[] vUtf8) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized int bind_blob(long stmt, int pos, byte[] v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized void result_null(long context) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_text(long context, String val) throws SQLException {
		result_text_utf8(context, stringToUtf8ByteArray(val));
	}

	synchronized void result_text_utf8(long context, byte[] valUtf8) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_blob(long context, byte[] val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_double(long context, double val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_long(long context, long val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_int(long context, int val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void result_error(long context, String err) throws SQLException {
		result_error_utf8(context, stringToUtf8ByteArray(err));
	}

	synchronized void result_error_utf8(long context, byte[] errUtf8) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized String value_text(Function f, int arg) throws SQLException {
		return utf8ByteArrayToString(value_text_utf8(f, arg));
	}

	synchronized byte[] value_text_utf8(Function f, int argUtf8) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized byte[] value_blob(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized double value_double(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized long value_long(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int value_int(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int value_type(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int create_function(String name, Function f) throws SQLException {
		return create_function_utf8(stringToUtf8ByteArray(name), f);
	}

	synchronized int create_function_utf8(byte[] nameUtf8, Function func) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int destroy_function(String name) throws SQLException {
		return destroy_function_utf8(stringToUtf8ByteArray(name));
	}

	synchronized int destroy_function_utf8(byte[] nameUtf8) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	synchronized void free_functions() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized int backup(String dbName, String destFileName, ProgressObserver observer) throws SQLException {
		return backup(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(destFileName), observer);
	}

	synchronized int backup(byte[] dbNameUtf8, byte[] destFileNameUtf8, ProgressObserver observer) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int restore(String dbName, String sourceFileName, ProgressObserver observer)
			throws SQLException {

		return restore(stringToUtf8ByteArray(dbName), stringToUtf8ByteArray(sourceFileName), observer);
	}

	synchronized int restore(byte[] dbNameUtf8, byte[] sourceFileName, ProgressObserver observer) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized void register_progress_handler(int vmCalls, ProgressHandler progressHandler)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void clear_progress_handler() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	synchronized boolean[][] column_metadata(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	static void throwex(String msg) throws SQLException {
		throw new SQLException(msg);
	}

	static byte[] stringToUtf8ByteArray(String str) {
		if (str == null) {
			return null;
		}
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is not supported", e);
		}
	}

	static String utf8ByteArrayToString(byte[] utf8bytes) {
		if (utf8bytes == null) {
			return null;
		}
		try {
			return new String(utf8bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is not supported", e);
		}
	}

}
