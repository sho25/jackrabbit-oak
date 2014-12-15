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
name|segment
operator|.
name|file
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|deleteDirectory
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentNodeStore
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

begin_class
specifier|public
class|class
name|CompactionEstimatorTest
block|{
specifier|private
name|File
name|directory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"FileStoreTest"
argument_list|,
literal|"dir"
argument_list|,
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
expr_stmt|;
name|directory
operator|.
name|delete
argument_list|()
expr_stmt|;
name|directory
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|cleanDir
parameter_list|()
throws|throws
name|IOException
block|{
name|deleteDirectory
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGainEstimator
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|blobSize
init|=
literal|2
operator|*
name|MB
decl_stmt|;
name|FileStore
name|fileStore
init|=
operator|new
name|FileStore
argument_list|(
name|directory
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentNodeStore
name|nodeStore
init|=
operator|new
name|SegmentNodeStore
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
comment|// 1. Create some blob properties
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
name|NodeBuilder
name|c1
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
decl_stmt|;
name|c1
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c1
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|c2
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
decl_stmt|;
name|c2
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c2
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|c3
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"c3"
argument_list|)
decl_stmt|;
name|c3
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|createBlob
argument_list|(
name|nodeStore
argument_list|,
name|blobSize
argument_list|)
argument_list|)
expr_stmt|;
name|c3
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
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
comment|// 2. Now remove the property
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|remove
argument_list|()
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
name|fileStore
operator|.
name|flush
argument_list|()
expr_stmt|;
try|try
block|{
comment|// should be at 66%
name|assertTrue
argument_list|(
name|fileStore
operator|.
name|estimateCompactionGain
argument_list|()
operator|.
name|estimateCompactionGain
argument_list|()
operator|>
literal|60
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fileStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Blob
name|createBlob
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
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
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|nodeStore
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

