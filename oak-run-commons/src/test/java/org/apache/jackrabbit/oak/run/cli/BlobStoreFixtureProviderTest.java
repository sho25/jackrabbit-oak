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
name|cli
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Properties
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
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
name|felix
operator|.
name|cm
operator|.
name|file
operator|.
name|ConfigurationHandler
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
name|BlobTrackingStore
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
name|datastore
operator|.
name|TypedDataStore
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
name|BlobStore
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
name|GarbageCollectableBlobStore
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|assertThat
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|BlobStoreFixtureProviderTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|TemporaryFolder
name|temporaryFolder
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
name|Test
specifier|public
name|void
name|fileDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--fds-path"
block|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"--read-write"
block|}
decl_stmt|;
try|try
init|(
name|BlobStoreFixture
name|fixture
init|=
name|BlobStoreFixtureProvider
operator|.
name|create
argument_list|(
name|createFDSOptions
argument_list|(
name|args
argument_list|)
argument_list|)
init|)
block|{
name|String
name|blobId
init|=
name|fixture
operator|.
name|getBlobStore
argument_list|()
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|readOnlyFileDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
block|{
literal|"--fds-path"
block|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
try|try
init|(
name|BlobStoreFixture
name|fixture
init|=
name|BlobStoreFixtureProvider
operator|.
name|create
argument_list|(
name|createFDSOptions
argument_list|(
name|args
argument_list|)
argument_list|)
init|)
block|{
try|try
block|{
name|BlobStore
name|blobStore
init|=
name|fixture
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|blobStore
argument_list|,
name|instanceOf
argument_list|(
name|GarbageCollectableBlobStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobStore
argument_list|,
name|instanceOf
argument_list|(
name|TypedDataStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobStore
argument_list|,
name|instanceOf
argument_list|(
name|BlobTrackingStore
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|fixture
operator|.
name|getBlobStore
argument_list|()
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{              }
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|configLoading
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|File
name|config
init|=
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"test.cfg"
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|os
init|=
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
name|config
argument_list|)
init|)
block|{
name|p
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|Properties
name|p2
init|=
name|BlobStoreFixtureProvider
operator|.
name|loadConfig
argument_list|(
name|config
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|p2
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|configLoading_OSGi
parameter_list|()
throws|throws
name|Exception
block|{
name|Dictionary
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|p
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
name|p
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
name|File
name|config
init|=
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"test.config"
argument_list|)
decl_stmt|;
try|try
init|(
name|OutputStream
name|os
init|=
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
name|config
argument_list|)
init|)
block|{
name|ConfigurationHandler
operator|.
name|write
argument_list|(
name|os
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
name|Properties
name|p2
init|=
name|BlobStoreFixtureProvider
operator|.
name|loadConfig
argument_list|(
name|config
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|p2
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|p2
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|,
operator|(
name|int
index|[]
operator|)
name|p2
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Options
name|createFDSOptions
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
operator|.
name|withDisableSystemExit
argument_list|()
decl_stmt|;
name|opts
operator|.
name|parseAndConfigure
argument_list|(
name|parser
argument_list|,
name|args
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
block|}
end_class

end_unit

