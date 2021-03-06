
DROP TABLE IF EXISTS SNAPSHOTS;
CREATE TABLE SNAPSHOTS (
	ID               VARCHAR,
	NAME             VARCHAR,
	METRICS          VARCHAR,
	ACTUAL_COUNT     BIGINT,
	EXPECTED_COUNT   BIGINT,
	START_TIME       BIGINT,  -- start time for measurment
	END_TIME         BIGINT,  -- end time for measurment
	CONSTRAINT SNAPSHOTS_PK 
	           PRIMARY KEY (ID, METRICS)
);

DROP TABLE IF EXISTS JOBS;
CREATE TABLE JOBS (
	JOB_ID           VARCHAR PRIMARY KEY,
	NAME             VARCHAR NOT NULL UNIQUE,
	START_TIME       BIGINT,  
	END_TIME         BIGINT,  -- expected end time
);



-- aggregate view of snapshot measurments
CREATE VIEW SNAPSHOT_VIEWS AS 
   SELECT ID, NAME, METRICS,
   	(SELECT MIN(a.START_TIME) FROM SNAPSHOTS a 
   	  WHERE a.ID=ID) AS START_TIME,
   	(SELECT MAX(a.END_TIME) FROM SNAPSHOTS a 
   	  WHERE a.ID=ID) AS END_TIME,
   	 EXPECTED_COUNT,
   	 COUNT(*) AS COUNT
   FROM SNAPSHOTS
   GROUP BY (ID,NAME,METRICS,EXPECTED_COUNT);
   

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
