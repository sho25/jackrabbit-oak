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
name|index
operator|.
name|lucene
package|;
end_package

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
name|api
operator|.
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
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
name|JcrConstants
operator|.
name|JCR_DATA
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|OakDirectory
operator|.
name|PROP_BLOB_SIZE
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|OakDirectoryTest
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
name|INITIAL_CONTENT
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
name|int
name|fileSize
init|=
name|IndexDefinition
operator|.
name|DEFAULT_BLOB_SIZE
operator|*
literal|2
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|writes_DefaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertWrites
argument_list|(
name|dir
argument_list|,
name|IndexDefinition
operator|.
name|DEFAULT_BLOB_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|writes_CustomBlobSize
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|BLOB_SIZE
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertWrites
argument_list|(
name|dir
argument_list|,
literal|300
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompatibility
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|BLOB_SIZE
argument_list|,
name|OakDirectory
operator|.
name|DEFAULT_BLOB_SIZE
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|assertWrites
argument_list|(
name|dir
argument_list|,
name|OakDirectory
operator|.
name|DEFAULT_BLOB_SIZE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|testNode
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|//Remove the size property to simulate old behaviour
name|testNode
operator|.
name|removeProperty
argument_list|(
name|PROP_BLOB_SIZE
argument_list|)
expr_stmt|;
comment|//Read should still work even if the size property is removed
name|IndexInput
name|i
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|i
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|i
operator|.
name|readBytes
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|//OAK-2388
specifier|public
name|void
name|testOverflow
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|NodeBuilder
name|file
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"test.txt"
argument_list|)
decl_stmt|;
name|int
name|blobSize
init|=
literal|32768
decl_stmt|;
name|int
name|dataSize
init|=
literal|90844
decl_stmt|;
name|file
operator|.
name|setProperty
argument_list|(
name|OakDirectory
operator|.
name|PROP_BLOB_SIZE
argument_list|,
name|blobSize
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
super|super
name|Blob
argument_list|>
name|blobs
init|=
operator|new
name|ArrayList
argument_list|<
name|Blob
argument_list|>
argument_list|(
name|dataSize
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
name|dataSize
condition|;
name|i
operator|++
control|)
block|{
name|blobs
operator|.
name|add
argument_list|(
operator|new
name|ArrayBasedBlob
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|file
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:data"
argument_list|,
name|blobs
argument_list|,
name|Type
operator|.
name|BINARIES
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test.txt"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|long
operator|)
name|blobSize
operator|*
operator|(
name|dataSize
operator|-
literal|1
operator|)
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|saveListing
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|SAVE_DIR_LISTING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fileNames
init|=
name|newHashSet
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
literal|"foo"
operator|+
name|i
decl_stmt|;
name|createFile
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|fileNames
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
name|createDir
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileNames
argument_list|,
name|newHashSet
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|assertWrites
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|blobSize
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
name|fileSize
argument_list|)
decl_stmt|;
name|IndexOutput
name|o
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|o
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
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexInput
name|i
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fileSize
argument_list|,
name|i
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|fileSize
index|]
decl_stmt|;
name|i
operator|.
name|readBytes
argument_list|(
name|result
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|equals
argument_list|(
name|data
argument_list|,
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|testNode
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobSize
argument_list|,
name|testNode
operator|.
name|getProperty
argument_list|(
name|PROP_BLOB_SIZE
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|newArrayList
argument_list|(
name|testNode
operator|.
name|getProperty
argument_list|(
name|JCR_DATA
argument_list|)
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobSize
argument_list|,
name|blobs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|private
name|int
name|createFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|+
literal|1
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|randomBytes
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|IndexOutput
name|o
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|o
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
name|o
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|size
return|;
block|}
specifier|private
name|Directory
name|createDir
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|boolean
name|readOnly
parameter_list|)
block|{
return|return
operator|new
name|OakDirectory
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
argument_list|,
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|,
name|readOnly
argument_list|)
return|;
block|}
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
block|}
end_class

end_unit

