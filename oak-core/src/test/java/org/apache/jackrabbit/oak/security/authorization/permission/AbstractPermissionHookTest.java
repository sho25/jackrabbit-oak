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
name|permission
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
name|AccessControlEntry
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AbstractAccessControlTest
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
comment|/**  * Testing the {@code PermissionHook}  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPermissionHookTest
extends|extends
name|AbstractAccessControlTest
implements|implements
name|AccessControlConstants
implements|,
name|PermissionConstants
implements|,
name|PrivilegeConstants
block|{
specifier|protected
name|String
name|testPath
init|=
literal|"/testPath"
decl_stmt|;
specifier|protected
name|String
name|childPath
init|=
literal|"/testPath/childNode"
decl_stmt|;
specifier|protected
name|String
name|testPrincipalName
decl_stmt|;
specifier|protected
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|ArrayList
argument_list|<
name|Principal
argument_list|>
argument_list|()
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
name|Principal
name|testPrincipal
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
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
name|namePathMapper
argument_list|)
decl_stmt|;
name|NodeUtil
name|testNode
init|=
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
decl_stmt|;
name|testNode
operator|.
name|addChild
argument_list|(
literal|"childNode"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|testPath
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testPrincipalName
operator|=
name|testPrincipal
operator|.
name|getName
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
block|}
annotation|@
name|Override
annotation|@
name|After
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
name|test
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|exists
argument_list|()
condition|)
block|{
name|test
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
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
specifier|protected
name|Tree
name|getPrincipalRoot
parameter_list|(
name|String
name|principalName
parameter_list|)
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|PERMISSIONS_STORE_PATH
argument_list|)
operator|.
name|getChild
argument_list|(
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|principalName
argument_list|)
return|;
block|}
specifier|protected
name|Tree
name|getEntry
parameter_list|(
name|String
name|principalName
parameter_list|,
name|String
name|accessControlledPath
parameter_list|,
name|long
name|index
parameter_list|)
throws|throws
name|Exception
block|{
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
return|return
name|traverse
argument_list|(
name|principalRoot
argument_list|,
name|accessControlledPath
argument_list|,
name|index
argument_list|)
return|;
block|}
specifier|protected
name|Tree
name|traverse
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|accessControlledPath
parameter_list|,
name|long
name|index
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Tree
name|entry
range|:
name|parent
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|long
name|entryIndex
init|=
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
if|if
condition|(
name|accessControlledPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
if|if
condition|(
name|index
operator|==
name|entryIndex
condition|)
block|{
return|return
name|entry
return|;
block|}
elseif|else
if|if
condition|(
name|index
operator|>
name|entryIndex
condition|)
block|{
return|return
name|traverse
argument_list|(
name|entry
argument_list|,
name|accessControlledPath
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|Text
operator|.
name|isDescendant
argument_list|(
name|path
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
return|return
name|traverse
argument_list|(
name|entry
argument_list|,
name|accessControlledPath
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"no such entry"
argument_list|)
throw|;
block|}
specifier|protected
name|long
name|cntEntries
parameter_list|(
name|Tree
name|parent
parameter_list|)
block|{
name|long
name|cnt
init|=
name|parent
operator|.
name|getChildrenCount
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|parent
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|cnt
operator|+=
name|cntEntries
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|cnt
return|;
block|}
specifier|protected
name|void
name|createPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|principals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|gr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
operator|+
name|i
argument_list|)
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
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
annotation|@
name|Test
specifier|public
name|void
name|testModifyRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|testAce
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
operator|+
literal|"/rep:policy"
argument_list|)
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|testPrincipalName
argument_list|,
name|testAce
operator|.
name|getProperty
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a new restriction node through the OAK API instead of access control manager
name|NodeUtil
name|node
init|=
operator|new
name|NodeUtil
argument_list|(
name|testAce
argument_list|)
decl_stmt|;
name|NodeUtil
name|restrictions
init|=
name|node
operator|.
name|addChild
argument_list|(
name|REP_RESTRICTIONS
argument_list|,
name|NT_REP_RESTRICTIONS
argument_list|)
decl_stmt|;
name|restrictions
operator|.
name|setString
argument_list|(
name|REP_GLOB
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|String
name|restrictionsPath
init|=
name|restrictions
operator|.
name|getTree
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|testPrincipalName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cntEntries
argument_list|(
name|principalRoot
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"*"
argument_list|,
name|principalRoot
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
comment|// modify the restrictions node
name|Tree
name|restrictionsNode
init|=
name|root
operator|.
name|getTree
argument_list|(
name|restrictionsPath
argument_list|)
decl_stmt|;
name|restrictionsNode
operator|.
name|setProperty
argument_list|(
name|REP_GLOB
argument_list|,
literal|"/*/jcr:content/*"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|principalRoot
operator|=
name|getPrincipalRoot
argument_list|(
name|testPrincipalName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cntEntries
argument_list|(
name|principalRoot
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/*/jcr:content/*"
argument_list|,
name|principalRoot
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove the restriction again
name|root
operator|.
name|getTree
argument_list|(
name|restrictionsPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|principalRoot
operator|=
name|getPrincipalRoot
argument_list|(
name|testPrincipalName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cntEntries
argument_list|(
name|principalRoot
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|principalRoot
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getProperty
argument_list|(
name|REP_GLOB
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReorderAce
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|entry
init|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|aclTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
operator|+
literal|"/rep:policy"
argument_list|)
decl_stmt|;
name|aclTree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|entry
operator|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReorderAndAddAce
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|entry
init|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|aclTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
operator|+
literal|"/rep:policy"
argument_list|)
decl_stmt|;
comment|// reorder
name|aclTree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// add a new entry
name|NodeUtil
name|ace
init|=
operator|new
name|NodeUtil
argument_list|(
name|aclTree
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"denyEveryoneLockMgt"
argument_list|,
name|NT_REP_DENY_ACE
argument_list|)
decl_stmt|;
name|ace
operator|.
name|setString
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setStrings
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRIVILEGES
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|entry
operator|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReorderAddAndRemoveAces
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|entry
init|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|aclTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|testPath
operator|+
literal|"/rep:policy"
argument_list|)
decl_stmt|;
comment|// reorder testPrincipal entry to the end
name|aclTree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|orderBefore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|aceIt
init|=
name|aclTree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// remove the everyone entry
name|aceIt
operator|.
name|next
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// remember the name of the testPrincipal entry.
name|String
name|name
init|=
name|aceIt
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// add a new entry
name|NodeUtil
name|ace
init|=
operator|new
name|NodeUtil
argument_list|(
name|aclTree
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"denyEveryoneLockMgt"
argument_list|,
name|NT_REP_DENY_ACE
argument_list|)
decl_stmt|;
name|ace
operator|.
name|setString
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|,
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ace
operator|.
name|setStrings
argument_list|(
name|AccessControlConstants
operator|.
name|REP_PRIVILEGES
argument_list|,
name|JCR_LOCK_MANAGEMENT
argument_list|)
expr_stmt|;
comment|// reorder the new entry before the remaining existing entry
name|ace
operator|.
name|getTree
argument_list|()
operator|.
name|orderBefore
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|entry
operator|=
name|getEntry
argument_list|(
name|testPrincipalName
argument_list|,
name|testPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * ACE    :  0   1   2   3   4   5   6   7      * Before :  tp  ev  p0  p1  p2  p3      * After  :      ev      p2  p1  p3  p4  p5      */
annotation|@
name|Test
specifier|public
name|void
name|testReorderAddAndRemoveAces2
parameter_list|()
throws|throws
name|Exception
block|{
name|createPrincipals
argument_list|()
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|testPath
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|AccessControlEntry
index|[]
name|aces
init|=
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|aces
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|aces
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|acl
operator|.
name|orderBefore
argument_list|(
name|aces
index|[
literal|4
index|]
argument_list|,
name|aces
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|entry
init|=
name|getEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|testPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|getEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|testPath
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * ACE    :  0   1   2   3   4   5   6   7      * Before :  tp  ev  p0  p1  p2  p3      * After  :      p1      ev  p3  p2      */
annotation|@
name|Test
specifier|public
name|void
name|testReorderAndRemoveAces
parameter_list|()
throws|throws
name|Exception
block|{
name|createPrincipals
argument_list|()
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|testPath
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|AccessControlEntry
index|[]
name|aces
init|=
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|aces
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|aces
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|acl
operator|.
name|orderBefore
argument_list|(
name|aces
index|[
literal|4
index|]
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|acl
operator|.
name|orderBefore
argument_list|(
name|aces
index|[
literal|3
index|]
argument_list|,
name|aces
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|entry
init|=
name|getEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|testPath
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|getEntry
argument_list|(
name|principals
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|testPath
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|entry
operator|.
name|getProperty
argument_list|(
name|REP_INDEX
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|pName
range|:
operator|new
name|String
index|[]
block|{
name|testPrincipalName
block|,
name|principals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
block|}
control|)
block|{
try|try
block|{
name|getEntry
argument_list|(
name|pName
argument_list|,
name|testPath
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|()
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
name|testImplicitAceRemoval
parameter_list|()
throws|throws
name|Exception
block|{
name|AccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|testPath
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|getTestPrincipal
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|,
name|REP_WRITE
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|testPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|childPath
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|childPath
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|childPath
operator|+
literal|"/rep:policy"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|principalRoot
init|=
name|getPrincipalRoot
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cntEntries
argument_list|(
name|principalRoot
argument_list|)
argument_list|)
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|childPath
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testRoot
operator|.
name|getTree
argument_list|(
name|childPath
operator|+
literal|"/rep:policy"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|testRoot
operator|.
name|getTree
argument_list|(
name|childPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|testRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|testPath
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"childNode"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|childPath
operator|+
literal|"/rep:policy"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// aces must be removed in the permission store even if the editing
comment|// session wasn't able to access them.
name|principalRoot
operator|=
name|getPrincipalRoot
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cntEntries
argument_list|(
name|principalRoot
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

