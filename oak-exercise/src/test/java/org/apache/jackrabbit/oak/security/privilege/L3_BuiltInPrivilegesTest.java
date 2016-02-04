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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|base
operator|.
name|Predicate
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Iterables
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlList
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
name|api
operator|.
name|security
operator|.
name|authorization
operator|.
name|PrivilegeManager
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|ContentSession
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
name|Root
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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  *<pre>  * Module: Privilege Management  * =============================================================================  *  * Title: The Built-in Privileges  * -----------------------------------------------------------------------------  *  * Goal:  * Understand what built-in privileges are provided by JCR specification and  * the Oak repository and what type of operations and items they take  * effect upon.  *  * Exercises:  *  * - Privilege Overview  *   List all privileges defined by JSR 283 and Oak and mark if they are  *   aggregated privileges (if yes: what's the aggregation). Try to understand  *   the meaning of the individual privileges.  *  * - {@link #testAggregation()}  *   For all built-in aggregated privileges defined the mapping of the name  *   to the declared aggregated privileges such that the test passes.  *  *   Question: What can you say about the nature of {@link Privilege#JCR_READ}  *   Question: Review again what JSR 283 states about {@link Privilege#JCR_ALL}  *  * - {@link #testMapItems()}  *   This allows you to become familiar with the mapping of individual privileges  *   to items. Use the Oak API to change those items directly (instead of using  *   the corresponding JCR API call.  *   Use the JCR specification, the privilege definitions and {@link org.apache.jackrabbit.oak.spi.security.authorization.permission.Permissions}  *  *   Question: Can you map the items to the corresponding JCR API calls? (see additional exercises below)  *   Question: Can you extract the rules when a dedicated specific privilege is  *   being used instead of regular JCR write privileges?  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * - Create a new test-case similar to {@link #testMapItems()} extending from  *   {@link org.apache.jackrabbit.test.AbstractJCRTest} and verify your findings  *   by executing the corresponding JCR API calls.  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link L4_CustomPrivilegeTest}  *  *</pre>  *  * @see javax.jcr.security.Privilege  * @see org.apache.jackrabbit.api.security.authorization.PrivilegeManager  * @see javax.jcr.security.AccessControlManager#privilegeFromName(String)  * @see org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeConstants  */
end_comment

begin_class
specifier|public
class|class
name|L3_BuiltInPrivilegesTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|ContentSession
name|testSession
decl_stmt|;
specifier|private
name|Root
name|testRoot
decl_stmt|;
specifier|private
name|AccessControlManager
name|acMgr
decl_stmt|;
annotation|@
name|Override
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
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|testRoot
operator|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|acMgr
operator|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testSession
operator|!=
literal|null
condition|)
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregation
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PrivilegeManager
name|privilegeManager
init|=
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
comment|// EXERCISE: for all aggregated privileges define the mapping of the privilege name to declaredAggregates
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Privilege
argument_list|>
argument_list|>
name|expectedResults
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
comment|/* EXERCISE */
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|Privilege
argument_list|>
name|aggregated
init|=
name|Iterables
operator|.
expr|<
name|Privilege
operator|>
name|filter
argument_list|(
name|ImmutableList
operator|.
expr|<
name|Privilege
operator|>
name|copyOf
argument_list|(
name|privilegeManager
operator|.
name|getRegisteredPrivileges
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Privilege
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Privilege
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
operator|&&
name|input
operator|.
name|isAggregate
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Privilege
name|aggrPrivilege
range|:
name|aggregated
control|)
block|{
name|Set
argument_list|<
name|Privilege
argument_list|>
name|expected
init|=
name|expectedResults
operator|.
name|get
argument_list|(
name|aggrPrivilege
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|aggrPrivilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapItems
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
name|jcrAll
init|=
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
decl_stmt|;
for|for
control|(
name|Privilege
name|privilege
range|:
name|jcrAll
operator|.
name|getAggregatePrivileges
argument_list|()
control|)
block|{
try|try
block|{
comment|// EXERCISE : modify item(s) affected by the given privilege and verify that it fails
name|setupAcl
argument_list|(
name|privilege
argument_list|,
name|acMgr
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// EXERCISE : modify the same item(s) again and verify that it succeeds
block|}
finally|finally
block|{
name|clearAcl
argument_list|(
name|acMgr
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|setupAcl
parameter_list|(
name|Privilege
name|privilege
parameter_list|,
name|AccessControlManager
name|acMgr
parameter_list|)
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|Privilege
index|[]
block|{
name|privilege
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
literal|"/"
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|clearAcl
parameter_list|(
name|AccessControlManager
name|acMgr
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|policies
control|)
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
literal|"/"
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

