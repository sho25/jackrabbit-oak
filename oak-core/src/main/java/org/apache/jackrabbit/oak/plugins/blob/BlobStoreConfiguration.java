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
name|plugins
operator|.
name|blob
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Strings
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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

begin_comment
comment|/**  * Defines the configuration needed by a BlobStore.  */
end_comment

begin_class
specifier|public
class|class
name|BlobStoreConfiguration
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PRIMARY_DATA_STORE
init|=
literal|"primary"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ARCHIVE_DATA_STORE
init|=
literal|"archive"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROP_DATA_STORE
init|=
literal|"dataStoreProvider"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROP_BLOB_STORE_PROVIDER
init|=
literal|"blobStoreProvider"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_BLOB_STORE_PROVIDER
init|=
literal|""
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configMap
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|propKeys
decl_stmt|;
comment|/**      * Instantiates a new data store configuration.      */
specifier|private
name|BlobStoreConfiguration
parameter_list|()
block|{
name|configMap
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|propKeys
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
comment|// get default props
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"blobstore.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{         }
comment|// populate keys from the default set
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|defaultMap
init|=
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|propKeys
operator|.
name|addAll
argument_list|(
name|defaultMap
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove empty default properties from the map
name|getConfigMap
argument_list|()
operator|.
name|putAll
argument_list|(
name|Maps
operator|.
name|filterValues
argument_list|(
name|defaultMap
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
if|if
condition|(
operator|(
name|input
operator|==
literal|null
operator|)
operator|||
name|input
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new configuration object with default values.      *       * @return the data store configuration      */
specifier|public
specifier|static
name|BlobStoreConfiguration
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|BlobStoreConfiguration
argument_list|()
return|;
block|}
comment|/**      * Load configuration from the system props.      *       * @return the configuration      */
specifier|public
name|BlobStoreConfiguration
name|loadFromSystemProps
parameter_list|()
block|{
comment|// remove all jvm set properties to trim the map
name|getConfigMap
argument_list|()
operator|.
name|putAll
argument_list|(
name|Maps
operator|.
name|filterKeys
argument_list|(
name|Maps
operator|.
name|fromProperties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|startsWith
argument_list|(
literal|"java."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"sun."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"user."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"file."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"line."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"os."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"awt."
argument_list|)
operator|||
name|input
operator|.
name|startsWith
argument_list|(
literal|"path."
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Load configuration from a map.      *       * @param map      *            the map      * @return the configuration      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|BlobStoreConfiguration
name|loadFromMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|cfgMap
parameter_list|)
block|{
name|getConfigMap
argument_list|()
operator|.
name|putAll
argument_list|(
operator|(
name|Map
argument_list|<
name|?
extends|extends
name|String
argument_list|,
name|?
extends|extends
name|String
argument_list|>
operator|)
name|cfgMap
argument_list|)
expr_stmt|;
name|loadFromSystemProps
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Load configuration from a BundleContext or the map provided.      *       * @param map      *            the map      * @param context      *            the context      * @return the configuration      */
specifier|public
name|BlobStoreConfiguration
name|loadFromContextOrMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|map
parameter_list|,
name|BundleContext
name|context
parameter_list|)
block|{
name|loadFromMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|getPropKeys
argument_list|()
control|)
block|{
if|if
condition|(
name|context
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|configMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|context
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getConfigMap
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|void
name|addProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|getConfigMap
argument_list|()
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getConfigMap
parameter_list|()
block|{
return|return
name|configMap
return|;
block|}
specifier|public
name|void
name|setConfigMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configMap
parameter_list|)
block|{
name|this
operator|.
name|configMap
operator|=
name|configMap
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPropKeys
parameter_list|()
block|{
return|return
name|propKeys
return|;
block|}
specifier|public
name|void
name|setPropKeys
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|propKeys
parameter_list|)
block|{
name|this
operator|.
name|propKeys
operator|=
name|propKeys
expr_stmt|;
block|}
block|}
end_class

end_unit

