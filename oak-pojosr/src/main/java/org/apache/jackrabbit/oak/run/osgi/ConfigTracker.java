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
name|File
import|;
end_import

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
name|base
operator|.
name|Charsets
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
name|base
operator|.
name|Splitter
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONValue
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
name|ServiceReference
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
name|osgi
operator|.
name|util
operator|.
name|tracker
operator|.
name|ServiceTracker
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
comment|/**  * Installs config obtained from JSON Config file or passed as part of  * startup  */
end_comment

begin_class
class|class
name|ConfigTracker
extends|extends
name|ServiceTracker
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
name|Map
name|config
decl_stmt|;
specifier|private
specifier|final
name|BundleContext
name|bundleContext
decl_stmt|;
specifier|public
name|ConfigTracker
parameter_list|(
name|Map
name|config
parameter_list|,
name|BundleContext
name|bundleContext
parameter_list|)
block|{
name|super
argument_list|(
name|bundleContext
argument_list|,
name|ConfigurationAdmin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|bundleContext
operator|=
name|bundleContext
expr_stmt|;
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|ConfigurationAdmin
name|cm
init|=
operator|(
name|ConfigurationAdmin
operator|)
name|super
operator|.
name|addingService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
try|try
block|{
name|synchronizeConfigs
argument_list|(
operator|new
name|ConfigInstaller
argument_list|(
name|cm
argument_list|,
name|bundleContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error occurred while installing configs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|cm
return|;
block|}
comment|/**      * Synchronizes the configs. All config added by this class is also kept in sync with re runs      * i.e. if a config was added in first run and say later removed then that would also be removed      * from the ConfigurationAdmin      */
specifier|private
name|void
name|synchronizeConfigs
parameter_list|(
name|ConfigInstaller
name|configInstaller
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|existingPids
init|=
name|configInstaller
operator|.
name|determineExistingConfigs
argument_list|()
decl_stmt|;
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
name|configs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
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
name|configFromFile
init|=
name|parseJSONConfig
argument_list|(
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|OakOSGiRepositoryFactory
operator|.
name|REPOSITORY_CONFIG_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|configs
operator|.
name|putAll
argument_list|(
name|configFromFile
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|runtimeConfig
init|=
operator|(
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
operator|)
name|config
operator|.
name|get
argument_list|(
name|OakOSGiRepositoryFactory
operator|.
name|REPOSITORY_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|runtimeConfig
operator|!=
literal|null
condition|)
block|{
name|configs
operator|.
name|putAll
argument_list|(
name|runtimeConfig
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|processedPids
init|=
name|configInstaller
operator|.
name|installConfigs
argument_list|(
name|configs
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|pidsToBeRemoved
init|=
name|Sets
operator|.
name|difference
argument_list|(
name|existingPids
argument_list|,
name|processedPids
argument_list|)
decl_stmt|;
name|configInstaller
operator|.
name|removeConfigs
argument_list|(
name|pidsToBeRemoved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
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
name|parseJSONConfig
parameter_list|(
name|String
name|jsonFilePath
parameter_list|)
throws|throws
name|IOException
block|{
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
name|configs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|jsonFilePath
operator|==
literal|null
condition|)
block|{
return|return
name|configs
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|splitToList
argument_list|(
name|jsonFilePath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|filePath
range|:
name|files
control|)
block|{
name|File
name|jsonFile
init|=
operator|new
name|File
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|jsonFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No file found at path {}. Ignoring the file entry"
argument_list|,
name|jsonFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|content
init|=
name|Files
operator|.
name|toString
argument_list|(
name|jsonFile
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|JSONObject
name|json
init|=
operator|(
name|JSONObject
operator|)
name|JSONValue
operator|.
name|parse
argument_list|(
name|content
argument_list|)
decl_stmt|;
name|configs
operator|.
name|putAll
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
return|return
name|configs
return|;
block|}
block|}
end_class

end_unit

