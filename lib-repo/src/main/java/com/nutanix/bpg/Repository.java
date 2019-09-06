package com.nutanix.bpg;

import java.sql.Connection;

public interface Repository {
	Connection getConnection();
}
