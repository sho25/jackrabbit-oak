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
name|plugins
operator|.
name|index
operator|.
name|nodetype
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
name|ByteArrayInputStream
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
name|index
operator|.
name|CompositeIndexEditorProvider
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
name|IndexEditorProvider
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
name|IndexUpdateProvider
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
name|plugins
operator|.
name|nodetype
operator|.
name|write
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
name|query
operator|.
name|NodeStateNodeTypeInfoProvider
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
name|QueryEngineSettings
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
name|ast
operator|.
name|NodeTypeInfo
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
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|ast
operator|.
name|SelectorImpl
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
name|index
operator|.
name|FilterImpl
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
name|EditorHook
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
name|OakInitializer
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
name|mount
operator|.
name|Mounts
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
name|query
operator|.
name|Cursor
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
name|query
operator|.
name|Cursors
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

begin_comment
comment|/**  * {@code NodeTypeIndexTest} performs tests on {@link NodeTypeIndex}.  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeIndexTest
block|{
specifier|private
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// initialize node types& index definitions
name|OakInitializer
operator|.
name|initialize
argument_list|(
name|store
argument_list|,
operator|new
name|InitialContent
argument_list|()
argument_list|,
name|CompositeIndexEditorProvider
operator|.
name|compose
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|IndexEditorProvider
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeType
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// remove "rep:security" as it interferes with tests
name|root
operator|.
name|getChildNode
argument_list|(
literal|"rep:security"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// set "entryCount", so the node type index counts the nodes
comment|// and the approximation is not used
name|root
operator|.
name|getChildNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"nodetype"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|addFolder
argument_list|(
name|root
argument_list|,
literal|"folder-1"
argument_list|)
expr_stmt|;
name|addFolder
argument_list|(
name|root
argument_list|,
literal|"folder-2"
argument_list|)
expr_stmt|;
name|addFile
argument_list|(
name|root
argument_list|,
literal|"file-1"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|root
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeState
name|rootState
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeTypeIndex
name|index
init|=
operator|new
name|NodeTypeIndex
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
decl_stmt|;
name|filter
operator|=
name|createFilter
argument_list|(
name|rootState
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6.0
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|checkCursor
argument_list|(
name|index
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|"/folder-1"
argument_list|,
literal|"/folder-2"
argument_list|)
expr_stmt|;
name|filter
operator|=
name|createFilter
argument_list|(
name|rootState
argument_list|,
name|JcrConstants
operator|.
name|NT_FILE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5.0
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|checkCursor
argument_list|(
name|index
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|"/file-1"
argument_list|)
expr_stmt|;
name|filter
operator|=
name|createFilter
argument_list|(
name|rootState
argument_list|,
name|JcrConstants
operator|.
name|NT_HIERARCHYNODE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.0
argument_list|,
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|checkCursor
argument_list|(
name|index
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
argument_list|,
literal|"/folder-1"
argument_list|,
literal|"/folder-2"
argument_list|,
literal|"/file-1"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|FilterImpl
name|createFilter
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeTypeInfoProvider
name|nodeTypes
init|=
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeTypeInfo
name|type
init|=
name|nodeTypes
operator|.
name|getNodeTypeInfo
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|checkCursor
parameter_list|(
name|Cursor
name|cursor
parameter_list|,
name|String
modifier|...
name|matches
parameter_list|)
block|{
comment|// make sure the index is actually used
comment|// and does not traverse
name|assertEquals
argument_list|(
name|Cursors
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"$PathCursor"
argument_list|,
name|cursor
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|expected
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|matches
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|actual
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|cursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|addFolder
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|addChild
argument_list|(
name|node
argument_list|,
name|name
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|)
return|;
block|}
specifier|private
name|NodeBuilder
name|addFile
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|NodeBuilder
name|file
init|=
name|addChild
argument_list|(
name|node
argument_list|,
name|name
argument_list|,
name|JcrConstants
operator|.
name|NT_FILE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|content
init|=
name|addChild
argument_list|(
name|file
argument_list|,
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|,
name|JcrConstants
operator|.
name|NT_RESOURCE
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|Blob
name|blob
init|=
name|store
operator|.
name|createBlob
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"Apache Oak"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|content
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|,
name|blob
argument_list|)
expr_stmt|;
return|return
name|file
return|;
block|}
specifier|private
specifier|static
name|NodeBuilder
name|addChild
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|nodeType
parameter_list|)
block|{
return|return
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|nodeType
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
block|}
end_class

end_unit

