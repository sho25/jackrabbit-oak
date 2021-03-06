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
name|blob
operator|.
name|datastore
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|api
operator|.
name|jmx
operator|.
name|CacheStatsMBean
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
name|spi
operator|.
name|blob
operator|.
name|SharedBackend
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
name|blob
operator|.
name|stats
operator|.
name|BlobStoreStatsMBean
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
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|MockOsgi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|DataStoreServiceTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|mbeanRegs
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"repository.home"
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|FileDataStoreService
name|fds
init|=
operator|new
name|FileDataStoreService
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setStatisticsProvider
argument_list|(
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|activate
argument_list|(
name|fds
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|BlobStoreStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|context
operator|.
name|getService
argument_list|(
name|CacheStatsMBean
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * Test {@link CachingFileDataStore} is returned when cacheSize> 0 by default.      */
annotation|@
name|Test
specifier|public
name|void
name|configCachingFileDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nasPath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/NASPath"
decl_stmt|;
name|String
name|cachePath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/cachePath"
decl_stmt|;
name|DataStore
name|ds
init|=
name|getAssertCachingFileDataStore
argument_list|(
name|nasPath
argument_list|,
name|cachePath
argument_list|)
decl_stmt|;
name|CachingFileDataStore
name|cds
init|=
operator|(
name|CachingFileDataStore
operator|)
name|ds
decl_stmt|;
name|SharedBackend
name|backend
init|=
name|cds
operator|.
name|getBackend
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|(
name|Properties
operator|)
name|getField
argument_list|(
name|backend
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"path not equal"
argument_list|,
name|nasPath
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|FSBackend
operator|.
name|FS_BACKEND_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * Test to verify @FileDataStore is returned if cacheSize is not configured.      */
annotation|@
name|Test
specifier|public
name|void
name|configFileDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nasPath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/NASPath"
decl_stmt|;
name|String
name|cachePath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/cachePath"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"repository.home"
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|FileDataStoreService
operator|.
name|PATH
argument_list|,
name|nasPath
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|FileDataStoreService
operator|.
name|CACHE_PATH
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|FileDataStoreService
name|fdsSvc
init|=
operator|new
name|FileDataStoreService
argument_list|()
decl_stmt|;
name|DataStore
name|ds
init|=
name|fdsSvc
operator|.
name|createDataStore
argument_list|(
name|context
operator|.
name|componentContext
argument_list|()
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not instance of FileDataStore"
argument_list|,
name|ds
operator|instanceof
name|FileDataStore
argument_list|)
expr_stmt|;
name|FileDataStore
name|fds
init|=
operator|(
name|FileDataStore
operator|)
name|ds
decl_stmt|;
name|assertEquals
argument_list|(
literal|"path not equal"
argument_list|,
name|nasPath
argument_list|,
name|fds
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests the regitration of CachingFileDataStore and checks existence of      * reference.key file on first access of getOrCreateReference.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|registerAndCheckReferenceKey
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|.
name|registerService
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
expr_stmt|;
name|String
name|nasPath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/NASPath"
decl_stmt|;
name|String
name|cachePath
init|=
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/cachePath"
decl_stmt|;
name|DataStore
name|ds
init|=
name|getAssertCachingFileDataStore
argument_list|(
name|nasPath
argument_list|,
name|cachePath
argument_list|)
decl_stmt|;
specifier|final
name|CachingFileDataStore
name|dataStore
init|=
operator|(
name|CachingFileDataStore
operator|)
name|ds
decl_stmt|;
name|byte
index|[]
name|key
init|=
name|dataStore
operator|.
name|getBackend
argument_list|()
operator|.
name|getOrCreateReferenceKey
argument_list|()
decl_stmt|;
comment|// Check bytes retrieved from reference.key file
name|File
name|refFile
init|=
operator|new
name|File
argument_list|(
name|nasPath
argument_list|,
literal|"reference.key"
argument_list|)
decl_stmt|;
name|byte
index|[]
name|keyRet
init|=
name|FileUtils
operator|.
name|readFileToByteArray
argument_list|(
name|refFile
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|key
argument_list|,
name|keyRet
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|key
argument_list|,
name|dataStore
operator|.
name|getBackend
argument_list|()
operator|.
name|getOrCreateReferenceKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DataStore
name|getAssertCachingFileDataStore
parameter_list|(
name|String
name|nasPath
parameter_list|,
name|String
name|cachePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|long
name|cacheSize
init|=
literal|100L
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|config
operator|.
name|put
argument_list|(
literal|"repository.home"
argument_list|,
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|FileDataStoreService
operator|.
name|CACHE_SIZE
argument_list|,
name|cacheSize
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|FileDataStoreService
operator|.
name|PATH
argument_list|,
name|nasPath
argument_list|)
expr_stmt|;
name|config
operator|.
name|put
argument_list|(
name|FileDataStoreService
operator|.
name|CACHE_PATH
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|FileDataStoreService
name|fdsSvc
init|=
operator|new
name|FileDataStoreService
argument_list|()
decl_stmt|;
name|DataStore
name|ds
init|=
name|fdsSvc
operator|.
name|createDataStore
argument_list|(
name|context
operator|.
name|componentContext
argument_list|()
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|PropertiesUtil
operator|.
name|populate
argument_list|(
name|ds
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ds
operator|.
name|init
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"not instance of CachingFDS"
argument_list|,
name|ds
operator|instanceof
name|CachingFileDataStore
argument_list|)
expr_stmt|;
return|return
name|ds
return|;
block|}
specifier|private
specifier|static
name|Object
name|getField
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"properties"
argument_list|)
decl_stmt|;
comment|//NoSuchFieldException
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|f
operator|.
name|get
argument_list|(
name|obj
argument_list|)
return|;
block|}
block|}
end_class

end_unit

