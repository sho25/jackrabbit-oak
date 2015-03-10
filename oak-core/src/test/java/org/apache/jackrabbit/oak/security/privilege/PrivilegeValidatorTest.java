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
name|security
operator|.
name|privilege
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
name|AbstractSecurityTest
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
name|Tree
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
name|tree
operator|.
name|TreeFactory
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
name|tree
operator|.
name|impl
operator|.
name|AbstractTree
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
name|tree
operator|.
name|impl
operator|.
name|ImmutableTree
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
name|PrivilegeBits
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
name|PrivilegeBitsProvider
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
name|NodeStateUtils
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|PrivilegeValidatorTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PrivilegeConstants
block|{
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
name|Tree
name|privilegesTree
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|bitsProvider
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|privilegesTree
operator|=
name|checkNotNull
argument_list|(
name|bitsProvider
operator|.
name|getPrivilegesTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Tree
name|createPrivilegeTree
parameter_list|()
block|{
name|Tree
name|privTree
init|=
name|privilegesTree
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|privTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|privTree
return|;
block|}
specifier|private
specifier|static
name|void
name|setPrivilegeBits
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|LONGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMissingPrivilegeBits
parameter_list|()
block|{
try|try
block|{
name|createPrivilegeTree
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Missing privilege bits property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertTrue
argument_list|(
name|e
operator|.
name|isConstraintViolation
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBitsConflict
parameter_list|()
block|{
try|try
block|{
name|Tree
name|privTree
init|=
name|createPrivilegeTree
argument_list|()
decl_stmt|;
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_READ
argument_list|)
operator|.
name|writeTo
argument_list|(
name|privTree
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Conflicting privilege bits property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"OakConstraint0049: PrivilegeBits already in used."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBitsConflictWithAggregation
parameter_list|()
block|{
try|try
block|{
name|Tree
name|privTree
init|=
name|createPrivilegeTree
argument_list|()
decl_stmt|;
name|privTree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|,
name|JCR_MODIFY_PROPERTIES
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|setPrivilegeBits
argument_list|(
name|privTree
argument_list|,
name|REP_BITS
argument_list|,
literal|340
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Privilege bits don't match the aggregation."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"OakConstraint0053: Invalid privilege bits for aggregated privilege definition."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNextNotUpdated
parameter_list|()
block|{
try|try
block|{
name|Tree
name|privTree
init|=
name|createPrivilegeTree
argument_list|()
decl_stmt|;
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|privilegesTree
argument_list|)
operator|.
name|writeTo
argument_list|(
name|privTree
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Outdated rep:next property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"OakConstraint0043: Next bits not updated"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChangeNext
parameter_list|()
block|{
try|try
block|{
name|setPrivilegeBits
argument_list|(
name|bitsProvider
operator|.
name|getPrivilegesTree
argument_list|()
argument_list|,
name|REP_NEXT
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Outdated rep:next property must be detected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"OakConstraint0043: Next bits not updated"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingularAggregation
parameter_list|()
block|{
try|try
block|{
name|Tree
name|privTree
init|=
name|createPrivilegeTree
argument_list|()
decl_stmt|;
name|privTree
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|REP_AGGREGATES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|JCR_READ
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|PrivilegeBits
operator|.
name|getInstance
argument_list|(
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
operator|.
name|writeTo
argument_list|(
name|privTree
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Aggregation of a single privilege is invalid."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
name|assertEquals
argument_list|(
literal|"OakConstraint0050: Singular aggregation is equivalent to existing privilege."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2413">OAK-2413</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeChangedWithChanges
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeState
name|privilegeDefinition
init|=
name|nb
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|NT_REP_PRIVILEGE
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|privilegeDefinition
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PrivilegeValidator
name|pv
init|=
operator|new
name|PrivilegeValidator
argument_list|(
name|root
argument_list|,
name|root
argument_list|)
decl_stmt|;
try|try
block|{
name|pv
operator|.
name|childNodeChanged
argument_list|(
literal|"test"
argument_list|,
name|privilegeDefinition
argument_list|,
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|41
argument_list|,
name|e
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2413">OAK-2413</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testChildNodeChangedWithoutChanges
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|nb
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|NodeState
name|privilegeDefinition
init|=
name|nb
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|NT_REP_PRIVILEGE
operator|.
name|equals
argument_list|(
name|NodeStateUtils
operator|.
name|getPrimaryTypeName
argument_list|(
name|privilegeDefinition
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PrivilegeValidator
name|pv
init|=
operator|new
name|PrivilegeValidator
argument_list|(
name|root
argument_list|,
name|root
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|pv
operator|.
name|childNodeChanged
argument_list|(
literal|"test"
argument_list|,
name|privilegeDefinition
argument_list|,
name|privilegeDefinition
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

