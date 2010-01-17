package com.cn.service;

import java.sql.SQLException;
import java.sql.Statement;

public class StatService extends Service {

	public StatService() {
		super();
	}

	public void createDefaultStats(int cid) {
		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeQuery("INSERT INTO Stats "+
					"(cid, lvl, credits, xp, health, energy) " +
					"VALUES " +
					"("+cid+", 1, 0, 0, 100, 20)" );
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
