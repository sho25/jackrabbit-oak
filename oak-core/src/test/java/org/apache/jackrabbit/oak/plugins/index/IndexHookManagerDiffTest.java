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
name|Sets
operator|.
name|difference
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
name|JCR_PRIMARYTYPE
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
operator|.
name|EMPTY_NODE
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
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|p2
operator|.
name|Property2IndexHookProvider
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
name|NodeStateDiff
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
name|IndexHookManagerDiffTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIndexDefinitions
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// this index is on the current update branch, it should be seen by the
comment|// diff
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"existing"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
comment|// this index is NOT the current update branch, it should NOT be seen by
comment|// the diff
name|builder
operator|.
name|child
argument_list|(
literal|"newchild"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"existing2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// Add index definition
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"other"
argument_list|)
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"index2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
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
name|IndexHookProvider
name|provider
init|=
operator|new
name|CompositeIndexHookProvider
argument_list|(
operator|new
name|Property2IndexHookProvider
argument_list|()
argument_list|)
decl_stmt|;
comment|//<type,<path, indexhook>>
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|>
name|updates
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|IndexHook
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|NodeStateDiff
name|diff
init|=
operator|new
name|IndexHookManagerDiff
argument_list|(
name|provider
argument_list|,
name|builder
argument_list|,
name|updates
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|updates
operator|.
name|keySet
argument_list|()
control|)
block|{
for|for
control|(
name|List
argument_list|<
name|IndexHook
argument_list|>
name|hooks
range|:
name|updates
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|IndexHook
name|hook
range|:
name|hooks
control|)
block|{
name|hook
operator|.
name|apply
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|newHashSet
argument_list|(
literal|"/"
argument_list|,
literal|"/test/other"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|found
init|=
name|updates
operator|.
name|remove
argument_list|(
literal|"p2"
argument_list|)
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expecting "
operator|+
name|expected
operator|+
literal|" got "
operator|+
name|found
argument_list|,
name|difference
argument_list|(
name|found
argument_list|,
name|expected
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|updates
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|indexed
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
comment|// check that the index content nodes exist
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
literal|"oak:index"
argument_list|,
literal|"existing"
argument_list|,
literal|":index"
argument_list|)
expr_stmt|;
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
literal|"test"
argument_list|,
literal|"other"
argument_list|,
literal|"oak:index"
argument_list|,
literal|"index2"
argument_list|,
literal|":index"
argument_list|)
expr_stmt|;
name|NodeState
name|ignoredIndex
init|=
name|checkPathExists
argument_list|(
name|indexed
argument_list|,
literal|"newchild"
argument_list|,
literal|"other"
argument_list|,
literal|"oak:index"
argument_list|,
literal|"existing2"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|ignoredIndex
operator|.
name|hasChildNode
argument_list|(
literal|":index"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|checkPathExists
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
modifier|...
name|verify
parameter_list|)
block|{
name|NodeState
name|c
init|=
name|state
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|verify
control|)
block|{
name|assertTrue
argument_list|(
name|c
operator|.
name|hasChildNode
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|=
name|c
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

