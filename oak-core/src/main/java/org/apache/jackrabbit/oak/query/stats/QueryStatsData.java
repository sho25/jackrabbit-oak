begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|json
operator|.
name|JsopBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|QueryEngineSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|CounterStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|HistogramStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatsOptions
import|;
end_import

begin_class
specifier|public
class|class
name|QueryStatsData
block|{
specifier|private
specifier|final
name|String
name|query
decl_stmt|;
specifier|private
specifier|final
name|String
name|language
decl_stmt|;
specifier|private
specifier|final
name|long
name|createdMillis
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|internal
decl_stmt|;
specifier|private
name|String
name|lastThreadName
decl_stmt|;
specifier|private
name|long
name|lastExecutedMillis
decl_stmt|;
specifier|private
name|long
name|executeCount
decl_stmt|;
comment|/**      * Rows read by iterating over the query result.      */
specifier|private
name|long
name|totalRowsRead
decl_stmt|;
specifier|private
name|long
name|maxRowsRead
decl_stmt|;
comment|/**      *  Rows returned by the index.      */
specifier|private
name|long
name|totalRowsScanned
decl_stmt|;
specifier|private
name|long
name|maxRowsScanned
decl_stmt|;
specifier|private
name|long
name|planNanos
decl_stmt|;
specifier|private
name|long
name|readNanos
decl_stmt|;
specifier|private
name|long
name|maxTimeNanos
decl_stmt|;
specifier|private
name|boolean
name|captureStackTraces
decl_stmt|;
specifier|private
name|boolean
name|isSlowQuery
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|updateTotalQueryHistogram
init|=
literal|true
decl_stmt|;
specifier|public
name|QueryStatsData
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|language
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|language
operator|=
name|language
expr_stmt|;
block|}
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|query
operator|+
literal|"/"
operator|+
name|language
return|;
block|}
comment|/**      * The maximum CPU time needed to run one query.      *       * @return the time in nanoseconds      */
specifier|public
name|long
name|getMaxTimeNanos
parameter_list|()
block|{
return|return
name|maxTimeNanos
return|;
block|}
specifier|public
name|long
name|getTotalTimeNanos
parameter_list|()
block|{
return|return
name|planNanos
operator|+
name|readNanos
return|;
block|}
specifier|public
name|long
name|getMaxRowsScanned
parameter_list|()
block|{
return|return
name|maxRowsScanned
return|;
block|}
specifier|public
name|void
name|setCaptureStackTraces
parameter_list|(
name|boolean
name|captureStackTraces
parameter_list|)
block|{
name|this
operator|.
name|captureStackTraces
operator|=
name|captureStackTraces
expr_stmt|;
block|}
specifier|public
name|long
name|getCreatedMillis
parameter_list|()
block|{
return|return
name|createdMillis
return|;
block|}
specifier|public
name|long
name|getExecuteCount
parameter_list|()
block|{
return|return
name|executeCount
return|;
block|}
specifier|public
name|long
name|getTotalRowsRead
parameter_list|()
block|{
return|return
name|totalRowsRead
return|;
block|}
specifier|public
name|long
name|getTotalRowsScanned
parameter_list|()
block|{
return|return
name|totalRowsScanned
return|;
block|}
specifier|public
name|String
name|getLanguage
parameter_list|()
block|{
return|return
name|language
return|;
block|}
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
specifier|public
name|boolean
name|isInternal
parameter_list|()
block|{
return|return
name|internal
return|;
block|}
specifier|public
name|String
name|getLastThreadName
parameter_list|()
block|{
return|return
name|lastThreadName
return|;
block|}
specifier|public
name|long
name|getLastExecutedMillis
parameter_list|()
block|{
return|return
name|lastExecutedMillis
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|JsopBuilder
argument_list|()
operator|.
name|object
argument_list|()
operator|.
name|key
argument_list|(
literal|"createdMillis"
argument_list|)
operator|.
name|value
argument_list|(
name|getTimeString
argument_list|(
name|createdMillis
argument_list|)
argument_list|)
operator|.
name|key
argument_list|(
literal|"lastExecutedMillis"
argument_list|)
operator|.
name|value
argument_list|(
name|getTimeString
argument_list|(
name|lastExecutedMillis
argument_list|)
argument_list|)
operator|.
name|key
argument_list|(
literal|"executeCount"
argument_list|)
operator|.
name|value
argument_list|(
name|executeCount
argument_list|)
operator|.
name|key
argument_list|(
literal|"totalRowsRead"
argument_list|)
operator|.
name|value
argument_list|(
name|totalRowsRead
argument_list|)
operator|.
name|key
argument_list|(
literal|"maxRowsRead"
argument_list|)
operator|.
name|value
argument_list|(
name|maxRowsRead
argument_list|)
operator|.
name|key
argument_list|(
literal|"totalRowsScanned"
argument_list|)
operator|.
name|value
argument_list|(
name|totalRowsScanned
argument_list|)
operator|.
name|key
argument_list|(
literal|"maxRowsScanned"
argument_list|)
operator|.
name|value
argument_list|(
name|maxRowsScanned
argument_list|)
operator|.
name|key
argument_list|(
literal|"planNanos"
argument_list|)
operator|.
name|value
argument_list|(
name|planNanos
argument_list|)
operator|.
name|key
argument_list|(
literal|"readNanos"
argument_list|)
operator|.
name|value
argument_list|(
name|readNanos
argument_list|)
operator|.
name|key
argument_list|(
literal|"maxTimeNanos"
argument_list|)
operator|.
name|value
argument_list|(
name|maxTimeNanos
argument_list|)
operator|.
name|key
argument_list|(
literal|"internal"
argument_list|)
operator|.
name|value
argument_list|(
name|internal
argument_list|)
operator|.
name|key
argument_list|(
literal|"query"
argument_list|)
operator|.
name|value
argument_list|(
name|query
argument_list|)
operator|.
name|key
argument_list|(
literal|"language"
argument_list|)
operator|.
name|value
argument_list|(
name|language
argument_list|)
operator|.
name|key
argument_list|(
literal|"lastThreadName"
argument_list|)
operator|.
name|value
argument_list|(
name|lastThreadName
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|getTimeString
parameter_list|(
name|long
name|timeMillis
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%tF %tT"
argument_list|,
name|timeMillis
argument_list|,
name|timeMillis
argument_list|)
return|;
block|}
specifier|public
class|class
name|QueryExecutionStats
block|{
name|long
name|time
decl_stmt|;
specifier|private
specifier|final
name|long
name|SLOW_QUERY_HISTOGRAM
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
name|long
name|TOTAL_QUERY_HISTOGRAM
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|String
name|SLOW_QUERY_PERCENTILE_METRICS_NAME
init|=
literal|"SLOW_QUERY_PERCENTILE_METRICS"
decl_stmt|;
specifier|private
specifier|final
name|String
name|SLOW_QUERY_COUNT_NAME
init|=
literal|"SLOW_QUERY_COUNT"
decl_stmt|;
specifier|private
specifier|final
name|int
name|SLOW_QUERY_LIMIT_SCANNED
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.query.slowScanLimit"
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|QueryRecorder
operator|.
name|record
argument_list|(
name|query
argument_list|,
name|internal
argument_list|)
expr_stmt|;
name|executeCount
operator|++
expr_stmt|;
name|lastExecutedMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|time
operator|+=
name|nanos
expr_stmt|;
name|planNanos
operator|+=
name|nanos
expr_stmt|;
name|maxTimeNanos
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTimeNanos
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setInternal
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
name|internal
operator|=
name|b
expr_stmt|;
block|}
specifier|public
name|void
name|setThreadName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|captureStackTraces
condition|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|e
range|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"\n\tat "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|name
operator|+
name|buff
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|lastThreadName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|long
name|count
parameter_list|,
name|long
name|max
parameter_list|,
name|long
name|nanos
parameter_list|)
block|{
name|totalRowsRead
operator|+=
name|count
expr_stmt|;
name|maxRowsRead
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxRowsRead
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|time
operator|+=
name|nanos
expr_stmt|;
name|readNanos
operator|+=
name|nanos
expr_stmt|;
name|maxTimeNanos
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxTimeNanos
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|scan
parameter_list|(
name|long
name|count
parameter_list|,
name|long
name|max
parameter_list|,
name|QueryEngineSettings
name|queryEngineSettings
parameter_list|)
block|{
name|totalRowsScanned
operator|+=
name|count
expr_stmt|;
name|maxRowsScanned
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxRowsScanned
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|long
name|maxScannedLimit
init|=
name|Math
operator|.
name|min
argument_list|(
name|SLOW_QUERY_LIMIT_SCANNED
argument_list|,
name|queryEngineSettings
operator|.
name|getLimitReads
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateTotalQueryHistogram
condition|)
block|{
name|updateTotalQueryHistogram
operator|=
literal|false
expr_stmt|;
name|HistogramStats
name|histogramStats
init|=
name|queryEngineSettings
operator|.
name|getStatisticsProvider
argument_list|()
operator|.
name|getHistogram
argument_list|(
name|SLOW_QUERY_PERCENTILE_METRICS_NAME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|histogramStats
operator|.
name|update
argument_list|(
name|TOTAL_QUERY_HISTOGRAM
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|totalRowsScanned
operator|>=
name|maxScannedLimit
operator|&&
operator|!
name|isSlowQuery
condition|)
block|{
name|isSlowQuery
operator|=
literal|true
expr_stmt|;
name|HistogramStats
name|histogramStats
init|=
name|queryEngineSettings
operator|.
name|getStatisticsProvider
argument_list|()
operator|.
name|getHistogram
argument_list|(
name|SLOW_QUERY_PERCENTILE_METRICS_NAME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|histogramStats
operator|.
name|update
argument_list|(
name|SLOW_QUERY_HISTOGRAM
argument_list|)
expr_stmt|;
name|CounterStats
name|slowQueryCounter
init|=
name|queryEngineSettings
operator|.
name|getStatisticsProvider
argument_list|()
operator|.
name|getCounterStats
argument_list|(
name|SLOW_QUERY_COUNT_NAME
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|slowQueryCounter
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

