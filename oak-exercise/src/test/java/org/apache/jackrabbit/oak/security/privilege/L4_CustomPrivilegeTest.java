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
name|security
operator|.
name|Principal
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|Function
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

begin_comment
comment|/**  *<pre>  * Module: Privilege Management  * =============================================================================  *  * Title: Custom Privileges  * -----------------------------------------------------------------------------  *  * Goal:  * The aim of this exercise is to make you familiar with the API to register  * custom privileges and provide you with the basic understanding on how those  * are being evaluated and enforced.  *  * Exercises:  *  * - {@link #testCustomPrivilege()}  *   Use this test to verify the nature of the aggregated custom privilege  *   registered in the setup.  *  * - {@link #testJcrAll()}  *   Verify that our custom privileges properly listed in the aggregated privileges  *   exposed by jcr:all.  *  *   Question: Having completed this test, what can you say about the nature of jcr:all?  *   Question: What does that mean for the internal representation of jcr:all?  *  * - {@link #testHasPrivilege()}  *   Fix the test case such that the given set of test princials is granted the  *   custom privileges at the test node.  *  *   Question: Would it also work if you would grant jcr:all for any of the test principals?  *  *  * Advanced Exercises:  * -----------------------------------------------------------------------------  *  * As you can see in the permission evaluation implementation custom privileges  * will NOT be enforced upon read/write to the repository as the nature of the  * custom privilege is by definition known to the application responsible for  * the registration and therefore must be evaluated/enforced on the application  * level as well.  *  * - Verify this by taking another look at the Oak internal permission evaluation.  *  *   Question: Can you identify which parts in Oak would need to be extended and  *             which interfaces you would need to implement and plug/replace if  *             you wanted to enforce your custom privileges in the repository?  *  *  * Related Exercises:  * -----------------------------------------------------------------------------  *  * - {@link org.apache.jackrabbit.oak.security.privilege.L6_JcrAllTest}  *  *</pre>  *  * @see org.apache.jackrabbit.api.security.authorization.PrivilegeManager#registerPrivilege(String, boolean, String[])  */
end_comment

begin_class
specifier|public
class|class
name|L4_CustomPrivilegeTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
specifier|private
name|Privilege
name|customAbstractPriv
decl_stmt|;
specifier|private
name|Privilege
name|customAggrPriv
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
name|privilegeManager
operator|=
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|customAbstractPriv
operator|=
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"customAbstractPriv_"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|customAggrPriv
operator|=
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"customAbstractPriv_"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
name|customAbstractPriv
operator|.
name|getName
argument_list|()
block|,
name|PrivilegeConstants
operator|.
name|JCR_READ
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertEqualPrivileges
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|expectedNames
parameter_list|,
name|Privilege
index|[]
name|result
parameter_list|)
block|{
if|if
condition|(
name|expectedNames
operator|.
name|size
argument_list|()
operator|!=
name|result
operator|.
name|length
condition|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
name|Iterable
argument_list|<
name|String
argument_list|>
name|resultNames
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|result
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|Privilege
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Privilege
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Iterables
operator|.
name|removeAll
argument_list|(
name|resultNames
argument_list|,
name|expectedNames
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|resultNames
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomPrivilege
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
literal|null
decl_stmt|;
comment|//EXERCISE
name|assertEqualPrivileges
argument_list|(
name|expected
argument_list|,
name|customAggrPriv
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
argument_list|)
expr_stmt|;
name|expected
operator|=
literal|null
expr_stmt|;
comment|// EXERCISE
name|assertEqualPrivileges
argument_list|(
name|expected
argument_list|,
name|customAggrPriv
operator|.
name|getAggregatePrivileges
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|expectedIsAbstract
init|=
literal|null
decl_stmt|;
comment|// EXERCISE
name|assertEquals
argument_list|(
name|expectedIsAbstract
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|customAggrPriv
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrAll
parameter_list|()
block|{
comment|// EXERCISE : verify that the custom privileges are contained in the jcr:all aggregation
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHasPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
comment|// EXERCISE : fix the test case such that the test principals have the specified privileges granted at "/"
name|Privilege
index|[]
name|testPrivileges
init|=
operator|new
name|Privilege
index|[]
block|{
name|customAbstractPriv
block|,
name|customAggrPriv
block|}
decl_stmt|;
name|Set
argument_list|<
name|Principal
argument_list|>
name|testPrincipals
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|hasPrivilege
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
operator|.
name|hasPrivileges
argument_list|(
literal|"/"
argument_list|,
name|testPrincipals
argument_list|,
name|testPrivileges
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hasPrivilege
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// EXERCISE: cleanup the changes.
block|}
block|}
block|}
end_class

end_unit

