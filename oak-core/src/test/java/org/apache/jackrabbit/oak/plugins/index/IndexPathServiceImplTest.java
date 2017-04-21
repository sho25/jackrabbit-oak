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
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
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
name|InitialContent
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
name|Oak
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
name|ContentRepository
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
name|Tree
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
name|index
operator|.
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|MemoryNodeStore
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
name|query
operator|.
name|AbstractQueryTest
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
name|security
operator|.
name|OpenSecurityProvider
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
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasItem
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
name|assertThat
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
name|IndexPathServiceImplTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
name|NodeStore
name|nodeStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
specifier|private
name|IndexPathService
name|indexPathService
init|=
operator|new
name|IndexPathServiceImpl
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
return|return
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeTypeIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|errorIfQueryDefinitionsNotIndexed
parameter_list|()
throws|throws
name|Exception
block|{
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|errorIfNodetypeIndexDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/nodetype"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"disabled"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexed
parameter_list|()
throws|throws
name|Exception
block|{
name|enableIndexDefinitionIndex
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|hasItem
argument_list|(
literal|"/oak:index/uuid"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|hasItem
argument_list|(
literal|"/oak:index/nodetype"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|hasItem
argument_list|(
literal|"/oak:index/reference"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexInSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|enableIndexDefinitionIndex
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|Tree
name|fooIndex
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"fooIndex"
argument_list|)
decl_stmt|;
name|fooIndex
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|fooIndex
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"disabled"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|indexPathService
operator|.
name|getIndexPaths
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|paths
argument_list|,
name|hasItem
argument_list|(
literal|"/a/b/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|enableIndexDefinitionIndex
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|nodetype
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/nodetype"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nodetype
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodetypes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodetype
operator|.
name|hasProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
condition|)
block|{
name|nodetypes
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|nodetype
operator|.
name|getProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|nodetypes
operator|.
name|add
argument_list|(
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
expr_stmt|;
name|nodetype
operator|.
name|setProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|,
name|nodetypes
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|nodetype
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

