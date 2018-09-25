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
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
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
name|Sets
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
name|oak
operator|.
name|api
operator|.
name|PropertyState
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexDefinition
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
name|EmptyNodeState
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
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IOContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
import|import static
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|BufferedOakDirectory
operator|.
name|DELETE_THRESHOLD_UNTIL_REOPEN
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|BufferedOakDirectory
operator|.
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|lucene
operator|.
name|directory
operator|.
name|BufferedOakDirectory
operator|.
name|reReadCommandLineParam
import|;
end_import

begin_import
import|import static
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
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|assertFalse
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

begin_class
specifier|public
class|class
name|BufferedOakDirectoryTest
block|{
specifier|private
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|createOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|buffered
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|writeFile
argument_list|(
name|buffered
argument_list|,
literal|"file"
argument_list|)
decl_stmt|;
comment|// must not be visible yet in base
name|Directory
name|base
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|base
operator|.
name|fileExists
argument_list|(
literal|"file"
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now it must exist
name|base
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFile
argument_list|(
name|base
argument_list|,
literal|"file"
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|listAll
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|buffered
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|buffered
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
comment|// must only show up after buffered is closed
name|Directory
name|base
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|base
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
name|base
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"file"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|base
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffered
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|buffered
operator|.
name|deleteFile
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|buffered
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// must only disappear after buffered is closed
name|base
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"file"
argument_list|)
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|base
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
name|base
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|base
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|fileLength
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|base
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|base
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|base
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|buffered
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|buffered
operator|.
name|deleteFile
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
try|try
block|{
name|buffered
operator|.
name|fileLength
argument_list|(
literal|"file"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must throw FileNotFoundException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|buffered
operator|.
name|fileLength
argument_list|(
literal|"unknown"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must throw FileNotFoundException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
name|buffered
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reopen
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|42
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
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
literal|10
operator|*
name|DELETE_THRESHOLD_UNTIL_REOPEN
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"file-"
operator|+
name|i
decl_stmt|;
name|writeFile
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// keep 5%
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|names
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open unbuffered and check list as well
name|dir
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|names
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|respectSettingConfigForSingleBlobWrite
parameter_list|()
block|{
name|boolean
name|oldVal
init|=
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
decl_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Flag not setting as set by configuration"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Flag not setting as set by configuration"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|selectWriteStrategyBasedOnFlagAndMode
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|oldVal
init|=
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
decl_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|IndexOutput
name|multiBlobIndexOutput
init|=
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|multiBlobIndexOutput
operator|.
name|writeBytes
argument_list|(
name|randomBytes
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|multiBlobIndexOutput
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|PropertyState
name|jcrData
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|":data"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"multiple blobs not written"
argument_list|,
name|jcrData
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|IndexOutput
name|multiBlobIndexOutput
init|=
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|multiBlobIndexOutput
operator|.
name|writeBytes
argument_list|(
name|randomBytes
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|multiBlobIndexOutput
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|jcrData
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|":data"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"multiple blobs written"
argument_list|,
name|jcrData
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
init|)
block|{
name|IndexOutput
name|multiBlobIndexOutput
init|=
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|multiBlobIndexOutput
operator|.
name|writeBytes
argument_list|(
name|randomBytes
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|multiBlobIndexOutput
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|jcrData
operator|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|":data"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"multiple blobs not written despite disabled buffered directory"
argument_list|,
name|jcrData
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readNonStreamingWhenMultipleBlobsExist
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|oldVal
init|=
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
decl_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|IndexOutput
name|multiBlobIndexOutput
init|=
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|multiBlobIndexOutput
operator|.
name|writeBytes
argument_list|(
name|randomBytes
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|multiBlobIndexOutput
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|// Enable feature... reader shouldn't care about the flag.
comment|// Repo state needs to be used for that
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|OakIndexInput
name|multiBlobIndexInput
init|=
operator|(
name|OakIndexInput
operator|)
name|multiBlobDir
operator|.
name|openInput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OakBufferedIndexFile must be used"
argument_list|,
name|multiBlobIndexInput
operator|.
name|file
operator|instanceof
name|OakBufferedIndexFile
argument_list|)
expr_stmt|;
block|}
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readStreamingWithSingleBlob
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|oldVal
init|=
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
decl_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|IndexOutput
name|multiBlobIndexOutput
init|=
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|multiBlobIndexOutput
operator|.
name|writeBytes
argument_list|(
name|randomBytes
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|multiBlobIndexOutput
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|// Enable feature... reader shouldn't care about the flag.
comment|// Repo state needs to be used for that
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|OakIndexInput
name|multiBlobIndexInput
init|=
operator|(
name|OakIndexInput
operator|)
name|multiBlobDir
operator|.
name|openInput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OakStreamingIndexFile must be used"
argument_list|,
name|multiBlobIndexInput
operator|.
name|file
operator|instanceof
name|OakStreamingIndexFile
argument_list|)
expr_stmt|;
block|}
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writeNonStreamingIfDisabledByFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|oldVal
init|=
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
decl_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|OakIndexOutput
name|multiBlobIndexOutput
init|=
operator|(
name|OakIndexOutput
operator|)
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo1"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OakBufferedIndexFile must be used"
argument_list|,
name|multiBlobIndexOutput
operator|.
name|file
operator|instanceof
name|OakBufferedIndexFile
argument_list|)
expr_stmt|;
block|}
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
init|(
name|Directory
name|multiBlobDir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
init|)
block|{
name|OakIndexOutput
name|multiBlobIndexOutput
init|=
operator|(
name|OakIndexOutput
operator|)
name|multiBlobDir
operator|.
name|createOutput
argument_list|(
literal|"foo2"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"OakStreamingIndexFile must be used"
argument_list|,
name|multiBlobIndexOutput
operator|.
name|file
operator|instanceof
name|OakStreamingIndexFile
argument_list|)
expr_stmt|;
block|}
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|defaultValue
parameter_list|()
block|{
name|BufferedOakDirectory
name|bufferedOakDirectory
init|=
operator|(
name|BufferedOakDirectory
operator|)
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Flag not setting as set by command line flag"
argument_list|,
name|bufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|commandLineParamSetsValue
parameter_list|()
block|{
name|String
name|oldVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Flag not setting as set by command line flag"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Flag not setting as set by command line flag"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|commandLineOverridesSetter
parameter_list|()
block|{
name|String
name|oldVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Flag not setting as set by command line flag"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Flag not setting as set by command line flag"
argument_list|,
name|BufferedOakDirectory
operator|.
name|isEnableWritingSingleBlobIndexFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|settingConfigDifferentFromCLIWarns
parameter_list|()
block|{
name|String
name|oldVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|LogCustomizer
name|custom
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|BufferedOakDirectory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Ignoring configuration "
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|custom
operator|.
name|starting
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Warn on conflicting config on CLI and set method"
argument_list|,
literal|1
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|custom
operator|.
name|finished
argument_list|()
expr_stmt|;
name|custom
operator|.
name|starting
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Warn on conflicting config on CLI and set method"
argument_list|,
literal|1
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|custom
operator|.
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|dontWarnUnnecesarily
parameter_list|()
block|{
name|String
name|oldVal
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
decl_stmt|;
specifier|final
name|LogCustomizer
name|custom
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|BufferedOakDirectory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Ignoring configuration "
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|custom
operator|.
name|starting
argument_list|()
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Don't warn unnecessarily"
argument_list|,
literal|0
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Don't warn unnecessarily"
argument_list|,
literal|0
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Don't warn unnecessarily"
argument_list|,
literal|0
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|reReadCommandLineParam
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Don't warn unnecessarily"
argument_list|,
literal|0
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedOakDirectory
operator|.
name|setEnableWritingSingleBlobIndexFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Don't warn unnecessarily"
argument_list|,
literal|0
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
name|custom
operator|.
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|ENABLE_WRITING_SINGLE_BLOB_INDEX_FILE_PARAM
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|file
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|expected
operator|.
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Directory
name|createDir
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|boolean
name|buffered
parameter_list|)
block|{
name|LuceneIndexDefinition
name|def
init|=
operator|new
name|LuceneIndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/foo"
argument_list|)
decl_stmt|;
if|if
condition|(
name|buffered
condition|)
block|{
return|return
operator|new
name|BufferedOakDirectory
argument_list|(
name|builder
argument_list|,
name|INDEX_DATA_CHILD_NAME
argument_list|,
name|def
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|OakDirectory
argument_list|(
name|builder
argument_list|,
name|def
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
specifier|private
name|byte
index|[]
name|randomBytes
parameter_list|(
name|int
name|size
parameter_list|)
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
name|rnd
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|private
name|byte
index|[]
name|writeFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|randomBytes
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
call|(
name|int
call|)
argument_list|(
literal|16
operator|*
name|FileUtils
operator|.
name|ONE_KB
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

