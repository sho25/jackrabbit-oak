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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|NT_FOLDER
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
name|MIX_REFERENCEABLE
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|principal
operator|.
name|EveryonePrincipal
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
name|privilege
operator|.
name|PrivilegeConstants
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
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
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
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|before
operator|=
name|after
expr_stmt|;
name|builder
operator|.
name|getChildNode
argument_list|(
literal|":hidden"
argument_list|)
operator|.
name|remove
argument_list|()
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
name|effective
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
name|getDirectTypeNames
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|effective
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
name|effective
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
name|getDirectTypeNames
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
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
name|effective
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
specifier|public
name|void
name|addNamedPropertyWithBadRequiredType
parameter_list|()
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
name|NodeBuilder
name|testNode
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"testNode"
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_FOLDER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"mix:title"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeNamedPropertyToBadRequiredType
parameter_list|()
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
name|NodeBuilder
name|testNode
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"testNode"
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_FOLDER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"mix:title"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"title"
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
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addMandatoryPropertyWithBadRequiredType
parameter_list|()
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
name|NodeBuilder
name|acl
init|=
name|builder
operator|.
name|child
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
decl_stmt|;
name|acl
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeBuilder
name|ace
init|=
name|acl
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
decl_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_GRANT_ACE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRIVILEGES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|55
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeMandatoryPropertyToBadRequiredType
parameter_list|()
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
name|NodeBuilder
name|acl
init|=
name|builder
operator|.
name|child
argument_list|(
name|AccessControlConstants
operator|.
name|REP_POLICY
argument_list|)
decl_stmt|;
name|acl
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeBuilder
name|ace
init|=
name|acl
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
decl_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_GRANT_ACE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setProperty
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRIVILEGES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
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
comment|// change to invalid type
name|ace
operator|.
name|setProperty
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRIVILEGES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|requiredTypeIsUndefined
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
name|setProperty
argument_list|(
literal|"any"
argument_list|,
literal|"title"
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
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"any"
argument_list|,
literal|134.34
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|after
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
name|changeNodeTypeWExtraNodes
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
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
literal|"testcontent"
argument_list|)
operator|.
name|child
argument_list|(
literal|"unstructured_child"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
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
name|root
operator|=
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
expr_stmt|;
name|builder
operator|=
name|root
operator|.
name|builder
argument_list|()
expr_stmt|;
name|before
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:folder"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|(
literal|"should not be able to change node type due to extra nodes"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeNodeTypeWExtraProps
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"extra"
argument_list|,
literal|"information"
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
name|root
operator|=
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
expr_stmt|;
name|builder
operator|=
name|root
operator|.
name|builder
argument_list|()
expr_stmt|;
name|before
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:folder"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|(
literal|"should not be able to change node type due to extra properties"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeNodeTypeNewBroken
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:folder"
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"extra"
argument_list|,
literal|"information"
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|(
literal|"should not be able to change node type due to extra properties"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|malformedUUID
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
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
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"not-a-uuid"
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
name|root
operator|=
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
expr_stmt|;
name|builder
operator|=
name|root
operator|.
name|builder
argument_list|()
expr_stmt|;
name|before
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"testcontent"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|MIX_REFERENCEABLE
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
try|try
block|{
name|hook
operator|.
name|processCommit
argument_list|(
name|before
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
name|fail
argument_list|(
literal|"should not be able to change mixin due to illegal uuid format"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

