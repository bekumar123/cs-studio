--
-- Table structure for table `engine_status`
--

DROP TABLE IF EXISTS `engine_status`;
CREATE TABLE `engine_status` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `engine_id` int(10) unsigned NOT NULL,
  `status` enum('ON','OFF') NOT NULL,
  `time` bigint(20) NOT NULL COMMENT 'Timestamp in nanoseconds since epoch 1970-01-01 00:00:00',
  `info` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`,`time`,`engine_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1
PARTITION BY RANGE (time)
SUBPARTITION BY HASH (engine_id)
SUBPARTITIONS 40
(PARTITION p0 VALUES LESS THAN (unix_timestamp('2011-07-01')*1000000000) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (unix_timestamp('2011-08-01')*1000000000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (unix_timestamp('2011-09-01')*1000000000) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (unix_timestamp('2011-10-01')*1000000000) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (unix_timestamp('2011-11-01')*1000000000) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (unix_timestamp('2011-12-01')*1000000000) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (unix_timestamp('2012-01-01')*1000000000) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (unix_timestamp('2012-02-01')*1000000000) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (unix_timestamp('2012-03-01')*1000000000) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (unix_timestamp('2012-04-01')*1000000000) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (unix_timestamp('2012-05-01')*1000000000) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (unix_timestamp('2012-06-01')*1000000000) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (unix_timestamp('2012-07-01')*1000000000) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (unix_timestamp('2012-08-01')*1000000000) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (unix_timestamp('2012-09-01')*1000000000) ENGINE = InnoDB,
 PARTITION p15 VALUES LESS THAN (unix_timestamp('2012-10-01')*1000000000) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (unix_timestamp('2012-11-01')*1000000000) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (unix_timestamp('2012-12-01')*1000000000) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (unix_timestamp('2013-01-01')*1000000000) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (unix_timestamp('2013-02-01')*1000000000) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (unix_timestamp('2013-03-01')*1000000000) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (unix_timestamp('2013-04-01')*1000000000) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (unix_timestamp('2013-05-01')*1000000000) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (unix_timestamp('2013-06-01')*1000000000) ENGINE = InnoDB,
 PARTITION pRest VALUES LESS THAN MAXVALUE ENGINE = InnoDB);
 