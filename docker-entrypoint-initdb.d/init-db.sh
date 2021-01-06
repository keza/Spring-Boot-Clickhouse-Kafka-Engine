#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
CREATE TABLE event_queue (
                                type String,
                                post_id Int32
)
    ENGINE = Kafka
        SETTINGS kafka_broker_list = 'kafka:9092',
            kafka_topic_list = 'events',
            kafka_group_name = 'events_consumer_group1',
            kafka_format = 'JSONEachRow',
            kafka_num_consumers = 3,
            kafka_thread_per_consumer = 1;
CREATE TABLE events (
                          type String,
                          post_id Int32,
                          _topic String,
                          _offset UInt64,
                          _partition UInt64,
                          _timestamp DateTime
) Engine = MergeTree()
   ORDER BY  (type, post_id)
     PRIMARY KEY (type, post_id);
CREATE MATERIALIZED VIEW event_queue_mv TO events AS
SELECT type, post_id, _topic, _offset, _partition, _timestamp
FROM event_queue;
CREATE MATERIALIZED VIEW events_count_mv
    Engine = SummingMergeTree()
        ORDER BY  (type, post_id)
    POPULATE
    AS SELECT
              type,
              post_id,
              count() as count
    FROM events
    GROUP BY (type, post_id);
EOSQL