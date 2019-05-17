begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|api
operator|.
name|jmx
operator|.
name|QueryEngineSettingsMBean
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
name|QueryStatsMBean
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
name|QueryStatsMBeanImpl
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
name|QueryStatsReporter
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
name|spi
operator|.
name|query
operator|.
name|QueryLimits
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
name|StatisticsProvider
import|;
end_import

begin_comment
comment|/**  * Settings of the query engine.  */
end_comment

begin_class
specifier|public
class|class
name|QueryEngineSettings
implements|implements
name|QueryEngineSettingsMBean
implements|,
name|QueryLimits
block|{
comment|/**      * the flag used to turn on/off the optimisations on top of the {@code org.apache.jackrabbit.oak.query.Query} object.      * {@code -Doak.query.sql2optimisation}      */
specifier|public
specifier|static
specifier|final
name|String
name|SQL2_OPTIMISATION_FLAG
init|=
literal|"oak.query.sql2optimisation"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SQL2_OPTIMISATION_FLAG_2
init|=
literal|"oak.query.sql2optimisation2"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|SQL2_OPTIMIZATION_2
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|SQL2_OPTIMISATION_FLAG_2
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_QUERY_LIMIT_IN_MEMORY
init|=
literal|"oak.queryLimitInMemory"
decl_stmt|;
comment|// should be the same as QueryEngineSettingsService.DEFAULT_QUERY_LIMIT_IN_MEMORY
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
init|=
name|Long
operator|.
name|getLong
argument_list|(
name|OAK_QUERY_LIMIT_IN_MEMORY
argument_list|,
literal|500000
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_QUERY_LIMIT_READS
init|=
literal|"oak.queryLimitReads"
decl_stmt|;
comment|// should be the same as QueryEngineSettingsService.DEFAULT_QUERY_LIMIT_READS
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_QUERY_LIMIT_READS
init|=
name|Long
operator|.
name|getLong
argument_list|(
name|OAK_QUERY_LIMIT_READS
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OAK_QUERY_FAIL_TRAVERSAL
init|=
literal|"oak.queryFailTraversal"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_FAIL_TRAVERSAL
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|OAK_QUERY_FAIL_TRAVERSAL
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_FULL_TEXT_COMPARISON_WITHOUT_INDEX
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.queryFullTextComparisonWithoutIndex"
argument_list|)
decl_stmt|;
specifier|private
name|long
name|limitInMemory
init|=
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
decl_stmt|;
specifier|private
name|long
name|limitReads
init|=
name|DEFAULT_QUERY_LIMIT_READS
decl_stmt|;
specifier|private
name|boolean
name|failTraversal
init|=
name|DEFAULT_FAIL_TRAVERSAL
decl_stmt|;
specifier|private
name|boolean
name|fullTextComparisonWithoutIndex
init|=
name|DEFAULT_FULL_TEXT_COMPARISON_WITHOUT_INDEX
decl_stmt|;
specifier|private
name|boolean
name|sql2Optimisation
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
name|SQL2_OPTIMISATION_FLAG
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OAK_FAST_QUERY_SIZE
init|=
literal|"oak.fastQuerySize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_FAST_QUERY_SIZE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|OAK_FAST_QUERY_SIZE
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|fastQuerySize
init|=
name|DEFAULT_FAST_QUERY_SIZE
decl_stmt|;
specifier|private
specifier|final
name|QueryStatsMBeanImpl
name|queryStats
init|=
operator|new
name|QueryStatsMBeanImpl
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|/**      * StatisticsProvider used to record query side metrics.      */
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
specifier|final
name|QueryValidator
name|queryValidator
init|=
operator|new
name|QueryValidator
argument_list|()
decl_stmt|;
specifier|public
name|QueryEngineSettings
parameter_list|()
block|{
name|statisticsProvider
operator|=
name|StatisticsProvider
operator|.
name|NOOP
expr_stmt|;
block|}
specifier|public
name|QueryEngineSettings
parameter_list|(
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLimitInMemory
parameter_list|()
block|{
return|return
name|limitInMemory
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLimitInMemory
parameter_list|(
name|long
name|limitInMemory
parameter_list|)
block|{
name|this
operator|.
name|limitInMemory
operator|=
name|limitInMemory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLimitReads
parameter_list|()
block|{
return|return
name|limitReads
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLimitReads
parameter_list|(
name|long
name|limitReads
parameter_list|)
block|{
name|this
operator|.
name|limitReads
operator|=
name|limitReads
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getFailTraversal
parameter_list|()
block|{
return|return
name|failTraversal
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setFailTraversal
parameter_list|(
name|boolean
name|failTraversal
parameter_list|)
block|{
name|this
operator|.
name|failTraversal
operator|=
name|failTraversal
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFastQuerySize
parameter_list|()
block|{
return|return
name|fastQuerySize
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setFastQuerySize
parameter_list|(
name|boolean
name|fastQuerySize
parameter_list|)
block|{
name|this
operator|.
name|fastQuerySize
operator|=
name|fastQuerySize
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|OAK_FAST_QUERY_SIZE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|fastQuerySize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFullTextComparisonWithoutIndex
parameter_list|(
name|boolean
name|fullTextComparisonWithoutIndex
parameter_list|)
block|{
name|this
operator|.
name|fullTextComparisonWithoutIndex
operator|=
name|fullTextComparisonWithoutIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|getFullTextComparisonWithoutIndex
parameter_list|()
block|{
return|return
name|fullTextComparisonWithoutIndex
return|;
block|}
specifier|public
name|boolean
name|isSql2Optimisation
parameter_list|()
block|{
return|return
name|sql2Optimisation
return|;
block|}
specifier|public
name|QueryStatsMBean
name|getQueryStats
parameter_list|()
block|{
return|return
name|queryStats
return|;
block|}
specifier|public
name|QueryStatsReporter
name|getQueryStatsReporter
parameter_list|()
block|{
return|return
name|queryStats
return|;
block|}
specifier|public
name|StatisticsProvider
name|getStatisticsProvider
parameter_list|()
block|{
return|return
name|statisticsProvider
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setQueryValidatorPattern
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
name|comment
parameter_list|,
name|boolean
name|failQuery
parameter_list|)
block|{
name|queryValidator
operator|.
name|setPattern
argument_list|(
name|key
argument_list|,
name|pattern
argument_list|,
name|comment
argument_list|,
name|failQuery
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getQueryValidatorJson
parameter_list|()
block|{
return|return
name|queryValidator
operator|.
name|getJson
argument_list|()
return|;
block|}
specifier|public
name|QueryValidator
name|getQueryValidator
parameter_list|()
block|{
return|return
name|queryValidator
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
literal|"QueryEngineSettings{"
operator|+
literal|"limitInMemory="
operator|+
name|limitInMemory
operator|+
literal|", limitReads="
operator|+
name|limitReads
operator|+
literal|", failTraversal="
operator|+
name|failTraversal
operator|+
literal|", fullTextComparisonWithoutIndex="
operator|+
name|fullTextComparisonWithoutIndex
operator|+
literal|", sql2Optimisation="
operator|+
name|sql2Optimisation
operator|+
literal|", fastQuerySize="
operator|+
name|fastQuerySize
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

