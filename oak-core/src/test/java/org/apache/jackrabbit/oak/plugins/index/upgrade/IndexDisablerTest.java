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
name|upgrade
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
name|ImmutableSet
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|DISABLE_INDEXES_ON_NEXT_CYCLE
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
name|INDEX_DEFINITIONS_NAME
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
name|TYPE_DISABLED
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
name|TYPE_PROPERTY_NAME
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
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsInAnyOrder
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|IndexDisablerTest
block|{
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|IndexDisabler
name|disabler
init|=
operator|new
name|IndexDisabler
argument_list|(
name|rootBuilder
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|simpleIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disableIndexes
parameter_list|()
throws|throws
name|Exception
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|disabledIndexes
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TYPE_DISABLED
argument_list|,
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|//Check no node created for non existing node
name|assertFalse
argument_list|(
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"barIndex"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes2
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes2
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|refreshBuilder
parameter_list|()
block|{
name|builder
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test that indexes are not disabled in same cycle      * as when reindexing is done      */
annotation|@
name|Test
specifier|public
name|void
name|reindexCase
parameter_list|()
throws|throws
name|Exception
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexDisabling_noop
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@bar"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexDisabling_noDeclaringTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@bar"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexDisabling_typeNotExist
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"fooIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"oak:TestNode"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@oak:BarType"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|disabledIndexes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexDisabling_typeExist
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"fooIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"oak:TestNode"
argument_list|,
literal|"oak:BarType"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@oak:BarType"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|refreshBuilder
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|disabledIndexes
init|=
name|disabler
operator|.
name|disableOldIndexes
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|disabledIndexes
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/oak:index/fooIndex/@oak:BarType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|IndexConstants
operator|.
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|declaringNodeType
init|=
name|rootBuilder
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|getProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|,
name|declaringNodeType
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|declaringNodeType
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|names
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"oak:TestNode"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~-------------------------------< anyIndexToBeDisabled>
annotation|@
name|Test
specifier|public
name|void
name|indexToBeDisabled_Noop
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexToBeDisabled_PathNotExists
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexToBeDisabled_PathExistsButDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE_DISABLED
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexToBeDisabled_PathExists
parameter_list|()
throws|throws
name|Exception
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
literal|"property"
argument_list|)
expr_stmt|;
name|recreateDisabler
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexToBeDisabled_PathNotExists
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@bar"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexToBeDisabled_DeclaringTypeNotExists
parameter_list|()
throws|throws
name|Exception
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
expr_stmt|;
name|recreateDisabler
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@bar"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexToBeDisabled_TypeNotExists
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"fooIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"oak:TestNode"
argument_list|)
argument_list|)
expr_stmt|;
name|recreateDisabler
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@bar"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeTypeIndexToBeDisabled_TypeExists
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndexDefinition
argument_list|(
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"fooIndex"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"oak:TestNode"
argument_list|)
argument_list|)
expr_stmt|;
name|recreateDisabler
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|SUPERSEDED_INDEX_PATHS
argument_list|,
name|asList
argument_list|(
literal|"/oak:index/fooIndex/@oak:TestNode"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disabler
operator|.
name|markDisableFlagIfRequired
argument_list|(
literal|"/oak:index/foo"
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|getBoolean
argument_list|(
name|DISABLE_INDEXES_ON_NEXT_CYCLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|recreateDisabler
parameter_list|()
block|{
name|disabler
operator|=
operator|new
name|IndexDisabler
argument_list|(
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

