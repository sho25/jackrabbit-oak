begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
operator|.
name|file
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|System
operator|.
name|getProperty
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
name|DefaultSegmentWriterBuilder
operator|.
name|defaultSegmentWriterBuilder
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
name|DefaultSegmentWriter
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
name|RecordId
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
name|SegmentNodeBuilder
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
name|SegmentNodeState
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
name|tar
operator|.
name|GCGeneration
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

begin_comment
comment|/**  * This test asserts that a large number of child nodes can be added in a single  * transaction. Due to its long running time the test needs to be explicitly enabled  * via {@code -Dtest=LargeNumberOfChildNodeUpdatesIT}.  * Used {@code -DLargeNumberOfChildNodeUpdatesIT.child-count=<int>} to control the number  * of child nodes used by this test. Default is 5000000.  */
end_comment

begin_class
specifier|public
class|class
name|LargeNumberOfChildNodeUpdatesIT
block|{
comment|/** Only run if explicitly asked to via -Dtest=LargeNumberOfChildNodeUpdatesIT */
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|LargeNumberOfChildNodeUpdatesIT
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|.
name|equals
argument_list|(
name|getProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"LargeNumberOfChildNodeUpdatesIT.child-count"
argument_list|,
literal|5000000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
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
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
name|assumeTrue
argument_list|(
name|ENABLED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNode
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidFileStoreVersionException
block|{
try|try
init|(
name|FileStore
name|fileStore
init|=
name|FileStoreBuilder
operator|.
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
init|)
block|{
name|DefaultSegmentWriter
name|writer
init|=
name|defaultSegmentWriterBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|withGeneration
argument_list|(
name|GCGeneration
operator|.
name|newGCGeneration
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|build
argument_list|(
name|fileStore
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|root
init|=
name|fileStore
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|NODE_COUNT
condition|;
name|k
operator|++
control|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"n-"
operator|+
name|k
argument_list|)
expr_stmt|;
block|}
name|SegmentNodeState
name|node1
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|RecordId
name|nodeId
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|node1
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|node2
init|=
name|fileStore
operator|.
name|getReader
argument_list|()
operator|.
name|readNode
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|node1
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|node2
operator|.
name|getRecordId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

