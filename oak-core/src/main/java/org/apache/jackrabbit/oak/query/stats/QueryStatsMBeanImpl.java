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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentSkipListMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|query
operator|.
name|stats
operator|.
name|QueryStatsData
operator|.
name|QueryExecutionStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|QueryStatsMBeanImpl
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|QueryStatsMBean
implements|,
name|QueryStatsReporter
block|{
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
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
specifier|private
specifier|final
name|int
name|MAX_STATS_DATA
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.query.stats"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|MAX_POPULAR_QUERIES
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.query.slowLimit"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|QueryStatsData
argument_list|>
name|statistics
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|QueryStatsData
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|QueryEngineSettings
name|settings
decl_stmt|;
specifier|private
name|boolean
name|captureStackTraces
decl_stmt|;
specifier|private
name|int
name|evictionCount
decl_stmt|;
specifier|public
name|QueryStatsMBeanImpl
parameter_list|(
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|QueryStatsMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getSlowQueries
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|maxScanned
init|=
name|Math
operator|.
name|min
argument_list|(
name|SLOW_QUERY_LIMIT_SCANNED
argument_list|,
name|settings
operator|.
name|getLimitReads
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryStatsData
name|s
range|:
name|statistics
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getMaxRowsScanned
argument_list|()
operator|>
name|maxScanned
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueryStatsData
name|o1
parameter_list|,
name|QueryStatsData
name|o2
parameter_list|)
block|{
return|return
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getMaxRowsScanned
argument_list|()
argument_list|,
name|o2
operator|.
name|getMaxRowsScanned
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|asTabularData
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TabularData
name|getPopularQueries
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|(
name|statistics
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueryStatsData
name|o1
parameter_list|,
name|QueryStatsData
name|o2
parameter_list|)
block|{
return|return
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|,
name|o2
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
while|while
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
name|MAX_POPULAR_QUERIES
condition|)
block|{
name|list
operator|.
name|remove
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|asTabularData
argument_list|(
name|list
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetStats
parameter_list|()
block|{
name|statistics
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|boolean
name|getCaptureStackTraces
parameter_list|()
block|{
return|return
name|captureStackTraces
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|asJson
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|(
name|statistics
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueryStatsData
name|o1
parameter_list|,
name|QueryStatsData
name|o2
parameter_list|)
block|{
return|return
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|,
name|o2
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"[\n"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryStatsData
name|s
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|s
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|append
argument_list|(
literal|"\n]\n"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryExecutionStats
name|getQueryExecution
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Execute "
operator|+
name|language
operator|+
literal|" / "
operator|+
name|statement
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|statistics
operator|.
name|size
argument_list|()
operator|>
literal|2
operator|*
name|MAX_STATS_DATA
condition|)
block|{
name|evict
argument_list|()
expr_stmt|;
block|}
name|QueryStatsData
name|stats
init|=
operator|new
name|QueryStatsData
argument_list|(
name|statement
argument_list|,
name|language
argument_list|)
decl_stmt|;
name|QueryStatsData
name|s2
init|=
name|statistics
operator|.
name|putIfAbsent
argument_list|(
name|stats
operator|.
name|getKey
argument_list|()
argument_list|,
name|stats
argument_list|)
decl_stmt|;
if|if
condition|(
name|s2
operator|!=
literal|null
condition|)
block|{
name|stats
operator|=
name|s2
expr_stmt|;
block|}
name|stats
operator|.
name|setCaptureStackTraces
argument_list|(
name|captureStackTraces
argument_list|)
expr_stmt|;
return|return
name|stats
operator|.
expr|new
name|QueryExecutionStats
argument_list|()
return|;
block|}
specifier|private
name|void
name|evict
parameter_list|()
block|{
name|evictionCount
operator|++
expr_stmt|;
comment|// retain 50% of the slowest entries
comment|// of the rest, retain the newest entries
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|(
name|statistics
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueryStatsData
name|o1
parameter_list|,
name|QueryStatsData
name|o2
parameter_list|)
block|{
name|int
name|comp
init|=
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|,
name|o2
operator|.
name|getTotalTimeNanos
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|==
literal|0
condition|)
block|{
name|comp
operator|=
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getCreatedMillis
argument_list|()
argument_list|,
name|o2
operator|.
name|getCreatedMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|comp
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
operator|.
name|subList
argument_list|(
name|MAX_STATS_DATA
operator|/
literal|2
argument_list|,
name|MAX_STATS_DATA
argument_list|)
argument_list|,
operator|new
name|Comparator
argument_list|<
name|QueryStatsData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|QueryStatsData
name|o1
parameter_list|,
name|QueryStatsData
name|o2
parameter_list|)
block|{
return|return
operator|-
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getCreatedMillis
argument_list|()
argument_list|,
name|o2
operator|.
name|getCreatedMillis
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|MAX_STATS_DATA
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|statistics
operator|.
name|remove
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getEvictionCount
parameter_list|()
block|{
return|return
name|evictionCount
return|;
block|}
specifier|private
name|TabularData
name|asTabularData
parameter_list|(
name|ArrayList
argument_list|<
name|QueryStatsData
argument_list|>
name|list
parameter_list|)
block|{
name|TabularDataSupport
name|tds
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CompositeType
name|ct
init|=
name|QueryStatsCompositeTypeFactory
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|TabularType
name|tt
init|=
operator|new
name|TabularType
argument_list|(
name|QueryStatsData
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Query History"
argument_list|,
name|ct
argument_list|,
name|QueryStatsCompositeTypeFactory
operator|.
name|index
argument_list|)
decl_stmt|;
name|tds
operator|=
operator|new
name|TabularDataSupport
argument_list|(
name|tt
argument_list|)
expr_stmt|;
name|int
name|position
init|=
literal|1
decl_stmt|;
for|for
control|(
name|QueryStatsData
name|q
range|:
name|list
control|)
block|{
name|tds
operator|.
name|put
argument_list|(
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|QueryStatsCompositeTypeFactory
operator|.
name|names
argument_list|,
name|QueryStatsCompositeTypeFactory
operator|.
name|getValues
argument_list|(
name|q
argument_list|,
name|position
operator|++
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tds
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|QueryStatsCompositeTypeFactory
block|{
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|index
init|=
block|{
literal|"position"
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|names
init|=
block|{
literal|"position"
block|,
literal|"maxTimeMillis"
block|,
literal|"totalTimeMillis"
block|,
literal|"executeCount"
block|,
literal|"rowsRead"
block|,
literal|"rowsScanned"
block|,
literal|"maxRowsScanned"
block|,
literal|"language"
block|,
literal|"statement"
block|,
literal|"lastExecuted"
block|,
literal|"lastThread"
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|descriptions
init|=
name|names
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|final
specifier|static
name|OpenType
index|[]
name|types
init|=
block|{
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|LONG
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
specifier|public
specifier|static
name|CompositeType
name|getCompositeType
parameter_list|()
throws|throws
name|OpenDataException
block|{
return|return
operator|new
name|CompositeType
argument_list|(
name|QueryStatsMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|QueryStatsMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|names
argument_list|,
name|descriptions
argument_list|,
name|types
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Object
index|[]
name|getValues
parameter_list|(
name|QueryStatsData
name|q
parameter_list|,
name|int
name|position
parameter_list|)
block|{
return|return
operator|new
name|Object
index|[]
block|{
operator|(
name|long
operator|)
name|position
block|,
name|q
operator|.
name|getMaxTimeNanos
argument_list|()
operator|/
literal|1000000
block|,
name|q
operator|.
name|getTotalTimeNanos
argument_list|()
operator|/
literal|1000000
block|,
name|q
operator|.
name|getExecuteCount
argument_list|()
block|,
name|q
operator|.
name|getTotalRowsRead
argument_list|()
block|,
name|q
operator|.
name|getTotalRowsScanned
argument_list|()
block|,
name|q
operator|.
name|getMaxRowsScanned
argument_list|()
block|,
name|q
operator|.
name|getLanguage
argument_list|()
block|,
name|q
operator|.
name|getQuery
argument_list|()
block|,
name|QueryStatsData
operator|.
name|getTimeString
argument_list|(
name|q
operator|.
name|getLastExecutedMillis
argument_list|()
argument_list|)
block|,
name|q
operator|.
name|isInternal
argument_list|()
condition|?
literal|"(internal query)"
else|:
name|q
operator|.
name|getLastThreadName
argument_list|()
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

