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
name|upgrade
operator|.
name|cli
operator|.
name|blob
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
name|blob
operator|.
name|cloud
operator|.
name|azure
operator|.
name|blobstorage
operator|.
name|AzureDataStore
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
name|blob
operator|.
name|AbstractSharedCachingDataStore
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
name|Properties
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

begin_class
specifier|public
class|class
name|AzureDataStoreFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testPopulateProperties
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
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
literal|"cacheSize"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|AzureDataStore
name|ds
init|=
name|AzureDataStoreFactory
operator|.
name|createDS
argument_list|(
literal|"/tmp"
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|readLong
argument_list|(
literal|"cacheSize"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/tmp"
argument_list|,
name|readString
argument_list|(
literal|"path"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPopulatePropertiesNoPath
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
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
literal|"cacheSize"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|String
name|tempDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
decl_stmt|;
name|AzureDataStore
name|ds
init|=
name|AzureDataStoreFactory
operator|.
name|createDS
argument_list|(
literal|null
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|readLong
argument_list|(
literal|"cacheSize"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tempDir
argument_list|,
name|readString
argument_list|(
literal|"path"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPopulatePropertiesPathFromConfig
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
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
literal|"cacheSize"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"path"
argument_list|,
literal|"/tmp"
argument_list|)
expr_stmt|;
name|AzureDataStore
name|ds
init|=
name|AzureDataStoreFactory
operator|.
name|createDS
argument_list|(
literal|null
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|readLong
argument_list|(
literal|"cacheSize"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/tmp"
argument_list|,
name|readString
argument_list|(
literal|"path"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStripOsgiPrefix
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
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
literal|"cacheSize"
argument_list|,
literal|"I\"123\""
argument_list|)
expr_stmt|;
name|AzureDataStore
name|ds
init|=
name|AzureDataStoreFactory
operator|.
name|createDS
argument_list|(
literal|"xyz"
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|123
argument_list|,
name|readLong
argument_list|(
literal|"cacheSize"
argument_list|,
name|AbstractSharedCachingDataStore
operator|.
name|class
argument_list|,
name|ds
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|readLong
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|field
operator|.
name|getLong
argument_list|(
name|object
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|readString
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Object
name|object
parameter_list|)
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalAccessException
block|{
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|String
operator|)
name|field
operator|.
name|get
argument_list|(
name|object
argument_list|)
return|;
block|}
block|}
end_class

end_unit

