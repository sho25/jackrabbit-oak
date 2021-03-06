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
name|migration
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileSystems
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|CommitFailedException
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
name|PropertyBuilder
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
name|split
operator|.
name|DefaultSplitBlobStore
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
name|NodeState
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
name|Before
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMigratorTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|LENGTH
init|=
literal|1024
operator|*
literal|16
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
name|File
name|repository
decl_stmt|;
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|BlobStore
name|newBlobStore
decl_stmt|;
specifier|private
name|BlobMigrator
name|migrator
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|CommitFailedException
throws|,
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|Path
name|target
init|=
name|FileSystems
operator|.
name|getDefault
argument_list|()
operator|.
name|getPath
argument_list|(
literal|"target"
argument_list|)
decl_stmt|;
name|repository
operator|=
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
operator|.
name|createTempDirectory
argument_list|(
name|target
argument_list|,
literal|"migrate-"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|BlobStore
name|oldBlobStore
init|=
name|createOldBlobStore
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|NodeStore
name|originalNodeStore
init|=
name|createNodeStore
argument_list|(
name|oldBlobStore
argument_list|,
name|repository
argument_list|)
decl_stmt|;
name|createContent
argument_list|(
name|originalNodeStore
argument_list|)
expr_stmt|;
name|closeNodeStore
argument_list|()
expr_stmt|;
name|newBlobStore
operator|=
name|createNewBlobStore
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|DefaultSplitBlobStore
name|splitBlobStore
init|=
operator|new
name|DefaultSplitBlobStore
argument_list|(
name|repository
operator|.
name|getPath
argument_list|()
argument_list|,
name|oldBlobStore
argument_list|,
name|newBlobStore
argument_list|)
decl_stmt|;
name|nodeStore
operator|=
name|createNodeStore
argument_list|(
name|splitBlobStore
argument_list|,
name|repository
argument_list|)
expr_stmt|;
name|migrator
operator|=
operator|new
name|BlobMigrator
argument_list|(
name|splitBlobStore
argument_list|,
name|nodeStore
argument_list|)
expr_stmt|;
comment|// see OAK-6066
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
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
block|}
specifier|protected
specifier|abstract
name|NodeStore
name|createNodeStore
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|File
name|repository
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|closeNodeStore
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|BlobStore
name|createOldBlobStore
parameter_list|(
name|File
name|repository
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|BlobStore
name|createNewBlobStore
parameter_list|(
name|File
name|repository
parameter_list|)
function_decl|;
annotation|@
name|After
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|IOException
block|{
name|closeNodeStore
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|repository
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|blobsExistsOnTheNewBlobStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|migrator
operator|.
name|migrate
argument_list|()
expr_stmt|;
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertPropertyOnTheNewStore
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|blobsCanBeReadAfterSwitchingBlobStore
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|migrator
operator|.
name|migrate
argument_list|()
expr_stmt|;
name|closeNodeStore
argument_list|()
expr_stmt|;
name|nodeStore
operator|=
name|createNodeStore
argument_list|(
name|newBlobStore
argument_list|,
name|repository
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertPropertyExists
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertPropertyExists
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|Blob
name|blob
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
control|)
block|{
name|assertEquals
argument_list|(
name|LENGTH
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|LENGTH
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertPropertyOnTheNewStore
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|Blob
name|blob
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
control|)
block|{
name|assertPropertyOnTheNewStore
argument_list|(
name|blob
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertPropertyOnTheNewStore
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertPropertyOnTheNewStore
parameter_list|(
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|blobId
init|=
name|blob
operator|.
name|getContentIdentity
argument_list|()
decl_stmt|;
name|assertStreamEquals
argument_list|(
name|blob
operator|.
name|getNewStream
argument_list|()
argument_list|,
name|newBlobStore
operator|.
name|getInputStream
argument_list|(
name|blobId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|createContent
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyBuilder
argument_list|<
name|Blob
argument_list|>
name|builder
init|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
literal|"prop"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addValue
argument_list|(
name|createBlob
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addValue
argument_list|(
name|createBlob
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addValue
argument_list|(
name|createBlob
argument_list|(
name|nodeStore
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"node3"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|rootBuilder
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
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|LENGTH
index|]
decl_stmt|;
name|RANDOM
operator|.
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|ArrayBasedBlob
argument_list|(
name|buffer
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|assertStreamEquals
parameter_list|(
name|InputStream
name|expected
parameter_list|,
name|InputStream
name|actual
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|expectedByte
init|=
name|expected
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|actualByte
init|=
name|actual
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedByte
argument_list|,
name|actualByte
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedByte
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

