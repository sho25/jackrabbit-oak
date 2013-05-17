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
name|nodetype
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
name|easymock
operator|.
name|EasyMock
operator|.
name|createControl
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|EasyMock
operator|.
name|replay
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test for OAK-695.  */
end_comment

begin_class
specifier|public
class|class
name|TypeEditorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|ignoreHidden
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
decl_stmt|;
name|NodeState
name|root
init|=
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|EMPTY_NODE
argument_list|)
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
literal|":hidden"
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
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|before
operator|=
name|after
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|":hidden"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|after
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|before
operator|=
name|after
expr_stmt|;
name|builder
operator|.
name|removeChildNode
argument_list|(
literal|":hidden"
argument_list|)
expr_stmt|;
name|after
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNonMandatoryProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|EffectiveType
name|effective
init|=
name|createControl
argument_list|()
operator|.
name|createMock
argument_list|(
name|EffectiveType
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|isMandatoryProperty
argument_list|(
literal|"mandatory"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|effective
argument_list|)
expr_stmt|;
name|TypeEditor
name|editor
init|=
operator|new
name|TypeEditor
argument_list|(
name|EMPTY_NODE
argument_list|,
name|effective
argument_list|,
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|editor
operator|.
name|propertyDeleted
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"mandatory"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|removeMandatoryProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|EffectiveType
name|effective
init|=
name|createControl
argument_list|()
operator|.
name|createMock
argument_list|(
name|EffectiveType
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|isMandatoryProperty
argument_list|(
literal|"mandatory"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|constraintViolation
argument_list|(
literal|22
argument_list|,
literal|"/"
argument_list|,
literal|"Mandatory property mandatory can not be removed"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|new
name|CommitFailedException
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|effective
argument_list|)
expr_stmt|;
name|TypeEditor
name|editor
init|=
operator|new
name|TypeEditor
argument_list|(
name|EMPTY_NODE
argument_list|,
name|effective
argument_list|,
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|editor
operator|.
name|propertyDeleted
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"mandatory"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeNonMandatoryChildNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|EffectiveType
name|effective
init|=
name|createControl
argument_list|()
operator|.
name|createMock
argument_list|(
name|EffectiveType
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|isMandatoryChildNode
argument_list|(
literal|"mandatory"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|effective
argument_list|)
expr_stmt|;
name|TypeEditor
name|editor
init|=
operator|new
name|TypeEditor
argument_list|(
name|EMPTY_NODE
argument_list|,
name|effective
argument_list|,
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|editor
operator|.
name|childNodeDeleted
argument_list|(
literal|"mandatory"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|removeMandatoryChildNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|EffectiveType
name|effective
init|=
name|createControl
argument_list|()
operator|.
name|createMock
argument_list|(
name|EffectiveType
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|isMandatoryChildNode
argument_list|(
literal|"mandatory"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|effective
operator|.
name|constraintViolation
argument_list|(
literal|26
argument_list|,
literal|"/"
argument_list|,
literal|"Mandatory child node mandatory can not be removed"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
operator|new
name|CommitFailedException
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|effective
argument_list|)
expr_stmt|;
name|TypeEditor
name|editor
init|=
operator|new
name|TypeEditor
argument_list|(
name|EMPTY_NODE
argument_list|,
name|effective
argument_list|,
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|editor
operator|.
name|childNodeDeleted
argument_list|(
literal|"mandatory"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

