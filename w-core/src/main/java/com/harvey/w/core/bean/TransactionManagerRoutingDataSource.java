package com.harvey.w.core.bean;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.AbstractDataSource;

public class TransactionManagerRoutingDataSource extends AbstractDataSource {

	private DataSource primaryDataSource;

	@Override
	public Connection getConnection() throws SQLException {
		DataSource ds = this.getDataSource();
		return ds.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		DataSource ds = this.getDataSource();
		return ds.getConnection(username, password);
	}

	protected DataSource getDataSource() throws SQLException {
		DataSource ds = Hibernate4TransactionManager.DATASOURCES.get();
		return ds == null ? this.primaryDataSource : ds;
	}

	public DataSource getPrimaryDataSource() {
		return primaryDataSource;
	}

	public void setPrimaryDataSource(DataSource primaryDataSource) {
		this.primaryDataSource = primaryDataSource;
	}
}
