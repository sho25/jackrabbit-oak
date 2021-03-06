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
name|reference
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Operator
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
name|spi
operator|.
name|mount
operator|.
name|MountInfoProvider
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
name|Filter
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
name|QueryIndex
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
name|ExpectedException
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
name|ImmutableList
operator|.
name|of
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_UUID
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
name|NT_BASE
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
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
name|memory
operator|.
name|PropertyValues
operator|.
name|newReference
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
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|ReferenceIndexTest
block|{
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basicReferenceHandling
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u2"
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|(
name|indexed
argument_list|,
name|NT_BASE
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"*"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newReference
argument_list|(
literal|"u1"
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|assertFilter
argument_list|(
name|f
argument_list|,
operator|new
name|ReferenceIndex
argument_list|()
argument_list|,
name|indexed
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|FilterImpl
name|f2
init|=
name|createFilter
argument_list|(
name|indexed
argument_list|,
name|NT_BASE
argument_list|)
decl_stmt|;
name|f2
operator|.
name|restrictProperty
argument_list|(
literal|"*"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newReference
argument_list|(
literal|"u2"
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|)
expr_stmt|;
name|assertFilter
argument_list|(
name|f2
argument_list|,
operator|new
name|ReferenceIndex
argument_list|()
argument_list|,
name|indexed
argument_list|,
name|of
argument_list|(
literal|"/c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|referenceHandlingWithMounts
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"d"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u2"
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"foo"
argument_list|,
literal|"/a"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|mip
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ReferenceIndex
name|referenceIndex
init|=
operator|new
name|ReferenceIndex
argument_list|(
name|mip
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|(
name|indexed
argument_list|,
name|NT_BASE
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"*"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newReference
argument_list|(
literal|"u1"
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
comment|// System.out.println(NodeStateUtils.toString(NodeStateUtils.getNode(indexed, "/oak:index/reference")));
name|assertFilter
argument_list|(
name|f
argument_list|,
name|referenceIndex
argument_list|,
name|indexed
argument_list|,
name|of
argument_list|(
literal|"/a/x"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|FilterImpl
name|f2
init|=
name|createFilter
argument_list|(
name|indexed
argument_list|,
name|NT_BASE
argument_list|)
decl_stmt|;
name|f2
operator|.
name|restrictProperty
argument_list|(
literal|"*"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newReference
argument_list|(
literal|"u1"
argument_list|)
argument_list|,
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|)
expr_stmt|;
name|assertFilter
argument_list|(
name|f2
argument_list|,
name|referenceIndex
argument_list|,
name|indexed
argument_list|,
name|of
argument_list|(
literal|"/c"
argument_list|,
literal|"/a/y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeReferencedNode
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|JCR_UUID
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|CommitFailedException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"OakIntegrity0001: Unable to delete referenced node"
argument_list|)
expr_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeWeaklyReferencedNode
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|JCR_UUID
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|WEAKREFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|builder
operator|=
name|indexed
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|indexed
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Duplicates"
argument_list|)
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
name|List
argument_list|<
name|String
argument_list|>
name|assertFilter
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|QueryIndex
name|queryIndex
parameter_list|,
name|NodeState
name|indexed
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|)
block|{
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|newArrayList
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
name|paths
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
name|Collections
operator|.
name|sort
argument_list|(
name|paths
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|expected
control|)
block|{
name|assertTrue
argument_list|(
literal|"Expected path "
operator|+
name|p
operator|+
literal|" not found"
argument_list|,
name|paths
operator|.
name|contains
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Result set size is different \nExpected: "
operator|+
name|expected
operator|+
literal|"\nActual: "
operator|+
name|paths
argument_list|,
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|paths
return|;
block|}
block|}
end_class

end_unit

