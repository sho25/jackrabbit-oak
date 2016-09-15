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
operator|.
name|hybrid
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|IndexCopier
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
name|IndexDefinition
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
name|TestUtil
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
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
name|assertNotNull
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
name|assertNull
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
name|NRTIndexFactoryTest
block|{
annotation|@
name|Rule
specifier|public
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
specifier|private
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
name|NRTIndexFactory
name|indexFactory
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
name|indexCopier
operator|=
operator|new
name|IndexCopier
argument_list|(
name|sameThreadExecutor
argument_list|()
argument_list|,
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|indexFactory
operator|=
operator|new
name|NRTIndexFactory
argument_list|(
name|indexCopier
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noIndexForAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
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
decl_stmt|;
name|assertNull
argument_list|(
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idx1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexCreationAndCloser
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idx1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|NRTIndex
name|idx2
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|idx1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|NRTIndex
name|idx3
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//With 2 generation open the first one should be closed
name|assertTrue
argument_list|(
name|idx1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|closeIndexOnClose
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinition
name|idxDefn
init|=
name|getSyncIndexDefinition
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx1
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|NRTIndex
name|idx2
init|=
name|indexFactory
operator|.
name|createIndex
argument_list|(
name|idxDefn
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|indexFactory
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|indexFactory
operator|.
name|getIndexes
argument_list|(
literal|"/foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|idx1
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|idx2
operator|.
name|isClosed
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|IndexDefinition
name|getSyncIndexDefinition
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|INDEX_PATH
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enableNRTIndexing
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
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
return|;
block|}
block|}
end_class

end_unit

