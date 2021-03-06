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
name|run
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Dictionary
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Set
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
name|Maps
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
name|Sets
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
name|utils
operator|.
name|properties
operator|.
name|InterpolationHelper
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
name|framework
operator|.
name|Constants
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
name|InvalidSyntaxException
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
name|cm
operator|.
name|Configuration
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
name|cm
operator|.
name|ConfigurationAdmin
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
class|class
name|ConfigInstaller
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MARKER_NAME
init|=
literal|"oak.configinstall.name"
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
name|ConfigurationAdmin
name|cm
decl_stmt|;
specifier|private
specifier|final
name|BundleContext
name|bundleContext
decl_stmt|;
specifier|public
name|ConfigInstaller
parameter_list|(
name|ConfigurationAdmin
name|cm
parameter_list|,
name|BundleContext
name|bundleContext
parameter_list|)
block|{
name|this
operator|.
name|cm
operator|=
name|cm
expr_stmt|;
name|this
operator|.
name|bundleContext
operator|=
name|bundleContext
expr_stmt|;
block|}
specifier|public
name|void
name|installConfigs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|osgiConfig
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pidEntry
range|:
name|osgiConfig
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|pidString
init|=
name|pidEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|current
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|current
operator|.
name|putAll
argument_list|(
name|pidEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|performSubstitution
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|String
index|[]
name|pid
init|=
name|parsePid
argument_list|(
name|pidString
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|getConfiguration
argument_list|(
name|pidString
argument_list|,
name|pid
index|[
literal|0
index|]
argument_list|,
name|pid
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|config
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|old
init|=
name|props
operator|!=
literal|null
condition|?
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
operator|new
name|DictionaryAsMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|props
argument_list|)
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|old
operator|.
name|remove
argument_list|(
name|MARKER_NAME
argument_list|)
expr_stmt|;
name|old
operator|.
name|remove
argument_list|(
name|Constants
operator|.
name|SERVICE_PID
argument_list|)
expr_stmt|;
name|old
operator|.
name|remove
argument_list|(
name|ConfigurationAdmin
operator|.
name|SERVICE_FACTORYPID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|current
operator|.
name|equals
argument_list|(
name|old
argument_list|)
condition|)
block|{
name|current
operator|.
name|put
argument_list|(
name|MARKER_NAME
argument_list|,
name|pidString
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|getBundleLocation
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|setBundleLocation
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating configuration from {}"
argument_list|,
name|pidString
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating configuration from {}"
argument_list|,
name|pidString
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|update
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|removeConfigs
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|pidsToBeRemoved
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|pidString
range|:
name|pidsToBeRemoved
control|)
block|{
name|String
index|[]
name|pid
init|=
name|parsePid
argument_list|(
name|pidString
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|getConfiguration
argument_list|(
name|pidString
argument_list|,
name|pid
index|[
literal|0
index|]
argument_list|,
name|pid
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|config
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pidsToBeRemoved
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Configuration belonging to following pids have been removed {}"
argument_list|,
name|pidsToBeRemoved
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Determines the existing configs which are installed by ConfigInstaller      *      * @return set of pid strings      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|determineExistingConfigs
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidSyntaxException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|pids
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|String
name|filter
init|=
literal|"("
operator|+
name|MARKER_NAME
operator|+
literal|"="
operator|+
literal|"*"
operator|+
literal|")"
decl_stmt|;
name|Configuration
index|[]
name|configurations
init|=
name|cm
operator|.
name|listConfigurations
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|configurations
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Configuration
name|cfg
range|:
name|configurations
control|)
block|{
name|pids
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|cfg
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|MARKER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pids
return|;
block|}
specifier|private
name|void
name|performSubstitution
parameter_list|(
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|current
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|simpleConfig
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|current
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|instanceof
name|String
condition|)
block|{
name|simpleConfig
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|InterpolationHelper
operator|.
name|performSubstitution
argument_list|(
name|simpleConfig
argument_list|,
name|bundleContext
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|simpleConfig
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|current
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Configuration
name|getConfiguration
parameter_list|(
name|String
name|pidString
parameter_list|,
name|String
name|pid
parameter_list|,
name|String
name|factoryPid
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|oldConfiguration
init|=
name|findExistingConfiguration
argument_list|(
name|pidString
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldConfiguration
operator|!=
literal|null
condition|)
block|{
return|return
name|oldConfiguration
return|;
block|}
else|else
block|{
name|Configuration
name|newConfiguration
decl_stmt|;
if|if
condition|(
name|factoryPid
operator|!=
literal|null
condition|)
block|{
name|newConfiguration
operator|=
name|cm
operator|.
name|createFactoryConfiguration
argument_list|(
name|pid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConfiguration
operator|=
name|cm
operator|.
name|getConfiguration
argument_list|(
name|pid
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|newConfiguration
return|;
block|}
block|}
specifier|private
name|Configuration
name|findExistingConfiguration
parameter_list|(
name|String
name|pidString
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|filter
init|=
literal|"("
operator|+
name|MARKER_NAME
operator|+
literal|"="
operator|+
name|escapeFilterValue
argument_list|(
name|pidString
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|Configuration
index|[]
name|configurations
init|=
name|cm
operator|.
name|listConfigurations
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|configurations
operator|!=
literal|null
operator|&&
name|configurations
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|configurations
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|escapeFilterValue
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"[(]"
argument_list|,
literal|"\\\\("
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[)]"
argument_list|,
literal|"\\\\)"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[=]"
argument_list|,
literal|"\\\\="
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[\\*]"
argument_list|,
literal|"\\\\*"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
index|[]
name|parsePid
parameter_list|(
name|String
name|pid
parameter_list|)
block|{
name|int
name|n
init|=
name|pid
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|String
name|factoryPid
init|=
name|pid
operator|.
name|substring
argument_list|(
name|n
operator|+
literal|1
argument_list|)
decl_stmt|;
name|pid
operator|=
name|pid
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
index|[]
block|{
name|pid
block|,
name|factoryPid
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|String
index|[]
block|{
name|pid
block|,
literal|null
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

