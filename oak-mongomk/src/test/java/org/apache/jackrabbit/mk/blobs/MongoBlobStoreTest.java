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
name|mk
operator|.
name|blobs
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
name|FileOutputStream
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|mongomk
operator|.
name|impl
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
name|mongomk
operator|.
name|impl
operator|.
name|blob
operator|.
name|MongoBlobStore
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
name|AfterClass
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * Tests the {@link MongoBlobStore} implementation. It should really extend from  * AbstractBlobStore but it cannot due to classpath issues, so instead AbstractBlobStore  * tests are copied here as well.  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlobStoreTest
comment|/*extends AbstractBlobStoreTest*/
block|{
specifier|protected
name|AbstractBlobStore
name|store
decl_stmt|;
specifier|private
specifier|static
name|DB
name|db
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|createDefaultMongoConnection
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dropCollections
argument_list|()
expr_stmt|;
name|MongoBlobStore
name|blobStore
init|=
operator|new
name|MongoBlobStore
argument_list|(
name|db
argument_list|)
decl_stmt|;
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
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|dropCollections
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|db
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteFile
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|setBlockSize
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|4
operator|*
literal|1024
operator|*
literal|1024
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
name|String
name|tempFileName
init|=
literal|"target/temp/test"
decl_stmt|;
name|File
name|tempFile
init|=
operator|new
name|File
argument_list|(
name|tempFileName
argument_list|)
decl_stmt|;
name|tempFile
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|s
init|=
name|store
operator|.
name|writeBlob
argument_list|(
name|tempFileName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buff
init|=
operator|new
name|byte
index|[
literal|1
index|]
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
name|data
operator|.
name|length
condition|;
name|i
operator|+=
literal|1024
control|)
block|{
name|store
operator|.
name|readBlob
argument_list|(
name|s
argument_list|,
name|i
argument_list|,
name|buff
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
index|[
name|i
index|]
argument_list|,
name|buff
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|store
operator|.
name|writeBlob
argument_list|(
name|tempFileName
operator|+
literal|"_wrong"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCombinedIdentifier
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
name|store
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|2
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|combinedId
init|=
name|id
operator|+
name|id
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
name|combinedId
argument_list|)
argument_list|)
expr_stmt|;
name|doTestRead
argument_list|(
operator|new
name|byte
index|[
literal|4
index|]
argument_list|,
literal|4
argument_list|,
name|combinedId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyIdentifier
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
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|store
operator|.
name|readBlob
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCloseStream
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|InputStream
argument_list|()
block|{
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
decl_stmt|;
name|store
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|closed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceptionWhileReading
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|InputStream
argument_list|()
block|{
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"abc"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|store
operator|.
name|writeBlob
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|msg
operator|.
name|indexOf
argument_list|(
literal|"abc"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|closed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIllegalIdentifier
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
literal|1
index|]
decl_stmt|;
try|try
block|{
name|store
operator|.
name|readBlob
argument_list|(
literal|"ff"
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|store
operator|.
name|getBlobLength
argument_list|(
literal|"ff"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|store
operator|.
name|mark
argument_list|(
literal|"ff"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSmall
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|10
argument_list|,
literal|300
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMedium
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|1000
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
comment|// FIXME - GC is not implemented in MongoBlobStore yet.
specifier|public
name|void
name|testGarbageCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|mem
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|count
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|*=
literal|10
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|i
index|]
decl_stmt|;
name|String
name|id
decl_stmt|;
name|id
operator|=
name|store
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
comment|// copy the id so the string is not in the weak hash map
name|map
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|id
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|i
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|id
operator|=
name|store
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
comment|// copy the id so the string is not in the weak hash map
name|map
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|id
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|mem
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|startMark
argument_list|()
expr_stmt|;
name|store
operator|.
name|sweep
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|test
init|=
name|readFully
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|,
name|test
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mem
operator|.
name|clear
argument_list|()
expr_stmt|;
name|store
operator|.
name|clearInUse
argument_list|()
expr_stmt|;
name|store
operator|.
name|startMark
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|d
init|=
name|map
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
index|[
literal|0
index|]
operator|!=
literal|0
condition|)
block|{
continue|continue;
block|}
name|store
operator|.
name|mark
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|store
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|store
operator|.
name|clearInUse
argument_list|()
expr_stmt|;
name|store
operator|.
name|clearCache
argument_list|()
expr_stmt|;
comment|// https://issues.apache.org/jira/browse/OAK-60
comment|// endure there is at least one old entry (with age 1 ms)
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|store
operator|.
name|startMark
argument_list|()
expr_stmt|;
name|count
operator|=
name|store
operator|.
name|sweep
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"count: "
operator|+
name|count
argument_list|,
name|count
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|failedCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|long
name|length
init|=
name|store
operator|.
name|getBlobLength
argument_list|(
name|id
argument_list|)
decl_stmt|;
try|try
block|{
name|readFully
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|id
operator|+
literal|":"
operator|+
name|length
argument_list|,
name|length
operator|>
name|store
operator|.
name|getBlockSizeMin
argument_list|()
argument_list|)
expr_stmt|;
name|failedCount
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"failedCount: "
operator|+
name|failedCount
argument_list|,
name|failedCount
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTest
parameter_list|(
name|int
name|maxLength
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|count
operator|*
literal|2
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|maxLength
argument_list|)
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|s
index|[
name|i
operator|++
index|]
operator|=
name|store
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|s
index|[
name|i
operator|++
index|]
operator|=
name|store
operator|.
name|writeBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|setSeed
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
control|)
block|{
name|int
name|expectedLen
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|maxLength
argument_list|)
decl_stmt|;
name|byte
index|[]
name|expectedData
init|=
operator|new
name|byte
index|[
name|expectedLen
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|expectedData
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedLen
argument_list|,
name|store
operator|.
name|getBlobLength
argument_list|(
name|s
index|[
name|i
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|s
index|[
name|i
operator|++
index|]
decl_stmt|;
name|doTestRead
argument_list|(
name|expectedData
argument_list|,
name|expectedLen
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doTestRead
parameter_list|(
name|byte
index|[]
name|expected
parameter_list|,
name|int
name|expectedLen
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|got
init|=
name|readFully
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedLen
argument_list|,
name|got
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|got
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|got
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|got
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|byte
index|[]
name|readFully
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|store
operator|.
name|getBlobLength
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|100
condition|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|store
operator|.
name|readBlob
argument_list|(
name|id
argument_list|,
name|i
argument_list|,
name|data
argument_list|,
name|i
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|data
operator|=
name|BlobStoreInputStream
operator|.
name|readFully
argument_list|(
name|store
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|len
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|private
specifier|static
name|void
name|createDefaultMongoConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
name|MongoBlobStoreTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/config.cfg"
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|String
name|host
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"host"
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"port"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|database
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"db"
argument_list|)
decl_stmt|;
name|MongoConnection
name|mongoConnection
init|=
operator|new
name|MongoConnection
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|database
argument_list|)
decl_stmt|;
name|db
operator|=
name|mongoConnection
operator|.
name|getDB
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|dropCollections
parameter_list|()
block|{
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|db
operator|.
name|getCollection
argument_list|(
name|MongoBlobStore
operator|.
name|COLLECTION_BLOBS
argument_list|)
operator|.
name|drop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

