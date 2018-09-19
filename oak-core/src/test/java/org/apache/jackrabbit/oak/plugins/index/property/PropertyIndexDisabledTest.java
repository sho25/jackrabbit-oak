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
operator|.
name|property
package|;
end_package

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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
operator|.
name|EMPTY
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
name|assertTrue
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
name|InitialContentHelper
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
name|memory
operator|.
name|EmptyNodeState
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
name|PropertyValues
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
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * Test the Property2 index mechanism.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyIndexDisabledTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MANY
init|=
literal|100
decl_stmt|;
specifier|private
name|NodeState
name|root
decl_stmt|;
specifier|private
name|NodeBuilder
name|rootBuilder
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|EditorHook
name|HOOK
init|=
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
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
expr_stmt|;
name|rootBuilder
operator|=
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
operator|.
name|builder
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disabled
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|createIndexDefinition
argument_list|(
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"foo"
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
literal|null
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|USE_IF_EXISTS
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MANY
condition|;
name|i
operator|++
control|)
block|{
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"x"
operator|+
name|i
operator|%
literal|20
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|(
name|root
argument_list|,
name|NT_BASE
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"x10"
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyIndex
name|propertyIndex
init|=
operator|new
name|PropertyIndex
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|propertyIndex
operator|.
name|getCost
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
operator|!=
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"property foo = x10"
argument_list|,
name|propertyIndex
operator|.
name|getPlan
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
comment|// now test with a node that doesn't exist
name|index
operator|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|USE_IF_EXISTS
argument_list|,
literal|"/doesNotExist"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// need to create a new one - otherwise the cached plan is used
name|propertyIndex
operator|=
operator|new
name|PropertyIndex
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|propertyIndex
operator|.
name|getCost
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"property index not applicable"
argument_list|,
name|propertyIndex
operator|.
name|getPlan
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
comment|// test with a property that does exist
name|index
operator|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|USE_IF_EXISTS
argument_list|,
literal|"/oak:index/@jcr:primaryType"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// need to create a new one - otherwise the cached plan is used
name|propertyIndex
operator|=
operator|new
name|PropertyIndex
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|propertyIndex
operator|.
name|getCost
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
operator|!=
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"property foo = x10"
argument_list|,
name|propertyIndex
operator|.
name|getPlan
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
comment|// test with a property that does not exist
name|index
operator|=
name|rootBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|USE_IF_EXISTS
argument_list|,
literal|"/oak:index/@unknownProperty"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// need to create a new one - otherwise the cached plan is used
name|propertyIndex
operator|=
operator|new
name|PropertyIndex
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|propertyIndex
operator|.
name|getCost
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"property index not applicable"
argument_list|,
name|propertyIndex
operator|.
name|getPlan
argument_list|(
name|f
argument_list|,
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|commit
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|=
name|HOOK
operator|.
name|processCommit
argument_list|(
name|rootBuilder
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|EMPTY
argument_list|)
expr_stmt|;
name|rootBuilder
operator|=
name|root
operator|.
name|builder
argument_list|()
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
block|}
end_class

end_unit

