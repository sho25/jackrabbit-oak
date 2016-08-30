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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
operator|.
name|UTF_8
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
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|ByteArrayOutputStream
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
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|PrintStream
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
name|Iterables
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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|FileFilterUtils
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
name|lang3
operator|.
name|StringEscapeUtils
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
name|FileIOUtils
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
name|DataStoreBlobStore
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
name|OakFileDataStore
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
name|segment
operator|.
name|SegmentBlob
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
name|segment
operator|.
name|SegmentNodeStoreBuilders
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeStore
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
comment|/**  * Tests for {@link DataStoreCheckCommand}  */
end_comment

begin_class
specifier|public
class|class
name|DataStoreCheckTest
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
name|DataStoreCheckTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
specifier|private
name|String
name|storePath
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|blobsAdded
decl_stmt|;
specifier|private
name|String
name|cfgFilePath
decl_stmt|;
specifier|private
name|String
name|dsPath
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|OakFileDataStore
name|delegate
init|=
operator|new
name|OakFileDataStore
argument_list|()
decl_stmt|;
name|dsPath
operator|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|setPath
argument_list|(
name|dsPath
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DataStoreBlobStore
name|blobStore
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|delegate
argument_list|)
decl_stmt|;
name|File
name|storeFile
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|storePath
operator|=
name|storeFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|FileStore
name|fileStore
init|=
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
argument_list|(
name|storeFile
argument_list|)
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|256
argument_list|)
operator|.
name|withSegmentCacheSize
argument_list|(
literal|64
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|fileStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* Create nodes with blobs stored in DS*/
name|NodeBuilder
name|a
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|numBlobs
init|=
literal|10
decl_stmt|;
name|blobsAdded
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
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
name|numBlobs
condition|;
name|i
operator|++
control|)
block|{
name|SegmentBlob
name|b
init|=
operator|(
name|SegmentBlob
operator|)
name|store
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|18342
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
name|blobStore
operator|.
name|resolveChunks
argument_list|(
name|b
operator|.
name|getBlobId
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|chunk
init|=
name|idIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|blobsAdded
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
block|}
name|a
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|a
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created blobs : {}"
argument_list|,
name|blobsAdded
argument_list|)
expr_stmt|;
name|File
name|cfgFile
init|=
name|temporaryFolder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|BufferedWriter
name|writer
init|=
name|Files
operator|.
name|newWriter
argument_list|(
name|cfgFile
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|FileIOUtils
operator|.
name|writeAsLine
argument_list|(
name|writer
argument_list|,
literal|"path=\""
operator|+
name|StringEscapeUtils
operator|.
name|escapeJava
argument_list|(
name|dsPath
argument_list|)
operator|+
literal|"\""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|cfgFilePath
operator|=
name|cfgFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
name|blobStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|FileDescriptor
operator|.
name|err
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCorrect
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dump
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|testAllParams
argument_list|(
name|dump
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConsistency
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dump
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
comment|// Delete a random blob from datastore
name|OakFileDataStore
name|delegate
init|=
operator|new
name|OakFileDataStore
argument_list|()
decl_stmt|;
name|delegate
operator|.
name|setPath
argument_list|(
name|dsPath
argument_list|)
expr_stmt|;
name|delegate
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DataStoreBlobStore
name|blobStore
init|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|delegate
argument_list|)
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|String
name|deletedBlobId
init|=
name|Iterables
operator|.
name|get
argument_list|(
name|blobsAdded
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|blobsAdded
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|blobsAdded
operator|.
name|remove
argument_list|(
name|deletedBlobId
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|blobStore
operator|.
name|countDeleteChunks
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|deletedBlobId
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|testAllParams
argument_list|(
name|dump
argument_list|)
expr_stmt|;
name|assertFileEquals
argument_list|(
name|dump
argument_list|,
literal|"[id]"
argument_list|,
name|blobsAdded
argument_list|)
expr_stmt|;
name|assertFileEquals
argument_list|(
name|dump
argument_list|,
literal|"[ref]"
argument_list|,
name|Sets
operator|.
name|union
argument_list|(
name|blobsAdded
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|deletedBlobId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFileEquals
argument_list|(
name|dump
argument_list|,
literal|"[consistency]"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|deletedBlobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAllParams
parameter_list|(
name|File
name|dump
parameter_list|)
throws|throws
name|Exception
block|{
name|DataStoreCheckCommand
name|checkCommand
init|=
operator|new
name|DataStoreCheckCommand
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"--id"
argument_list|,
literal|"--ref"
argument_list|,
literal|"--consistency"
argument_list|,
literal|"--fds"
argument_list|,
name|cfgFilePath
argument_list|,
literal|"--store"
argument_list|,
name|storePath
argument_list|,
literal|"--dump"
argument_list|,
name|dump
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|checkCommand
operator|.
name|execute
argument_list|(
name|argsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMissingOpParams
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dump
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"--fds"
argument_list|,
name|cfgFilePath
argument_list|,
literal|"--store"
argument_list|,
name|storePath
argument_list|,
literal|"--dump"
argument_list|,
name|dump
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Running testMissinOpParams: {}"
argument_list|,
name|argsList
argument_list|)
expr_stmt|;
name|testIncorrectParams
argument_list|(
name|argsList
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Missing required option(s)"
argument_list|,
literal|"'id'"
argument_list|,
literal|"'ref'"
argument_list|,
literal|"'consistency'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTarNoDS
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dump
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"--id"
argument_list|,
literal|"--ref"
argument_list|,
literal|"--consistency"
argument_list|,
literal|"--store"
argument_list|,
name|storePath
argument_list|,
literal|"--dump"
argument_list|,
name|dump
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|testIncorrectParams
argument_list|(
name|argsList
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Operation not defined for SegmentNodeStore without external datastore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOpNoStore
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dump
init|=
name|temporaryFolder
operator|.
name|newFolder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|argsList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"--consistency"
argument_list|,
literal|"--fds"
argument_list|,
name|cfgFilePath
argument_list|,
literal|"--dump"
argument_list|,
name|dump
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|testIncorrectParams
argument_list|(
name|argsList
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Missing required option(s) ['store']"
argument_list|)
argument_list|)
expr_stmt|;
name|argsList
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"--ref"
argument_list|,
literal|"--fds"
argument_list|,
name|cfgFilePath
argument_list|,
literal|"--dump"
argument_list|,
name|dump
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|testIncorrectParams
argument_list|(
name|argsList
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"Missing required option(s) ['store']"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|testIncorrectParams
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|argList
parameter_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
name|assertMsg
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|buffer
argument_list|,
literal|true
argument_list|,
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DataStoreCheckCommand
name|checkCommand
init|=
operator|new
name|DataStoreCheckCommand
argument_list|()
decl_stmt|;
name|checkCommand
operator|.
name|execute
argument_list|(
name|argList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|message
init|=
name|buffer
operator|.
name|toString
argument_list|(
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Assert message: {}"
argument_list|,
name|assertMsg
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Message logged in System.err: {}"
argument_list|,
name|message
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|msg
range|:
name|assertMsg
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|FileDescriptor
operator|.
name|err
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertFileEquals
parameter_list|(
name|File
name|dump
parameter_list|,
name|String
name|prefix
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|blobsAdded
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|files
index|[]
init|=
name|FileFilterUtils
operator|.
name|filter
argument_list|(
name|FileFilterUtils
operator|.
name|prefixFileFilter
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|dump
operator|.
name|listFiles
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|files
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|files
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|files
index|[
literal|0
index|]
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blobsAdded
argument_list|,
name|FileIOUtils
operator|.
name|readStringsAsSet
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|files
index|[
literal|0
index|]
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|static
name|InputStream
name|randomStream
parameter_list|(
name|int
name|seed
parameter_list|,
name|int
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

