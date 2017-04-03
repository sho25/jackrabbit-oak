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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
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
name|concurrent
operator|.
name|ScheduledExecutorService
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
name|ScheduledThreadPoolExecutor
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricFilter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Slf4jReporter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
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
name|Oak
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
name|fixture
operator|.
name|JcrCreator
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|plugins
operator|.
name|metric
operator|.
name|MetricStatisticsProvider
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

begin_comment
comment|/**  * {@code ReadPropertyTest} implements a performance test, which reads  * three properties: one with a jcr prefix, one with the empty prefix and a  * third one, which does not exist.  */
end_comment

begin_class
specifier|public
class|class
name|ReadPropertyTest
extends|extends
name|AbstractTest
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
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|root
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
name|TEST_ID
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|root
operator|.
name|setProperty
argument_list|(
literal|"property"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|root
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
expr_stmt|;
name|root
operator|.
name|getProperty
argument_list|(
literal|"property"
argument_list|)
expr_stmt|;
name|root
operator|.
name|hasProperty
argument_list|(
literal|"does-not-exist"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|boolean
name|enableMetrics
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"enableMetrics"
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableMetrics
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Enabling Metrics integration"
argument_list|)
expr_stmt|;
name|MBeanServer
name|server
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ScheduledExecutorService
name|executor
init|=
name|MoreExecutors
operator|.
name|getExitingScheduledExecutorService
argument_list|(
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|MetricStatisticsProvider
name|statsProvider
init|=
operator|new
name|MetricStatisticsProvider
argument_list|(
name|server
argument_list|,
name|executor
argument_list|)
decl_stmt|;
name|oak
operator|.
name|getWhiteboard
argument_list|()
operator|.
name|register
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|statsProvider
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Slf4jReporter
name|reporter
init|=
name|Slf4jReporter
operator|.
name|forRegistry
argument_list|(
name|statsProvider
operator|.
name|getRegistry
argument_list|()
argument_list|)
operator|.
name|outputTo
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.metrics"
argument_list|)
argument_list|)
operator|.
name|convertRatesTo
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|MetricFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|name
parameter_list|,
name|Metric
name|metric
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"SESSION_READ"
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|convertDurationsTo
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|reporter
operator|.
name|start
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
block|}
end_class

end_unit
