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
name|authorization
operator|.
name|accesscontrol
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
name|ArrayList
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
name|AccessControlException
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
name|AccessControlPolicyIterator
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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|ACE
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
name|restriction
operator|.
name|Restriction
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
name|restriction
operator|.
name|RestrictionProvider
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
name|PrincipalImpl
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAccessControlTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PrivilegeConstants
block|{
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|"/testPath"
decl_stmt|;
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
name|PrincipalManager
name|principalManager
decl_stmt|;
name|ACL
name|acl
decl_stmt|;
name|Principal
name|testPrincipal
decl_stmt|;
name|Privilege
index|[]
name|testPrivileges
decl_stmt|;
annotation|@
name|Override
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
name|NodeUtil
name|rootNode
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"testPath"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testPrincipal
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|testPrivileges
operator|=
name|privilegesFromNames
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|)
expr_stmt|;
name|privilegeManager
operator|=
name|getPrivilegeManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|principalManager
operator|=
name|getPrincipalManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|acl
operator|=
name|createEmptyACL
argument_list|()
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
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
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
return|;
block|}
name|PrivilegeBitsProvider
name|getBitsProvider
parameter_list|()
block|{
return|return
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
return|;
block|}
name|List
argument_list|<
name|ACE
argument_list|>
name|createTestEntries
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|createEntry
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"testPrincipal"
operator|+
name|i
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createEntry
argument_list|(
name|principal
argument_list|,
name|privilegesFromNames
argument_list|(
name|privilegeNames
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|ACE
name|createEntry
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|PrivilegeBits
name|bits
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|AccessControlPolicyIterator
name|it
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
operator|.
name|getApplicablePolicies
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccessControlPolicy
name|policy
init|=
name|it
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|policy
operator|instanceof
name|ACL
condition|)
block|{
return|return
operator|(
operator|(
name|ACL
operator|)
name|policy
operator|)
operator|.
name|createACE
argument_list|(
name|principal
argument_list|,
name|bits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|ACE
name|createEntry
parameter_list|(
annotation|@
name|NotNull
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|ACL
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
return|return
name|acl
operator|.
name|createACE
argument_list|(
name|principal
argument_list|,
name|getBitsProvider
argument_list|()
operator|.
name|getBits
argument_list|(
name|privileges
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
name|ACL
name|createEmptyACL
parameter_list|()
block|{
return|return
name|createACL
argument_list|(
name|TEST_PATH
argument_list|,
name|Collections
operator|.
expr|<
name|ACE
operator|>
name|emptyList
argument_list|()
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|)
return|;
block|}
name|ACL
name|createACL
parameter_list|(
annotation|@
name|NotNull
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
parameter_list|)
block|{
return|return
name|createACL
argument_list|(
name|TEST_PATH
argument_list|,
name|entries
argument_list|,
name|namePathMapper
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|)
return|;
block|}
name|ACL
name|createACL
parameter_list|(
annotation|@
name|Nullable
name|String
name|jcrPath
parameter_list|,
annotation|@
name|NotNull
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
name|createACL
argument_list|(
name|jcrPath
argument_list|,
name|entries
argument_list|,
name|namePathMapper
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|)
return|;
block|}
name|ACL
name|createACL
parameter_list|(
annotation|@
name|Nullable
name|String
name|jcrPath
parameter_list|,
annotation|@
name|NotNull
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
specifier|final
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|String
name|path
init|=
operator|(
name|jcrPath
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
return|return
operator|new
name|ACL
argument_list|(
name|path
argument_list|,
name|entries
argument_list|,
name|namePathMapper
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|restrictionProvider
return|;
block|}
annotation|@
name|Override
name|ACE
name|createACE
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|PrivilegeBits
name|privilegeBits
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createEntry
argument_list|(
name|principal
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|checkValidPrincipal
parameter_list|(
name|Principal
name|principal
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|Util
operator|.
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|,
name|principalManager
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|()
block|{
return|return
name|privilegeManager
return|;
block|}
annotation|@
name|Override
name|PrivilegeBits
name|getPrivilegeBits
parameter_list|(
name|Privilege
index|[]
name|privileges
parameter_list|)
block|{
return|return
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
operator|.
name|getBits
argument_list|(
name|privileges
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

