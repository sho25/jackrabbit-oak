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
name|assertTrue
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
name|ArrayList
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
name|Collection
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
name|commons
operator|.
name|StringUtils
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
name|junit
operator|.
name|LogCustomizer
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
name|RDBDataSourceWrapper
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
name|Assert
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|org
operator|.
name|slf4j
operator|.
name|event
operator|.
name|Level
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
name|ImmutableList
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
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|RDBBlobStoreTest
extends|extends
name|AbstractBlobStoreTest
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|supportsStatsCollection
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|fixtures
parameter_list|()
block|{
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|RDBBlobStoreFixture
name|candidates
index|[]
init|=
operator|new
name|RDBBlobStoreFixture
index|[]
block|{
name|RDBBlobStoreFixture
operator|.
name|RDB_DB2
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_H2
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_DERBY
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_MSSQL
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_MYSQL
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_ORACLE
block|,
name|RDBBlobStoreFixture
operator|.
name|RDB_PG
block|}
decl_stmt|;
for|for
control|(
name|RDBBlobStoreFixture
name|bsf
range|:
name|candidates
control|)
block|{
if|if
condition|(
name|bsf
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|bsf
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|RDBBlobStore
name|blobStore
decl_stmt|;
specifier|private
name|String
name|blobStoreName
decl_stmt|;
specifier|private
name|RDBDataSourceWrapper
name|dsw
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
specifier|public
name|RDBBlobStoreTest
parameter_list|(
name|RDBBlobStoreFixture
name|bsf
parameter_list|)
block|{
name|blobStore
operator|=
name|bsf
operator|.
name|createRDBBlobStore
argument_list|()
expr_stmt|;
name|blobStoreName
operator|=
name|bsf
operator|.
name|getName
argument_list|()
expr_stmt|;
name|dsw
operator|=
name|bsf
operator|.
name|getDataSource
argument_list|()
expr_stmt|;
block|}
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
operator|>
literal|256
condition|)
block|{
if|if
condition|(
name|test
operator|==
literal|0
condition|)
block|{
name|test
operator|=
name|max
expr_stmt|;
comment|// try largest first
block|}
else|else
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
block|}
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
name|digest
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
literal|"max blob length for "
operator|+
name|blobStoreName
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
name|blobStoreName
operator|+
literal|": expected supported block size is "
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
annotation|@
name|Test
specifier|public
name|void
name|testDeleteManyBlobs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// see https://issues.apache.org/jira/browse/OAK-3807
name|int
name|count
init|=
literal|3000
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|256
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
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
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
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|RDBBlobStoreFriend
operator|.
name|deleteChunks
argument_list|(
name|blobStore
argument_list|,
name|toDelete
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateAndDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|256
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
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|long
name|until
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|until
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
comment|// Force update to update timestamp
name|long
name|beforeUpdateTs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|100
decl_stmt|;
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// Metadata row should not have been touched
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"entry was cleaned although it shouldn't have"
argument_list|,
name|blobStore
operator|.
name|deleteChunks
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|id
argument_list|)
argument_list|,
name|beforeUpdateTs
argument_list|)
argument_list|)
expr_stmt|;
comment|// Actual data row should still be present
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|RDBBlobStoreFriend
operator|.
name|readBlockFromBackend
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteChunks
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|data1
init|=
operator|new
name|byte
index|[
literal|256
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
name|data1
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest1
init|=
name|getDigest
argument_list|(
name|data1
argument_list|)
decl_stmt|;
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest1
argument_list|,
literal|0
argument_list|,
name|data1
argument_list|)
expr_stmt|;
name|String
name|id1
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest1
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|until
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|10
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|until
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
name|byte
index|[]
name|data2
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest2
init|=
name|getDigest
argument_list|(
name|data2
argument_list|)
decl_stmt|;
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest2
argument_list|,
literal|0
argument_list|,
name|data2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"meta entry was not removed"
argument_list|,
literal|1
argument_list|,
name|blobStore
operator|.
name|countDeleteChunks
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|id1
argument_list|)
argument_list|,
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"data entry was not removed"
argument_list|,
name|RDBBlobStoreFriend
operator|.
name|isDataEntryPresent
argument_list|(
name|blobStore
argument_list|,
name|digest1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResilienceMissingMetaEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|test
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
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
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
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
literal|"data mismatch"
argument_list|)
throw|;
block|}
name|RDBBlobStoreFriend
operator|.
name|killMetaEntry
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|)
expr_stmt|;
comment|// retry
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data3
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
name|data3
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"data mismatch"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceptionHandling
parameter_list|()
throws|throws
name|Exception
block|{
comment|// see OAK-7068
try|try
block|{
name|int
name|test
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
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
name|dsw
operator|.
name|setTemporaryUpdateException
argument_list|(
literal|"testExceptionHandling"
argument_list|)
expr_stmt|;
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expects IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{         }
finally|finally
block|{
name|dsw
operator|.
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Test
specifier|public
name|void
name|testRDBJDBCPerfLog
parameter_list|()
throws|throws
name|Exception
block|{
name|LogCustomizer
name|logCustomizerRead
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|RDBBlobStore
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
operator|.
name|matchesRegex
argument_list|(
literal|"read: .*"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|logCustomizerRead
operator|.
name|starting
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|256
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
name|RDBBlobStoreFriend
operator|.
name|storeBlock
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|RDBBlobStoreFriend
operator|.
name|readBlockFromBackend
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|logCustomizerRead
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|digest
operator|=
name|getDigest
argument_list|(
name|data
argument_list|)
expr_stmt|;
try|try
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|RDBBlobStoreFriend
operator|.
name|readBlockFromBackend
argument_list|(
name|blobStore
argument_list|,
name|digest
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|logCustomizerRead
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|logCustomizerRead
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInsertSmallBlobs
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|size
init|=
literal|1500
decl_stmt|;
name|long
name|duration
init|=
literal|2000
decl_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|duration
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
name|int
name|errors
init|=
literal|0
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
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|end
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
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
name|digest
argument_list|,
literal|0
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|cnt
operator|+=
literal|1
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"insert failed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|errors
operator|+=
literal|1
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"inserted "
operator|+
name|cnt
operator|+
literal|" blocks of size "
operator|+
name|size
operator|+
literal|" into "
operator|+
name|blobStoreName
operator|+
literal|" ("
operator|+
name|errors
operator|+
literal|" errors) in "
operator|+
name|duration
operator|+
literal|"ms ("
operator|+
operator|(
name|cnt
operator|*
literal|1000
operator|)
operator|/
name|duration
operator|+
literal|" blocks/s)"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

