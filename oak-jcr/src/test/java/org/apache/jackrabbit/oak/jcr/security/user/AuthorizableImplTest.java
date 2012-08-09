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
name|jcr
operator|.
name|security
operator|.
name|user
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Group
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
name|UserManager
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
name|user
operator|.
name|UserConstants
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
name|NotExecutableException
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
name|value
operator|.
name|StringValue
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
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RangeIterator
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
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

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
name|ArrayList
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * AuthorizableImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|AuthorizableImplTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|protectedUserProps
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|protectedGroupProps
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|protectedUserProps
operator|.
name|add
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
expr_stmt|;
name|protectedUserProps
operator|.
name|add
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
expr_stmt|;
name|protectedUserProps
operator|.
name|add
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
expr_stmt|;
name|protectedUserProps
operator|.
name|add
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
name|protectedGroupProps
operator|.
name|add
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkProtected
parameter_list|(
name|Property
name|prop
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|assertTrue
argument_list|(
name|prop
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAdmin
parameter_list|()
block|{
name|String
name|adminID
init|=
name|superuser
operator|.
name|getUserID
argument_list|()
decl_stmt|;
try|try
block|{
name|Authorizable
name|admin
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminID
argument_list|)
decl_stmt|;
name|admin
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"The admin user cannot be removed."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// OK superuser cannot be removed. not even by the superuser itself.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetSpecialProperties
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|Value
name|v
init|=
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"any_value"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pName
range|:
name|protectedUserProps
control|)
block|{
try|try
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|pName
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"changing the '"
operator|+
name|pName
operator|+
literal|"' property on a User should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
for|for
control|(
name|String
name|pName
range|:
name|protectedGroupProps
control|)
block|{
try|try
block|{
name|group
operator|.
name|setProperty
argument_list|(
name|pName
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"changing the '"
operator|+
name|pName
operator|+
literal|"' property on a Group should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveSpecialProperties
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
for|for
control|(
name|String
name|pName
range|:
name|protectedUserProps
control|)
block|{
try|try
block|{
name|user
operator|.
name|removeProperty
argument_list|(
name|pName
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing the '"
operator|+
name|pName
operator|+
literal|"' property on a User should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
for|for
control|(
name|String
name|pName
range|:
name|protectedGroupProps
control|)
block|{
try|try
block|{
name|group
operator|.
name|removeProperty
argument_list|(
name|pName
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing the '"
operator|+
name|pName
operator|+
literal|"' property on a Group should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProtectedUserProperties
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|UserImpl
name|user
init|=
operator|(
name|UserImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|user
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|checkProtected
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|checkProtected
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
condition|)
block|{
name|checkProtected
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProtectedGroupProperties
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|Node
name|n
init|=
operator|(
operator|(
name|GroupImpl
operator|)
name|group
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|checkProtected
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
condition|)
block|{
name|checkProtected
argument_list|(
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMembersPropertyType
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|Node
name|n
init|=
operator|(
operator|(
name|GroupImpl
operator|)
name|group
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
condition|)
block|{
name|group
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|(
name|superuser
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Property
name|p
init|=
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|p
operator|.
name|getValues
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|,
name|v
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMemberOfRangeIterator
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|Authorizable
name|auth
init|=
literal|null
decl_stmt|;
name|Group
name|group
init|=
literal|null
decl_stmt|;
try|try
block|{
name|auth
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|getTestPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|group
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|getTestPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|auth
operator|.
name|declaredMemberOf
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|instanceof
name|RangeIterator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|RangeIterator
operator|)
name|groups
operator|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|groups
operator|=
name|auth
operator|.
name|memberOf
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|instanceof
name|RangeIterator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|RangeIterator
operator|)
name|groups
operator|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|.
name|addMember
argument_list|(
name|auth
argument_list|)
expr_stmt|;
name|groups
operator|=
name|auth
operator|.
name|declaredMemberOf
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|instanceof
name|RangeIterator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|RangeIterator
operator|)
name|groups
operator|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|groups
operator|=
name|auth
operator|.
name|memberOf
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|instanceof
name|RangeIterator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|RangeIterator
operator|)
name|groups
operator|)
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|auth
operator|!=
literal|null
condition|)
block|{
name|auth
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetSpecialPropertiesDirectly
parameter_list|()
throws|throws
name|NotExecutableException
throws|,
name|RepositoryException
block|{
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|user
operator|.
name|getNode
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|pName
init|=
name|user
operator|.
name|getPrincipalName
argument_list|()
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"any-value"
argument_list|)
argument_list|)
expr_stmt|;
comment|// should have failed => change value back.
name|n
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
operator|new
name|StringValue
argument_list|(
name|pName
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to change protected property rep:principalName should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
try|try
block|{
name|String
name|imperson
init|=
literal|"anyimpersonator"
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_IMPERSONATORS
argument_list|,
operator|new
name|Value
index|[]
block|{
operator|new
name|StringValue
argument_list|(
name|imperson
argument_list|)
block|}
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to change protected property rep:impersonators should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveSpecialUserPropertiesDirectly
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|AuthorizableImpl
name|g
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|g
operator|.
name|getNode
argument_list|()
decl_stmt|;
try|try
block|{
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to remove protected property rep:password should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
try|try
block|{
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to remove protected property rep:principalName should fail."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveSpecialGroupPropertiesDirectly
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Node
name|n
init|=
operator|(
operator|(
name|GroupImpl
operator|)
name|group
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
condition|)
block|{
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to remove protected property rep:principalName should fail."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
try|try
block|{
if|if
condition|(
name|n
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
condition|)
block|{
name|n
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Attempt to remove protected property rep:members should fail."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserGetProperties
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|user
operator|.
name|getNode
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyIterator
name|it
init|=
name|n
operator|.
name|getProperties
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Property
name|p
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|user
operator|.
name|getProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// authorizable defined property
name|assertTrue
argument_list|(
name|user
operator|.
name|hasProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|user
operator|.
name|getProperty
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupGetProperties
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Node
name|n
init|=
operator|(
operator|(
name|GroupImpl
operator|)
name|group
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyIterator
name|it
init|=
name|n
operator|.
name|getProperties
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Property
name|prop
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|prop
operator|.
name|getDefinition
argument_list|()
operator|.
name|isProtected
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
name|group
operator|.
name|hasProperty
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|group
operator|.
name|getProperty
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// authorizable defined property
name|assertTrue
argument_list|(
name|group
operator|.
name|hasProperty
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|group
operator|.
name|getProperty
argument_list|(
name|prop
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleToMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
try|try
block|{
name|Value
name|v
init|=
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"anyValue"
argument_list|)
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Value
index|[]
name|vs
init|=
operator|new
name|Value
index|[]
block|{
name|v
block|,
name|v
block|}
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|vs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|.
name|removeProperty
argument_list|(
literal|"someProp"
argument_list|)
operator|&&
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiValuedToSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
try|try
block|{
name|Value
name|v
init|=
name|superuser
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"anyValue"
argument_list|)
decl_stmt|;
name|Value
index|[]
name|vs
init|=
operator|new
name|Value
index|[]
block|{
name|v
block|,
name|v
block|}
decl_stmt|;
name|user
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|vs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|user
operator|.
name|setProperty
argument_list|(
literal|"someProp"
argument_list|,
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|user
operator|.
name|removeProperty
argument_list|(
literal|"someProp"
argument_list|)
operator|&&
operator|!
name|uMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testObjectMethods
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|AuthorizableImpl
name|user2
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|hashCode
argument_list|()
argument_list|,
name|user2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|s
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|add
argument_list|(
name|user2
argument_list|)
argument_list|)
expr_stmt|;
name|Authorizable
name|user3
init|=
operator|new
name|Authorizable
argument_list|()
block|{
specifier|public
name|String
name|getID
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getID
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
name|user
operator|.
name|isGroup
argument_list|()
return|;
block|}
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|declaredMemberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|declaredMemberOf
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|memberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|memberOf
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPropertyNames
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPropertyNames
argument_list|(
name|relPath
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|equals
argument_list|(
name|user3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|add
argument_list|(
name|user3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPath
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthorizableImpl
name|user
init|=
operator|(
name|AuthorizableImpl
operator|)
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|user
operator|.
name|getNode
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|,
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedRepositoryOperationException
name|e
parameter_list|)
block|{
comment|// ok.
block|}
block|}
block|}
end_class

end_unit

