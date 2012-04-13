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
name|kernel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|simple
operator|.
name|SimpleKernelImpl
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
name|Scalar
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
name|TransientNodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import static
name|junit
operator|.
name|framework
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|KernelNodeStateEditorTest
block|{
specifier|private
specifier|final
name|MicroKernel
name|microkernel
init|=
operator|new
name|SimpleKernelImpl
argument_list|(
literal|"mem:"
argument_list|)
decl_stmt|;
specifier|private
name|KernelNodeState
name|state
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|String
name|jsop
init|=
literal|"+\"test\":{\"a\":1,\"b\":2,\"c\":3,"
operator|+
literal|"\"x\":{},\"y\":{},\"z\":{}}"
decl_stmt|;
name|String
name|revision
init|=
name|microkernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|microkernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|"test data"
argument_list|)
decl_stmt|;
name|state
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|microkernel
argument_list|,
literal|"/test"
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNode
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|TransientNodeState
name|childState
init|=
name|transientState
operator|.
name|getChildNode
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|childState
argument_list|)
expr_stmt|;
name|childState
operator|=
name|transientState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|childState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getProperty
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|PropertyState
name|propertyState
init|=
name|transientState
operator|.
name|getProperty
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
name|propertyState
operator|=
name|transientState
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|propertyState
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Scalar
operator|.
name|Type
operator|.
name|LONG
argument_list|,
name|propertyState
operator|.
name|getScalar
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|propertyState
operator|.
name|getScalar
argument_list|()
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNodes
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|TransientNodeState
argument_list|>
name|nodes
init|=
name|transientState
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|expectedPaths
argument_list|,
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
for|for
control|(
name|TransientNodeState
name|node
range|:
name|nodes
control|)
block|{
name|assertTrue
argument_list|(
name|expectedPaths
operator|.
name|remove
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedPaths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getProperties
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Scalar
argument_list|>
name|expectedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Scalar
argument_list|>
argument_list|()
decl_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|ScalarImpl
operator|.
name|longScalar
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|ScalarImpl
operator|.
name|longScalar
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
name|ScalarImpl
operator|.
name|longScalar
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|properties
init|=
name|transientState
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|properties
control|)
block|{
name|Scalar
name|value
init|=
name|expectedProperties
operator|.
name|remove
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|property
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|property
operator|.
name|getScalar
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedProperties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addNode
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|TransientNodeState
name|newNode
init|=
name|editor
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|newNode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
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
name|addExistingNode
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|TransientNodeState
name|newNode
init|=
name|editor
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|editor
operator|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|transientState
operator|=
name|editor
operator|.
name|getTransientState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|newNode
operator|=
name|editor
operator|.
name|addNode
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|newNode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNode
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|removeNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setProperty
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasProperty
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|Scalar
name|value
init|=
name|ScalarImpl
operator|.
name|stringScalar
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|editor
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
name|transientState
operator|.
name|getProperty
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|property
operator|.
name|getScalar
argument_list|()
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|property
operator|=
name|newState
operator|.
name|getProperty
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|property
operator|.
name|getScalar
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeProperty
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|newState
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|move
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|TransientNodeState
name|y
init|=
name|transientState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|move
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rename
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|move
argument_list|(
literal|"x"
argument_list|,
literal|"xx"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|TransientNodeState
name|y
init|=
name|transientState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|copy
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|transientState
operator|.
name|hasNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deepCopy
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|TransientNodeState
name|y
init|=
name|transientState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|editor
operator|.
name|edit
argument_list|(
literal|"x"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"x1"
argument_list|)
expr_stmt|;
name|editor
operator|.
name|copy
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|hasNode
argument_list|(
literal|"x1"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"x1"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|x
init|=
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|NodeState
name|xx
init|=
name|newState
operator|.
name|getChildNode
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|x
argument_list|,
name|xx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChildNodeCount
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|removeNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|transientState
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|addNode
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|transientState
operator|.
name|getChildNodeCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPropertyCount
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|TransientNodeState
name|transientState
init|=
name|editor
operator|.
name|getTransientState
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|Scalar
name|value
init|=
name|ScalarImpl
operator|.
name|stringScalar
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|editor
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|transientState
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|largeChildNodeList
parameter_list|()
block|{
name|KernelNodeStateEditor
name|editor
init|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|editor
operator|.
name|addNode
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
name|editor
operator|=
name|editor
operator|.
name|edit
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
literal|10000
condition|;
name|c
operator|++
control|)
block|{
name|editor
operator|.
name|addNode
argument_list|(
literal|"n"
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
name|KernelNodeState
name|newState
init|=
name|editor
operator|.
name|mergeInto
argument_list|(
name|microkernel
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|editor
operator|=
operator|new
name|KernelNodeStateEditor
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|editor
operator|=
name|editor
operator|.
name|edit
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TransientNodeState
name|q
range|:
name|editor
operator|.
name|getTransientState
argument_list|()
operator|.
name|getChildNodes
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"n"
operator|+
name|c
operator|++
argument_list|,
name|q
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

