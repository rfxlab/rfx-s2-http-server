package rfx.server.util.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import rfx.server.util.LogUtil;



public abstract class DbCommand<T>  {
	
	protected DataSource dataSource;
	
	protected DbSql dbSql = null;
	
	/**
	 * use dbSql for easily debug
	 */
	@Deprecated
	protected CallableStatement cs = null;
	
	protected Connection con = null;
	
	public DbCommand(DataSource dataSource) {
		super();
		if (dataSource == null) {
			throw new IllegalArgumentException("dataSource is NULL!");
		}
		this.dataSource = dataSource;
		try {
			con = dataSource.getConnection();
		} catch (SQLException e) {
			LogUtil.error(e);
		}
	}
	
	public T execute() {		
		T rs = null;		
		try {			
			if (con != null) {
				rs = build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.error(e);
		} finally {
			if (cs != null) {
				try {
					cs.close();
				} catch (SQLException e1) {
				}
			}
			if(this.dbSql != null){
				this.dbSql.close();
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
				}
			}
		}
		return rs;
	}

	//define the logic at implementer
	protected abstract T build() throws SQLException;
}
