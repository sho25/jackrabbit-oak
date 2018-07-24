begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|document
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
name|assertEquals
import|;
end_import

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
name|IOException
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
name|ArrayList
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
name|Blob
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
name|json
operator|.
name|BlobSerializer
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
name|BlobStoreBlob
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
name|document
operator|.
name|util
operator|.
name|MongoConnection
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
name|memory
operator|.
name|ArrayBasedBlob
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
name|MemoryBlobStore
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
comment|/**  * Tests the blob store.  */
end_comment

begin_class
specifier|public
class|class
name|BlobTest
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|MongoConnectionFactory
name|connectionFactory
init|=
operator|new
name|MongoConnectionFactory
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RandomizedClusterTest
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//     private static final boolean MONGO_DB = true;
comment|//     private static final long TOTAL_SIZE = 1 * 1024 * 1024 * 1024;
comment|//     private static final int DOCUMENT_COUNT = 10;
specifier|private
specifier|static
specifier|final
name|boolean
name|MONGO_DB
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|TOTAL_SIZE
init|=
literal|1
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DOCUMENT_COUNT
init|=
literal|10
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|setMongoConnection
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|MongoConnection
name|connection
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setMongoDB
argument_list|(
name|connection
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|connection
operator|.
name|getDBName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
name|void
name|dropCollections
parameter_list|()
block|{
if|if
condition|(
name|MONGO_DB
condition|)
block|{
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|connectionFactory
operator|.
name|getConnection
argument_list|()
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addBlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentMK
name|mk
init|=
name|setMongoConnection
argument_list|(
name|builderProvider
operator|.
name|newBuilder
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|long
name|blobSize
init|=
name|TOTAL_SIZE
operator|/
name|DOCUMENT_COUNT
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|blobIds
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// use a new seed each time, to allow running the test multiple times
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DOCUMENT_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|log
argument_list|(
literal|"writing "
operator|+
name|i
operator|+
literal|"/"
operator|+
name|DOCUMENT_COUNT
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|mk
operator|.
name|write
argument_list|(
operator|new
name|RandomStream
argument_list|(
name|blobSize
argument_list|,
name|r
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|blobIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|id
range|:
name|blobIds
control|)
block|{
name|assertEquals
argument_list|(
name|blobSize
argument_list|,
name|mk
operator|.
name|getLength
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlobSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|TestBlobStore
name|blobStore
init|=
operator|new
name|TestBlobStore
argument_list|()
decl_stmt|;
name|DocumentMK
name|mk
init|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|BlobSerializer
name|blobSerializer
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlobSerializer
argument_list|()
decl_stmt|;
name|Blob
name|blob
init|=
operator|new
name|BlobStoreBlob
argument_list|(
name|blobStore
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|blobSerializer
operator|.
name|serialize
argument_list|(
name|blob
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blobStore
operator|.
name|writeCount
argument_list|)
expr_stmt|;
name|blob
operator|=
operator|new
name|ArrayBasedBlob
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|blobSerializer
operator|.
name|serialize
argument_list|(
name|blob
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|blobStore
operator|.
name|writeCount
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
literal|"foo"
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|String
name|blobId
init|=
name|blobStore
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|reference
init|=
name|blobStore
operator|.
name|getReference
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|blob
operator|=
operator|new
name|ReferencedBlob
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|reference
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|writeCount
operator|=
literal|0
expr_stmt|;
name|blobSerializer
operator|.
name|serialize
argument_list|(
name|blob
argument_list|)
expr_stmt|;
comment|//Using reference so no reference should be written
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blobStore
operator|.
name|writeCount
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TestBlobStore
extends|extends
name|MemoryBlobStore
block|{
name|int
name|writeCount
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|writeCount
operator|++
expr_stmt|;
return|return
name|super
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ReferencedBlob
extends|extends
name|ArrayBasedBlob
block|{
specifier|private
specifier|final
name|String
name|reference
decl_stmt|;
specifier|public
name|ReferencedBlob
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|String
name|reference
parameter_list|)
block|{
name|super
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|reference
operator|=
name|reference
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
name|reference
return|;
block|}
block|}
block|}
end_class

end_unit

