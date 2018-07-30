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
operator|.
name|datastore
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|InputStream
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
name|Random
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|data
operator|.
name|DataStore
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
name|core
operator|.
name|data
operator|.
name|DataStoreException
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
name|core
operator|.
name|data
operator|.
name|FileDataStore
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
name|junit
operator|.
name|Test
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
operator|.
name|concat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Helper for retrieving the {@link DataStoreBlobStore} instantiated via system properties  *  * User must specify the class of DataStore to use via 'dataStore' system property  *  * Further to configure properties of DataStore instance one can specify extra system property  * where the key has a prefix 'ds.' or 'bs.'. So to set 'minRecordLength' of FileDataStore specify  * the system property as 'ds.minRecordLength'  */
end_comment

begin_class
specifier|public
class|class
name|DataStoreUtils
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataStoreUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DS_CLASS_NAME
init|=
literal|"dataStore"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DS_PROP_PREFIX
init|=
literal|"ds."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BS_PROP_PREFIX
init|=
literal|"bs."
decl_stmt|;
comment|/**      * By default create a default directory. But if overridden will need to be unset      */
specifier|public
specifier|static
name|long
name|time
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|File
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getBlobStore
argument_list|(
name|homeDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|String
name|homeDir
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|className
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|DS_CLASS_NAME
argument_list|,
name|OakFileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|DataStore
name|ds
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|DataStore
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|getConfig
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataStoreBlobStore
argument_list|(
name|ds
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DataStoreBlobStore
name|getBlobStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getBlobStore
argument_list|(
name|getHomeDir
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getConfig
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
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
name|?
argument_list|>
name|e
range|:
name|Maps
operator|.
name|fromProperties
argument_list|(
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|DS_PROP_PREFIX
argument_list|)
operator|||
name|key
operator|.
name|startsWith
argument_list|(
name|BS_PROP_PREFIX
argument_list|)
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|//length of bs.
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|concat
argument_list|(
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"target/blobstore/"
operator|+
operator|(
name|time
operator|==
operator|-
literal|1
condition|?
literal|0
else|:
name|time
operator|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|FileDataStore
name|createFDS
parameter_list|(
name|File
name|root
parameter_list|,
name|int
name|minRecordLength
parameter_list|)
block|{
name|OakFileDataStore
name|fds
init|=
operator|new
name|OakFileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|root
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|setMinRecordLength
argument_list|(
name|minRecordLength
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|fds
return|;
block|}
specifier|public
specifier|static
name|CachingFileDataStore
name|createCachingFDS
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|cachePath
parameter_list|)
throws|throws
name|DataStoreException
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"fsBackendPath"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|CachingFileDataStore
name|ds
init|=
operator|new
name|CachingFileDataStore
argument_list|()
decl_stmt|;
name|ds
operator|.
name|setMinRecordLength
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|config
init|=
name|DataStoreUtils
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|props
operator|.
name|putAll
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|Maps
operator|.
name|fromProperties
argument_list|(
name|props
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|cachePath
argument_list|)
expr_stmt|;
return|return
name|ds
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertySetup
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|DS_CLASS_NAME
argument_list|,
name|FileDataStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"ds.minRecordLength"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|DataStoreBlobStore
name|dbs
init|=
name|getBlobStore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|dbs
operator|.
name|getDataStore
argument_list|()
operator|.
name|getMinRecordLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|InputStream
name|randomStream
parameter_list|(
name|int
name|seed
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|size
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
end_class

end_unit

