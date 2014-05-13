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
operator|.
name|blob
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
name|assertTrue
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
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

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
name|Iterator
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
name|plugins
operator|.
name|document
operator|.
name|rdb
operator|.
name|RDBBlobStore
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
name|rdb
operator|.
name|RDBBlobStoreFriend
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
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|AbstractBlobStoreTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Tests the RDBBlobStore implementation.  */
end_comment

begin_class
specifier|public
class|class
name|RDBBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
specifier|private
name|RDBBlobStore
name|blobStore
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-url"
argument_list|,
literal|"jdbc:h2:mem:oakblobs"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USERNAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-user"
argument_list|,
literal|"sa"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PASSWD
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"rdb.jdbc-passwd"
argument_list|,
literal|""
argument_list|)
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
name|RDBBlobStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|blobStore
operator|=
operator|new
name|RDBBlobStore
argument_list|(
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|URL
argument_list|,
name|USERNAME
argument_list|,
name|PASSWD
argument_list|)
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|setBlockSize
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|setBlockSizeMin
argument_list|(
literal|48
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|blobStore
expr_stmt|;
name|empty
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|empty
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|empty
parameter_list|(
name|RDBBlobStore
name|blobStore
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|blobStore
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|blobStore
operator|.
name|deleteChunks
argument_list|(
name|ids
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBigBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|min
init|=
literal|0
decl_stmt|;
name|int
name|max
init|=
literal|8
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|int
name|test
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|max
operator|-
name|min
operator|>=
literal|2
condition|)
block|{
name|test
operator|=
operator|(
name|max
operator|+
name|min
operator|)
operator|/
literal|2
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|test
index|]
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest
init|=
name|getDigest
argument_list|(
name|data
argument_list|)
decl_stmt|;
try|try
block|{
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|getDigest
argument_list|(
name|data
argument_list|)
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data2
init|=
name|RDBBlobStoreFriend
operator|.
name|readBlockFromBackend
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|data2
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"data mismatch for length "
operator|+
name|data
operator|.
name|length
argument_list|)
throw|;
block|}
name|min
operator|=
name|test
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|max
operator|=
name|test
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"max id length for "
operator|+
name|URL
operator|+
literal|" was "
operator|+
name|test
argument_list|)
expr_stmt|;
name|int
name|expected
init|=
name|Math
operator|.
name|max
argument_list|(
name|blobStore
operator|.
name|getBlockSize
argument_list|()
argument_list|,
literal|2
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected supported block size is "
operator|+
name|expected
operator|+
literal|", but measured: "
operator|+
name|test
argument_list|,
name|test
operator|>=
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|getDigest
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|MessageDigest
name|messageDigest
decl_stmt|;
try|try
block|{
name|messageDigest
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-256"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|messageDigest
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|messageDigest
operator|.
name|digest
argument_list|()
return|;
block|}
block|}
end_class

end_unit

