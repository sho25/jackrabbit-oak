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
name|plugins
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
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
name|PropertiesUtil
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
name|osgi
operator|.
name|OsgiWhiteboard
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
name|observation
operator|.
name|ChangeCollectorProvider
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
name|commit
operator|.
name|ValidatorProvider
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
name|state
operator|.
name|NodeStore
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardIndexEditorProvider
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
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|,
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak Async Indexer Service"
argument_list|,
name|description
operator|=
literal|"Configures the async indexer services which performs periodic indexing of repository content"
argument_list|)
specifier|public
class|class
name|AsyncIndexerService
block|{
annotation|@
name|Property
argument_list|(
name|value
operator|=
block|{
literal|"async:5"
block|}
argument_list|,
name|cardinality
operator|=
literal|1024
argument_list|,
name|label
operator|=
literal|"Async Indexer Configs"
argument_list|,
name|description
operator|=
literal|"Async indexer configs in the form of<name>:<interval in secs> e.g. \"async:5\""
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ASYNC_CONFIG
init|=
literal|"asyncConfigs"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|PROP_LEASE_TIMEOUT_DEFAULT
init|=
literal|15
decl_stmt|;
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
name|PROP_LEASE_TIMEOUT_DEFAULT
argument_list|,
name|label
operator|=
literal|"Lease time out"
argument_list|,
name|description
operator|=
literal|"Lease timeout in minutes. AsyncIndexer would wait for this timeout period before breaking "
operator|+
literal|"async indexer lease"
argument_list|)
specifier|private
specifier|static
specifier|final
name|String
name|PROP_LEASE_TIME_OUT
init|=
literal|"leaseTimeOutMinutes"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
name|CONFIG_SEP
init|=
literal|':'
decl_stmt|;
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
name|WhiteboardIndexEditorProvider
name|indexEditorProvider
init|=
operator|new
name|WhiteboardIndexEditorProvider
argument_list|()
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Reference
argument_list|(
name|target
operator|=
literal|"(type="
operator|+
name|ChangeCollectorProvider
operator|.
name|TYPE
operator|+
literal|")"
argument_list|)
specifier|private
name|ValidatorProvider
name|validatorProvider
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|private
name|IndexMBeanRegistration
name|indexRegistration
decl_stmt|;
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
name|List
argument_list|<
name|AsyncConfig
argument_list|>
name|asyncIndexerConfig
init|=
name|getAsyncConfig
argument_list|(
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_ASYNC_CONFIG
argument_list|)
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Whiteboard
name|whiteboard
init|=
operator|new
name|OsgiWhiteboard
argument_list|(
name|bundleContext
argument_list|)
decl_stmt|;
name|indexRegistration
operator|=
operator|new
name|IndexMBeanRegistration
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|indexEditorProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|long
name|leaseTimeOutMin
init|=
name|PropertiesUtil
operator|.
name|toInteger
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|PROP_LEASE_TIME_OUT
argument_list|)
argument_list|,
name|PROP_LEASE_TIMEOUT_DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|AsyncConfig
name|c
range|:
name|asyncIndexerConfig
control|)
block|{
name|AsyncIndexUpdate
name|task
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
name|c
operator|.
name|name
argument_list|,
name|nodeStore
argument_list|,
name|indexEditorProvider
argument_list|,
name|statisticsProvider
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|task
operator|.
name|setValidatorProviders
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|validatorProvider
argument_list|)
argument_list|)
expr_stmt|;
name|task
operator|.
name|setLeaseTimeOut
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
name|leaseTimeOutMin
argument_list|)
argument_list|)
expr_stmt|;
name|indexRegistration
operator|.
name|registerAsyncIndexer
argument_list|(
name|task
argument_list|,
name|c
operator|.
name|timeIntervalInSecs
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Configured async indexers {} "
argument_list|,
name|asyncIndexerConfig
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Lease time: {} mins and AsyncIndexUpdate configured with {}"
argument_list|,
name|leaseTimeOutMin
argument_list|,
name|validatorProvider
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|public
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|indexRegistration
operator|!=
literal|null
condition|)
block|{
name|indexRegistration
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
comment|//~-------------------------------------------< internal>
specifier|static
name|List
argument_list|<
name|AsyncConfig
argument_list|>
name|getAsyncConfig
parameter_list|(
name|String
index|[]
name|configs
parameter_list|)
block|{
name|List
argument_list|<
name|AsyncConfig
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|config
range|:
name|configs
control|)
block|{
name|int
name|idOfEq
init|=
name|config
operator|.
name|indexOf
argument_list|(
name|CONFIG_SEP
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|idOfEq
operator|>
literal|0
argument_list|,
literal|"Invalid config provided [%s]"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|configs
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|config
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idOfEq
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|long
name|interval
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|config
operator|.
name|substring
argument_list|(
name|idOfEq
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|AsyncConfig
argument_list|(
name|name
argument_list|,
name|interval
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|static
class|class
name|AsyncConfig
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|long
name|timeIntervalInSecs
decl_stmt|;
specifier|private
name|AsyncConfig
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|timeIntervalInSecs
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|timeIntervalInSecs
operator|=
name|timeIntervalInSecs
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AsyncConfig{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", timeIntervalInSecs="
operator|+
name|timeIntervalInSecs
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

