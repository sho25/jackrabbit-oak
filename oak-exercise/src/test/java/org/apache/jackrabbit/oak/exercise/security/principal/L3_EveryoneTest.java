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
name|exercise
operator|.
name|security
operator|.
name|principal
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|JackrabbitSession
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
name|principal
operator|.
name|PrincipalIterator
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
name|principal
operator|.
name|PrincipalManager
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
name|user
operator|.
name|Authorizable
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
name|exercise
operator|.
name|security
operator|.
name|user
operator|.
name|L3_UserVsPrincipalTest
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
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  *<pre>  * Module: Principal Management  * =============================================================================  *  * Title: Everyone Test  * -----------------------------------------------------------------------------  *  * Goal:  * Understand the role of the {@link org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal}  *  * Exercises:  *  * - {@link #testEveryoneExists()}  *   Test to illustrate the that everyone principal always exists and always is  *   an instanceof {@link java.security.acl.Group} even if there is no corresponding  *   authorizable.  *   Discuss the meaning of the everyone principal and why having a corresponding authorizable is optional.  *   Note the difference between java.security.acl.Group and org.apache.jackrabbit.api.security.user.Group.  *  * - {@link #testEveryoneName()}  *   Test to illustrate that the name of the everyone principal is constant.  *   Complete the test case by typing the expected name.  *  * - {@link #testAccessByName()}  *   Even though there exists a dedicated method to retrieve the everyone principal  *   you can equally access it by name.  *   Use the principal management API to retrieve the everyone principal by name.  *   Discuss the drawback of this approach in an environment where you don't have access to the Oak constants.  *  * - {@link #testEveryoneIsMemberofEveryone()}  *   Test case illustrating the dynamic nature of the everyone principal.  *   Walk through the test  *   Try to extend the test such that the default principal management exposes additional user|group principals.  *  * - {@link #testEveryoneAsAuthorizableGroup()}  *   Additional test illustrating that the dynamic nature of the everyone principal  *   does not change if there exists a corresponding authorizable group.  *> Create a new authorizable that corresponds to the everyone principal (Q: what parameters are constants?)  *> Verify that principal exposed by the authorizable corresponds to the everyone principal.  *> Assert that the dynamic nature of the principal has not changed.  *> Test if the dynamic nature also applies to the authorizable  *  *  * Additional Exercises:  * -----------------------------------------------------------------------------  *  * The following exercises can easily be performed in a Sling based repository  * installation (e.g. Granite|CQ) with the same setup as in this test class.  *  * - Test if there exists an everyone authorizable group.  *   If this is the case try what happens if you remove that authorizable and discuss the consequences.  *   Question: Can you explain why it exists?  *  *</pre>  *  * @see L3_UserVsPrincipalTest  */
end_comment

begin_class
specifier|public
class|class
name|L3_EveryoneTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|principalManager
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getPrincipalManager
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testEveryoneExists
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Principal
name|everyone
init|=
name|principalManager
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|everyone
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyone
operator|instanceof
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
argument_list|)
expr_stmt|;
name|Authorizable
name|everyoneAuthorizable
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|superuser
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|everyone
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|everyoneAuthorizable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEveryoneName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Principal
name|everyone
init|=
name|principalManager
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|String
name|expectedName
init|=
literal|null
decl_stmt|;
comment|// EXERCISE type the expected authorizable name using constants defined by oak.
name|assertEquals
argument_list|(
name|expectedName
argument_list|,
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAccessByName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Principal
name|everyone
init|=
name|principalManager
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|principalManager
operator|.
name|hasPrincipal
argument_list|(
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Principal
name|everyoneByName
init|=
literal|null
decl_stmt|;
comment|// EXERCISE: retrieve the everyone principal by name
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|everyoneByName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEveryoneIsMemberofEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
name|everyone
init|=
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|principalManager
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|PrincipalIterator
name|it
init|=
name|principalManager
operator|.
name|getPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
comment|// EXERCISE: discuss the dynamic nature of the everyone group principal
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|principal
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|everyone
operator|.
name|equals
argument_list|(
name|principal
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|everyone
operator|.
name|isMember
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|everyone
operator|.
name|isMember
argument_list|(
name|principal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testEveryoneAsAuthorizableGroup
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// EXERCISE: create an authorizable that corresponds to the everyone principal.
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
name|user
operator|.
name|Group
name|everyoneAuthorizable
init|=
literal|null
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
name|everyone
init|=
operator|(
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
operator|)
name|principalManager
operator|.
name|getEveryone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|everyoneAuthorizable
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
comment|// EXERCISE: verify that the everyone principal is still a dynamic group
comment|// EXERCISE: test if the dyanmic nature also applies to the authorizable
block|}
finally|finally
block|{
if|if
condition|(
name|everyoneAuthorizable
operator|!=
literal|null
condition|)
block|{
name|everyoneAuthorizable
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
