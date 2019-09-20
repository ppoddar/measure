package com.nutanix.bpg.spark;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

public class Main {

	public static void main(String[] args) {
		SparkConf config = new SparkConf(true);
		config.setMaster("local");
		config.setAppName("start");
		SparkContext sc = new SparkContext(config);
		SparkSession session = new SparkSession(sc);
		SQLContext sqlContext = new SQLContext(session);
		
		Map<String, String> options = new HashMap<String, String>();
		options.put("url", "jdbc:postgresql:bpg");
		options.put("dbtable", "public.snapshots");
		options.put("driver", "org.postgresql.Driver");
		
		Dataset<Row> snapshots = sqlContext.read()
				.format("jdbc")
				.options(options).load();
		options.put("dbtable", "public.measurement_pg_stat_database");
		Dataset<Row> measurements = sqlContext.read()
				.format("jdbc")
				.options(options).load();
		
		
		
		Dataset<Row> result = 
		measurements.join(snapshots, snapshots.col("id")
				.equalTo(measurements.col("ctx")))
				.select("actual_count");
		
		result.show();
	}

}
