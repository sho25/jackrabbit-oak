begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|segment
operator|.
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|segment
operator|.
name|file
operator|.
name|FileStoreBuilder
operator|.
name|fileStoreBuilder
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
name|assertNotEquals
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
name|util
operator|.
name|List
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

begin_class
specifier|public
class|class
name|SegmentBufferWriterTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
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
name|FileStore
name|openFileStore
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|fileStoreBuilder
argument_list|(
name|folder
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonDirtyBuffersShouldNotBeFlushed
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|SegmentId
argument_list|>
name|before
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|openFileStore
argument_list|()
init|)
block|{
name|before
operator|=
name|newArrayList
argument_list|(
name|store
operator|.
name|getSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
name|segmentWriterBuilder
argument_list|(
literal|"t"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|SegmentId
argument_list|>
name|after
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|openFileStore
argument_list|()
init|)
block|{
name|after
operator|=
name|newArrayList
argument_list|(
name|store
operator|.
name|getSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dirtyBuffersShouldBeFlushed
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|SegmentId
argument_list|>
name|before
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|openFileStore
argument_list|()
init|)
block|{
name|before
operator|=
name|newArrayList
argument_list|(
name|store
operator|.
name|getSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"t"
argument_list|)
operator|.
name|build
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeString
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|SegmentId
argument_list|>
name|after
decl_stmt|;
try|try
init|(
name|FileStore
name|store
init|=
name|openFileStore
argument_list|()
init|)
block|{
name|after
operator|=
name|newArrayList
argument_list|(
name|store
operator|.
name|getSegmentIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotEquals
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

