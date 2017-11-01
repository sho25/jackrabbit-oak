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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
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
annotation|@
name|Component
argument_list|(
name|configurationPolicy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|QueryEngineSettingsService
operator|.
name|Configuration
operator|.
name|class
argument_list|)
specifier|public
class|class
name|QueryEngineSettingsService
block|{
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Apache Jackrabbit Query Engine Settings Service"
argument_list|,
name|description
operator|=
literal|"Various settings exposed by Oak QueryEngine. Note that settings done by system property "
operator|+
literal|"supersedes the one defined via OSGi config"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"In memory limit"
argument_list|,
name|description
operator|=
literal|"Maximum number of entries that can be held in memory while evaluating any query"
argument_list|)
name|int
name|queryLimitInMemory
parameter_list|()
default|default
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"In memory read limit"
argument_list|,
name|description
operator|=
literal|"Maximum number of results which can be read by any query"
argument_list|)
name|int
name|queryLimitReads
parameter_list|()
default|default
name|DEFAULT_QUERY_LIMIT_READS
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Fail traversal"
argument_list|,
name|description
operator|=
literal|"If enabled any query execution which results in traversal would fail."
argument_list|)
name|boolean
name|queryFailTraversal
parameter_list|()
default|default
name|DEFAULT_QUERY_FAIL_TRAVERSAL
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Fast result size"
argument_list|,
name|description
operator|=
literal|"Whether the query result size (QueryResult.getSize()) should return an estimation for queries that return many nodes. "
operator|+
literal|"The estimate will be larger or equal the actual result size, as it includes unindexed properties and nodes that are not accessible. "
operator|+
literal|"If disabled, for such cases -1 is returned. "
operator|+
literal|"Note: even if enabled, getSize may still return -1 if the index used does not support the feature."
argument_list|)
name|boolean
name|fastQuerySize
parameter_list|()
default|default
literal|false
function_decl|;
block|}
comment|// should be the same as QueryEngineSettings.DEFAULT_QUERY_LIMIT_IN_MEMORY
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
init|=
literal|500000
decl_stmt|;
specifier|static
specifier|final
name|String
name|QUERY_LIMIT_IN_MEMORY
init|=
literal|"queryLimitInMemory"
decl_stmt|;
comment|// should be the same as QueryEngineSettings.DEFAULT_QUERY_LIMIT_READS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_QUERY_LIMIT_READS
init|=
literal|100000
decl_stmt|;
specifier|static
specifier|final
name|String
name|QUERY_LIMIT_READS
init|=
literal|"queryLimitReads"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_QUERY_FAIL_TRAVERSAL
init|=
literal|false
decl_stmt|;
specifier|static
specifier|final
name|String
name|QUERY_FAIL_TRAVERSAL
init|=
literal|"queryFailTraversal"
decl_stmt|;
specifier|static
specifier|final
name|String
name|QUERY_FAST_QUERY_SIZE
init|=
literal|"fastQuerySize"
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
annotation|@
name|Reference
specifier|private
name|QueryEngineSettingsMBean
name|queryEngineSettings
decl_stmt|;
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|context
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|QueryEngineSettings
operator|.
name|OAK_QUERY_LIMIT_IN_MEMORY
argument_list|)
operator|==
literal|null
condition|)
block|{
name|int
name|queryLimitInMemory
init|=
name|config
operator|.
name|queryLimitInMemory
argument_list|()
decl_stmt|;
name|queryEngineSettings
operator|.
name|setLimitInMemory
argument_list|(
name|queryLimitInMemory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logMsg
argument_list|(
name|QUERY_LIMIT_IN_MEMORY
argument_list|,
name|QueryEngineSettings
operator|.
name|OAK_QUERY_LIMIT_IN_MEMORY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|QueryEngineSettings
operator|.
name|OAK_QUERY_LIMIT_READS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|int
name|queryLimitReads
init|=
name|config
operator|.
name|queryLimitReads
argument_list|()
decl_stmt|;
name|queryEngineSettings
operator|.
name|setLimitReads
argument_list|(
name|queryLimitReads
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logMsg
argument_list|(
name|QUERY_LIMIT_IN_MEMORY
argument_list|,
name|QueryEngineSettings
operator|.
name|OAK_QUERY_LIMIT_READS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|QueryEngineSettings
operator|.
name|OAK_QUERY_FAIL_TRAVERSAL
argument_list|)
operator|==
literal|null
condition|)
block|{
name|boolean
name|failTraversal
init|=
name|config
operator|.
name|queryFailTraversal
argument_list|()
decl_stmt|;
name|queryEngineSettings
operator|.
name|setFailTraversal
argument_list|(
name|failTraversal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logMsg
argument_list|(
name|QUERY_FAIL_TRAVERSAL
argument_list|,
name|QueryEngineSettings
operator|.
name|OAK_QUERY_FAIL_TRAVERSAL
argument_list|)
expr_stmt|;
block|}
name|boolean
name|fastQuerySizeSysProp
init|=
name|QueryEngineSettings
operator|.
name|DEFAULT_FAST_QUERY_SIZE
decl_stmt|;
name|boolean
name|fastQuerySizeFromConfig
init|=
name|config
operator|.
name|fastQuerySize
argument_list|()
decl_stmt|;
name|queryEngineSettings
operator|.
name|setFastQuerySize
argument_list|(
name|fastQuerySizeFromConfig
operator|||
name|fastQuerySizeSysProp
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Initialize QueryEngine settings {}"
argument_list|,
name|queryEngineSettings
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|logMsg
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|sysPropKey
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"For {} using value {} defined via system property {}"
argument_list|,
name|key
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
name|sysPropKey
argument_list|)
argument_list|,
name|sysPropKey
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

