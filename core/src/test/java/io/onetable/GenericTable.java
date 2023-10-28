/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package io.onetable;

import java.nio.file.Path;
import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import io.onetable.model.storage.TableFormat;

public interface GenericTable<T, Q> extends AutoCloseable {

  List<T> insertRows(int numRows);

  List<T> insertRecordsForSpecialPartition(int numRows);

  void upsertRows(List<T> rows);

  void deleteRows(List<T> rows);

  void deletePartition(Q partitionValue);

  void deleteSpecialPartition();

  Q getAnyPartitionValue(List<T> rows);

  String getBasePath();

  String getOrderByColumn();

  void close();

  static GenericTable getInstance(
      String tableName,
      Path tempDir,
      SparkSession sparkSession,
      JavaSparkContext jsc,
      TableFormat sourceFormat,
      boolean isPartitioned) {
    switch (sourceFormat) {
      case HUDI:
        return TestSparkHudiTable.forStandardSchemaAndPartitioning(
            tableName, tempDir, jsc, isPartitioned);
      case DELTA:
        return TestSparkDeltaTable.forStandardSchemaAndPartitioning(
            tableName, tempDir, sparkSession, isPartitioned);
      default:
        throw new IllegalArgumentException("Unsupported source format: " + sourceFormat);
    }
  }

  static GenericTable getInstanceWithAdditionalColumns(
      String tableName,
      Path tempDir,
      SparkSession sparkSession,
      JavaSparkContext jsc,
      TableFormat sourceFormat,
      boolean isPartitioned) {
    switch (sourceFormat) {
      case HUDI:
        return TestSparkHudiTable.forSchemaWithAdditionalColumnsAndPartitioning(
            tableName, tempDir, jsc, isPartitioned);
      case DELTA:
        return TestSparkDeltaTable.forSchemaWithAdditionalColumnsAndPartitioning(
            tableName, tempDir, sparkSession, isPartitioned);
      default:
        throw new IllegalArgumentException("Unsupported source format: " + sourceFormat);
    }
  }
}