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
name|assertEquals
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
name|plugins
operator|.
name|observation
operator|.
name|RecursingNodeStateDiff
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
name|observation
operator|.
name|SecurableNodeStateDiff
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

begin_class
specifier|public
class|class
name|SecureNodeStateDiffTest
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
name|builder
operator|.
name|child
argument_list|(
literal|"NA1"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x1"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x2"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y3"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"NA3a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y3"
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
name|testRemoveNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"-x"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"v"
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"+v"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveProperty
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"-a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddProperty
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"d"
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"+d"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeProperty
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"^c"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeNode
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA1"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"p"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"^NA1+p"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddInaccessibleChild
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"NA3a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"NA3a"
argument_list|)
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeInaccessibleChild
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"NA3a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y3"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|"^NA3^x3^NA3a-y3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveInaccessibleChild
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|base
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"NA3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x3"
argument_list|)
operator|.
name|child
argument_list|(
literal|"NA3a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
operator|new
name|AssertingNodeStateDiff
argument_list|(
name|base
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|expect
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|SecureNodeStateDiff
extends|extends
name|SecurableNodeStateDiff
block|{
specifier|protected
name|SecureNodeStateDiff
parameter_list|(
name|SecurableNodeStateDiff
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|NodeStateDiff
name|wrap
parameter_list|(
name|RecursingNodeStateDiff
name|diff
parameter_list|)
block|{
return|return
operator|new
name|SecureNodeStateDiff
argument_list|(
name|diff
argument_list|)
return|;
block|}
specifier|private
name|SecureNodeStateDiff
parameter_list|(
name|RecursingNodeStateDiff
name|diff
parameter_list|)
block|{
name|super
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SecurableNodeStateDiff
name|create
parameter_list|(
name|SecurableNodeStateDiff
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|new
name|SecureNodeStateDiff
argument_list|(
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canRead
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|canRead
argument_list|(
name|before
argument_list|)
operator|&&
name|canRead
argument_list|(
name|after
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|canRead
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|property
operator|==
literal|null
operator|||
name|canRead
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canRead
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|canRead
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|boolean
name|canRead
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|==
literal|null
operator|||
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"NA"
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AssertingNodeStateDiff
extends|extends
name|RecursingNodeStateDiff
block|{
specifier|private
specifier|final
name|StringBuilder
name|actual
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
specifier|public
name|AssertingNodeStateDiff
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
block|}
specifier|public
name|void
name|expect
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|SecureNodeStateDiff
operator|.
name|wrap
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
operator|.
name|append
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|actual
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

