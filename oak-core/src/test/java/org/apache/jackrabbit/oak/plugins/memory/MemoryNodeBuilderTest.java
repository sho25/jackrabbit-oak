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
name|memory
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|spi
operator|.
name|state
operator|.
name|AbstractNodeState
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
name|ChildNodeEntry
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
name|Ignore
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
name|MemoryNodeBuilderTest
block|{
specifier|private
name|NodeState
name|base
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|base
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectOnAddProperty
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childA
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|childB
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|childA
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|childB
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|childA
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectOnUpdateProperty
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childA
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|childB
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|childB
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|childA
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|childA
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|childB
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectOnRemoveProperty
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childA
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|childB
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|childB
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|childA
operator|.
name|removeProperty
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|childA
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|childB
operator|.
name|hasProperty
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|childA
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|childA
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|childB
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectOnAddNode
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childA
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|childB
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|childA
operator|.
name|hasChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|childB
operator|.
name|hasChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|childB
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|childA
operator|.
name|hasChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|childB
operator|.
name|hasChildNode
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadOnRemoveNode
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
operator|new
name|String
index|[]
block|{
literal|"x"
block|,
literal|"new"
block|}
control|)
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|child
init|=
name|root
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|child
operator|.
name|getChildNodeCount
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|root
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|child
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// reconnect!
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteOnRemoveNode
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
operator|new
name|String
index|[]
block|{
literal|"x"
block|,
literal|"new"
block|}
control|)
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|child
init|=
name|root
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
try|try
block|{
name|child
operator|.
name|setProperty
argument_list|(
literal|"q"
argument_list|,
literal|"w"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|root
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|child
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// reconnect!
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddRemovedNodeAgain
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
operator|.
name|removeNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|x
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|x
operator|.
name|child
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|x
operator|.
name|hasChildNode
argument_list|(
literal|"q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReset
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|child
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|child
operator|.
name|child
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|child
operator|.
name|hasChildNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|reset
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|child
operator|.
name|hasChildNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReset2
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|x
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|x
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|root
operator|.
name|reset
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|hasChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|x
operator|.
name|hasChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnmodifiedEqualsBase
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|x
init|=
name|root
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|x
operator|.
name|getBaseState
argument_list|()
argument_list|,
name|x
operator|.
name|getNodeState
argument_list|()
argument_list|)
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
name|testReadOnRemovedNode
parameter_list|()
block|{
name|NodeBuilder
name|root
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|m
init|=
name|root
operator|.
name|child
argument_list|(
literal|"m"
argument_list|)
decl_stmt|;
name|NodeBuilder
name|n
init|=
name|m
operator|.
name|child
argument_list|(
literal|"n"
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeNode
argument_list|(
literal|"m"
argument_list|)
expr_stmt|;
name|n
operator|.
name|hasChildNode
argument_list|(
literal|"any"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setNodeTest
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|setNode
argument_list|(
literal|"a"
argument_list|,
name|createBC
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|b
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"c2"
argument_list|,
literal|"c2Value"
argument_list|)
expr_stmt|;
name|b
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|hasProperty
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|assertion_OAK781
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setNode
argument_list|(
literal|"b"
argument_list|,
name|createBC
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|r
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|a
init|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|NodeState
name|b
init|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|NodeState
name|c
init|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// No assertion must fail in .child("c")
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|rootBuilder
operator|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|// No assertion must fail in .child("c")
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-781"
argument_list|)
specifier|public
name|void
name|modifyChildNodeOfNonExistingNode
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// +"/a":{"b":{"c":{"c":"cValue"}}} where b.exists() == false
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setNode
argument_list|(
literal|"b"
argument_list|,
name|createBC
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|r
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|a
init|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|NodeState
name|b
init|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|NodeState
name|c
init|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"c2"
argument_list|,
literal|"c2Value"
argument_list|)
expr_stmt|;
name|r
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|a
operator|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|b
operator|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|c
operator|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Node c is modified
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shadowNonExistingNode1
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// +"/a":{"b":{"c":{"c":"cValue"}}} where b.exists() == false
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setNode
argument_list|(
literal|"b"
argument_list|,
name|createBC
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|r
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|a
init|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|NodeState
name|b
init|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|NodeState
name|c
init|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"c2"
argument_list|,
literal|"c2Value"
argument_list|)
expr_stmt|;
name|r
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|a
operator|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|b
operator|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|c
operator|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// node b is shadowed by above child("b")
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// node c is shadowed by subtree b
name|assertFalse
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-781"
argument_list|)
specifier|public
name|void
name|shadowNonExistingNode2
parameter_list|()
block|{
name|NodeBuilder
name|rootBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// +"/a":{"b":{"c":{"c":"cValue"}}} where b.exists() == false
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setNode
argument_list|(
literal|"b"
argument_list|,
name|createBC
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|r
init|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeState
name|a
init|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|NodeState
name|b
init|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|NodeState
name|c
init|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|rootBuilder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"c2"
argument_list|,
literal|"c2Value"
argument_list|)
expr_stmt|;
name|r
operator|=
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|a
operator|=
name|r
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|b
operator|=
name|a
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|c
operator|=
name|b
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// node b is shadowed by above child("b")
name|assertTrue
argument_list|(
name|c
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// node c is shadowed by subtree b
name|assertFalse
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|hasProperty
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|createBC
parameter_list|(
specifier|final
name|boolean
name|exists
parameter_list|)
block|{
specifier|final
name|NodeState
name|C
init|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|"cValue"
argument_list|)
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractNodeState
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|exists
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"c"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|C
return|;
block|}
else|else
block|{
return|return
name|EmptyNodeState
operator|.
name|MISSING_NODE
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
if|if
condition|(
name|exists
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
literal|"c"
argument_list|,
name|C
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

