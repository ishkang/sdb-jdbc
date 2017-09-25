package sdb.core;

import java.sql.SQLException;

import sdb.BusyHandler;
import sdb.Function;
import sdb.ProgressHandler;

final class RemoteDB extends DB {

	@Override
	public void interrupt() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void busy_timeout(int ms) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void busy_handler(BusyHandler busyHandler) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	String errmsg() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String libversion() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int changes() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int total_changes() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int shared_cache(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int enable_load_extension(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void _open(String filename, int openFlags) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void _close() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int _exec(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected long prepare(String sql) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int finalize(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int step(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int reset(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int clear_bindings(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_parameter_count(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int column_count(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int column_type(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String column_decltype(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String column_table_name(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String column_name(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String column_text(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] column_blob(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double column_double(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long column_long(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int column_int(long stmt, int col) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_null(long stmt, int pos) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_int(long stmt, int pos, int v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_long(long stmt, int pos, long v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_double(long stmt, int pos, double v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_text(long stmt, int pos, String v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int bind_blob(long stmt, int pos, byte[] v) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void result_null(long context) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_text(long context, String val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_blob(long context, byte[] val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_double(long context, double val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_long(long context, long val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_int(long context, int val) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void result_error(long context, String err) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public String value_text(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] value_blob(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double value_double(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long value_long(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int value_int(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int value_type(Function f, int arg) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int create_function(String name, Function f) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int destroy_function(String name) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void free_functions() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int backup(String dbName, String destFileName, ProgressObserver observer) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int restore(String dbName, String sourceFileName, ProgressObserver observer) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void register_progress_handler(int vmCalls, ProgressHandler progressHandler) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear_progress_handler() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	boolean[][] column_metadata(long stmt) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
