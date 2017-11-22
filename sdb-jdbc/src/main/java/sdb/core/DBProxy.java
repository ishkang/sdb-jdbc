package sdb.core;

import java.sql.SQLException;

import sdb.BusyHandler;
import sdb.Function;
import sdb.ProgressHandler;

/**
 * 
 * @author 강상훈
 *
 */
public final class DBProxy extends DB {
	final DB db;

	public DBProxy(String host, int port, String userId, String userPassword) throws SQLException {
		if (host.isEmpty() || port == 0) {
			throw new SQLException("연결할 대상 서버가 지정되지 않았습니다.");
			// db = new NativeDB();
		} else {
			db = new RemoteDB(host, port, userId, userPassword);
		}
	}

	@Override
	public String getClientInfo() {
		return db.getClientInfo();
	}

	@Override
	protected void setClientInfo(String clientInfo) throws SQLException {
		db.setClientInfo(clientInfo);
	}
	
	@Override
	public void interrupt() throws SQLException {
		// System.out.println("@interrupt");
		db.interrupt();
	}

	@Override
	public void busy_timeout(int ms) throws SQLException {
		// System.out.println("@busy_timeout");
		db.busy_timeout(ms);
	}

	@Override
	public void busy_handler(BusyHandler busyHandler) throws SQLException {
		// System.out.println("@busy_handler");
		db.busy_handler(busyHandler);
	}

	@Override
	String errmsg() throws SQLException {
		// System.out.println("@errmsg");
		return db.errmsg();
	}

	@Override
	public String libversion() throws SQLException {
		// System.out.println("@libversion");
		return db.libversion();
	}

	@Override
	public int changes() throws SQLException {
		// System.out.println("@changes");
		return db.changes();
	}

	@Override
	public int total_changes() throws SQLException {
		// System.out.println("@total_changes");
		return db.total_changes();
	}

	@Override
	public int shared_cache(boolean enable) throws SQLException {
		// System.out.println("@shared_cache");
		return db.shared_cache(enable);
	}

	@Override
	public int enable_load_extension(boolean enable) throws SQLException {
		// System.out.println("@enable_load_extension");
		return db.enable_load_extension(enable);
	}

	@Override
	protected void _open(String filename, int openFlags) throws SQLException {
		// System.out.println("@_open");
		db._open(filename, openFlags);
	}

	@Override
	protected void _close() throws SQLException {
		// System.out.println("@_close");
		db._close();
	}

	@Override
	public int _exec(String sql) throws SQLException {
		// System.out.println("@_exec");
		return db._exec(sql);
	}

	@Override
	protected long prepare(String sql) throws SQLException {
		// System.out.println("@prepare");
		return db.prepare(sql);
	}

	@Override
	protected int finalize(long stmt) throws SQLException {
		// System.out.println("@finalize");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.finalize(stmt);
	}

	@Override
	public int step(long stmt) throws SQLException {
		// System.out.println("@step");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.step(stmt);
	}

	@Override
	public int reset(long stmt) throws SQLException {
		// System.out.println("@reset");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.reset(stmt);
	}

	@Override
	public int clear_bindings(long stmt) throws SQLException {
		// System.out.println("@clear_bindings");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.clear_bindings(stmt);
	}

	@Override
	int bind_parameter_count(long stmt) throws SQLException {
		// System.out.println("@bind_parameter_count");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_parameter_count(stmt);
	}

	@Override
	public int column_count(long stmt) throws SQLException {
		// System.out.println("@column_count");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_count(stmt);
	}

	@Override
	public int column_type(long stmt, int col) throws SQLException {
		// System.out.println("@column_type");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_type(stmt, col);
	}

	@Override
	public String column_decltype(long stmt, int col) throws SQLException {
		// System.out.println("@column_decltype");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_decltype(stmt, col);
	}

	@Override
	public String column_table_name(long stmt, int col) throws SQLException {
		// System.out.println("@column_table_name");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_table_name(stmt, col);
	}

	@Override
	public String column_name(long stmt, int col) throws SQLException {
		// System.out.println("@column_name");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_name(stmt, col);
	}

	@Override
	public String column_text(long stmt, int col) throws SQLException {
		// System.out.println("@column_text");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_text(stmt, col);
	}

	@Override
	public byte[] column_blob(long stmt, int col) throws SQLException {
		// System.out.println("@column_blob");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_blob(stmt, col);
	}

	@Override
	public double column_double(long stmt, int col) throws SQLException {
		// System.out.println("@column_double");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_double(stmt, col);
	}

	@Override
	public long column_long(long stmt, int col) throws SQLException {
		// System.out.println("@column_long");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_long(stmt, col);
	}

	@Override
	public int column_int(long stmt, int col) throws SQLException {
		// System.out.println("@column_int");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_int(stmt, col);
	}

	@Override
	int bind_null(long stmt, int pos) throws SQLException {
		// System.out.println("@bind_null");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_null(stmt, pos);
	}

	@Override
	int bind_int(long stmt, int pos, int v) throws SQLException {
		// System.out.println("@bind_int");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_int(stmt, pos, v);
	}

	@Override
	int bind_long(long stmt, int pos, long v) throws SQLException {
		// System.out.println("@bind_long");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_long(stmt, pos, v);
	}

	@Override
	int bind_double(long stmt, int pos, double v) throws SQLException {
		// System.out.println("@bind_double");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_double(stmt, pos, v);
	}

	@Override
	int bind_text(long stmt, int pos, String v) throws SQLException {
		// System.out.println("@bind_text");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_text(stmt, pos, v);
	}

	@Override
	int bind_blob(long stmt, int pos, byte[] v) throws SQLException {
		// System.out.println("@bind_blob");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.bind_blob(stmt, pos, v);
	}

	@Override
	public void result_null(long context) throws SQLException {
		// System.out.println("@result_null");
		db.result_null(context);
	}

	@Override
	public void result_text(long context, String val) throws SQLException {
		// System.out.println("@result_text");
		db.result_text(context, val);
	}

	@Override
	public void result_blob(long context, byte[] val) throws SQLException {
		// System.out.println("@result_blob");
		db.result_blob(context, val);
	}

	@Override
	public void result_double(long context, double val) throws SQLException {
		// System.out.println("@result_double");
		db.result_double(context, val);
	}

	@Override
	public void result_long(long context, long val) throws SQLException {
		// System.out.println("@result_long");
		db.result_long(context, val);
	}

	@Override
	public void result_int(long context, int val) throws SQLException {
		// System.out.println("@result_int");
		db.result_int(context, val);
	}

	@Override
	public void result_error(long context, String err) throws SQLException {
		// System.out.println("@result_error");
		db.result_error(context, err);
	}

	@Override
	public String value_text(Function f, int arg) throws SQLException {
		// System.out.println("@value_text");
		return db.value_text(f, arg);
	}

	@Override
	public byte[] value_blob(Function f, int arg) throws SQLException {
		// System.out.println("@value_blob");
		return db.value_blob(f, arg);
	}

	@Override
	public double value_double(Function f, int arg) throws SQLException {
		// System.out.println("@value_double");
		return db.value_double(f, arg);
	}

	@Override
	public long value_long(Function f, int arg) throws SQLException {
		// System.out.println("@value_long");
		return db.value_long(f, arg);
	}

	@Override
	public int value_int(Function f, int arg) throws SQLException {
		// System.out.println("@value_int");
		return db.value_int(f, arg);
	}

	@Override
	public int value_type(Function f, int arg) throws SQLException {
		// System.out.println("@value_type");
		return db.value_type(f, arg);
	}

	@Override
	public int create_function(String name, Function f) throws SQLException {
		// System.out.println("@create_function");
		return db.create_function(name, f);
	}

	@Override
	public int destroy_function(String name) throws SQLException {
		// System.out.println("@destroy_function");
		return db.destroy_function(name);
	}

	@Override
	void free_functions() throws SQLException {
		// System.out.println("@free_functions");
		db.free_functions();
	}

	@Override
	public int backup(String dbName, String destFileName, ProgressObserver observer) throws SQLException {
		// System.out.println("@backup");
		return db.backup(dbName, destFileName, observer);
	}

	@Override
	public int restore(String dbName, String sourceFileName, ProgressObserver observer) throws SQLException {
		// System.out.println("@restore");
		return db.restore(dbName, sourceFileName, observer);
	}

	@Override
	public void register_progress_handler(int vmCalls, ProgressHandler progressHandler) throws SQLException {
		// System.out.println("@register_progress_handler");
		db.register_progress_handler(vmCalls, progressHandler);
	}

	@Override
	public void clear_progress_handler() throws SQLException {
		// System.out.println("@clear_progress_handler");
		db.clear_progress_handler();
	}

	@Override
	boolean[][] column_metadata(long stmt) throws SQLException {
		// System.out.println("@column_metadata");

		if (stmt == 0)
			throw new SQLException("The prepared statement has been finalized");
		
		return db.column_metadata(stmt);
	}
}
