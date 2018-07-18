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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|JCR_READ
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
name|assertArrayEquals
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
name|Arrays
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
name|Random
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
name|jcr
operator|.
name|Session
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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
name|ConfigurationParameters
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
name|permission
operator|.
name|PermissionProvider
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
name|permission
operator|.
name|Permissions
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
name|permission
operator|.
name|RepositoryPermission
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
name|permission
operator|.
name|TreePermission
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
name|junit
operator|.
name|Test
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
name|Sets
import|;
end_import

begin_comment
comment|/**  * Randomized PermissionStore test. It generates a random structure (1110  * nodes), samples 10% of the paths for setting 'user' allow read, for setting  * 'user' deny read, 10% for setting 'group' allow read and 10% for setting  * 'group' deny read.  *<p>  * For testing a custom implementation against the known evaluation rules,  * override the {@link #getSecurityConfigParameters()} method.  *<p>  * For testing a custom implementation against the default implementation,  * override the {@link #candidatePermissionProvider(Root, String, Set)}.  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPermissionRandomTestIT
extends|extends
name|AbstractSecurityTest
block|{
specifier|protected
specifier|final
name|long
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|String
name|testPath
init|=
literal|"testPath"
operator|+
name|random
operator|.
name|nextInt
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allowU
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|denyU
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allowG
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|denyG
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|ContentSession
name|testSession
decl_stmt|;
specifier|private
specifier|final
name|String
name|groupId
init|=
literal|"gr"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
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
name|Tree
name|rootNode
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|testNode
init|=
name|TreeUtil
operator|.
name|getOrAddChild
argument_list|(
name|rootNode
argument_list|,
name|testPath
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
comment|// Setup 1110x
name|create
argument_list|(
name|testNode
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|int
name|sampleSize
init|=
name|paths
operator|.
name|size
argument_list|()
operator|/
literal|10
decl_stmt|;
name|sample
argument_list|(
name|paths
argument_list|,
name|sampleSize
argument_list|,
name|random
argument_list|,
name|allowU
argument_list|)
expr_stmt|;
name|sample
argument_list|(
name|paths
argument_list|,
name|sampleSize
argument_list|,
name|random
argument_list|,
name|denyU
argument_list|)
expr_stmt|;
name|sample
argument_list|(
name|paths
argument_list|,
name|sampleSize
argument_list|,
name|random
argument_list|,
name|allowG
argument_list|)
expr_stmt|;
name|sample
argument_list|(
name|paths
argument_list|,
name|sampleSize
argument_list|,
name|random
argument_list|,
name|denyG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
specifier|protected
name|PermissionProvider
name|candidatePermissionProvider
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
operator|new
name|SetsPP
argument_list|(
name|allowU
argument_list|,
name|denyU
argument_list|,
name|allowG
argument_list|,
name|denyG
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|create
parameter_list|(
name|Tree
name|t
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|lvl
parameter_list|,
name|int
name|maxlvl
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|lvl
operator|==
name|maxlvl
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Tree
name|c
init|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|t
argument_list|,
literal|"n"
operator|+
name|i
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|c
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|create
argument_list|(
name|c
argument_list|,
name|count
argument_list|,
name|lvl
operator|+
literal|1
argument_list|,
name|maxlvl
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|void
name|sample
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|int
name|size
parameter_list|,
name|Random
name|random
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|sample
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|size
operator|>
literal|0
operator|&&
name|size
operator|<=
name|paths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|paths
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|sample
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRandomRead
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|u
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|Group
name|group
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|group
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|Principal
name|g
init|=
name|group
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
comment|// set user allow read
for|for
control|(
name|String
name|path
range|:
name|allowU
control|)
block|{
name|setPrivileges
argument_list|(
name|u
argument_list|,
name|path
argument_list|,
literal|true
argument_list|,
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
comment|// set user deny read
for|for
control|(
name|String
name|path
range|:
name|denyU
control|)
block|{
name|setPrivileges
argument_list|(
name|u
argument_list|,
name|path
argument_list|,
literal|false
argument_list|,
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
comment|// set group allow read
for|for
control|(
name|String
name|path
range|:
name|allowG
control|)
block|{
name|setPrivileges
argument_list|(
name|g
argument_list|,
name|path
argument_list|,
literal|true
argument_list|,
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
comment|// set group deny read
for|for
control|(
name|String
name|path
range|:
name|denyG
control|)
block|{
name|setPrivileges
argument_list|(
name|g
argument_list|,
name|path
argument_list|,
literal|false
argument_list|,
name|JCR_READ
argument_list|)
expr_stmt|;
block|}
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|Root
name|testRoot
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|AuthorizationConfiguration
name|acConfig
init|=
name|getConfig
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
name|acConfig
operator|.
name|getPermissionProvider
argument_list|(
name|testRoot
argument_list|,
name|testSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
name|PermissionProvider
name|candidate
init|=
name|candidatePermissionProvider
argument_list|(
name|testRoot
argument_list|,
name|testSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|isSetImpl
init|=
name|candidate
operator|instanceof
name|SetsPP
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Tree
name|t
init|=
name|testRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|hasPrivileges0
init|=
name|pp
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|boolean
name|isGrantedA0
init|=
name|pp
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
decl_stmt|;
name|boolean
name|isGrantedP0
init|=
name|pp
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
decl_stmt|;
name|String
index|[]
name|privs0
init|=
name|pp
operator|.
name|getPrivileges
argument_list|(
name|t
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|privs0
argument_list|)
expr_stmt|;
name|boolean
name|hasPrivileges1
init|=
name|candidate
operator|.
name|hasPrivileges
argument_list|(
name|t
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|boolean
name|isGrantedA1
init|=
name|candidate
operator|.
name|isGranted
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
decl_stmt|;
name|boolean
name|isGrantedP1
init|=
name|candidate
operator|.
name|isGranted
argument_list|(
name|t
argument_list|,
literal|null
argument_list|,
name|Permissions
operator|.
name|READ
argument_list|)
decl_stmt|;
name|String
index|[]
name|privs1
init|=
name|candidate
operator|.
name|getPrivileges
argument_list|(
name|t
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|privs1
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSetImpl
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Unexpected #hasPrivileges on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|hasPrivileges1
operator|+
literal|" got "
operator|+
name|hasPrivileges0
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|hasPrivileges1
operator|==
name|hasPrivileges0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected #isGranted on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|isGrantedA1
operator|+
literal|" got "
operator|+
name|isGrantedA0
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|isGrantedA1
operator|==
name|isGrantedA0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected #isGranted on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|isGrantedP1
operator|+
literal|" got "
operator|+
name|isGrantedP0
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|isGrantedP1
operator|==
name|isGrantedP0
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|privs1
argument_list|,
name|privs0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Unexpected #hasPrivileges on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|hasPrivileges0
operator|+
literal|" got "
operator|+
name|hasPrivileges1
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|hasPrivileges1
operator|==
name|hasPrivileges0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected #isGranted on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|isGrantedA0
operator|+
literal|" got "
operator|+
name|isGrantedA1
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|isGrantedA1
operator|==
name|isGrantedA0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Unexpected #isGranted on ["
operator|+
name|path
operator|+
literal|"] expecting "
operator|+
name|isGrantedP0
operator|+
literal|" got "
operator|+
name|isGrantedP1
operator|+
literal|", seed "
operator|+
name|seed
argument_list|,
name|isGrantedP1
operator|==
name|isGrantedP0
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|privs0
argument_list|,
name|privs1
argument_list|)
expr_stmt|;
block|}
block|}
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
name|assertTrue
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
operator|+
name|testPath
argument_list|)
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|setPrivileges
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|allow
parameter_list|,
name|String
modifier|...
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
name|AccessControlManager
name|acm
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
name|acm
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privilegesFromNames
argument_list|(
name|privileges
argument_list|)
argument_list|,
name|allow
argument_list|)
expr_stmt|;
name|acm
operator|.
name|setPolicy
argument_list|(
name|path
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
specifier|static
class|class
name|SetsPP
implements|implements
name|PermissionProvider
block|{
specifier|public
name|SetsPP
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allowU
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|denyU
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowG
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|denyG
parameter_list|)
block|{
name|this
operator|.
name|allowU
operator|=
name|allowU
expr_stmt|;
name|this
operator|.
name|denyU
operator|=
name|denyU
expr_stmt|;
name|this
operator|.
name|allowG
operator|=
name|allowG
expr_stmt|;
name|this
operator|.
name|denyG
operator|=
name|denyG
expr_stmt|;
block|}
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allowU
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|denyU
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|allowG
decl_stmt|;
specifier|protected
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|denyG
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPrivileges
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
if|if
condition|(
name|canRead
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|JCR_READ
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
modifier|...
name|privilegeNames
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Implemened only for JCR_READ"
argument_list|,
name|privilegeNames
operator|.
name|length
operator|==
literal|1
operator|&&
name|privilegeNames
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|canRead
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RepositoryPermission
name|getRepositoryPermission
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unimplemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|TreePermission
name|getTreePermission
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|TreePermission
name|parentPermission
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unimplemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|long
name|permissions
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Implemened only for Permissions.READ on trees"
argument_list|,
name|property
operator|==
literal|null
operator|&&
name|permissions
operator|==
name|Permissions
operator|.
name|READ
argument_list|)
expr_stmt|;
return|return
name|canRead
argument_list|(
name|tree
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGranted
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|String
name|jcrActions
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Implemened only for Session.ACTION_READ"
argument_list|,
name|jcrActions
operator|.
name|equals
argument_list|(
name|Session
operator|.
name|ACTION_READ
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|canRead
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|canRead
parameter_list|(
name|String
name|p
parameter_list|)
block|{
name|String
name|deny
init|=
name|extractStatus
argument_list|(
name|p
argument_list|,
name|denyU
argument_list|)
decl_stmt|;
name|String
name|allow
init|=
name|extractStatus
argument_list|(
name|p
argument_list|,
name|allowU
argument_list|)
decl_stmt|;
name|String
name|gdeny
init|=
name|extractStatus
argument_list|(
name|p
argument_list|,
name|denyG
argument_list|)
decl_stmt|;
name|String
name|gallow
init|=
name|extractStatus
argument_list|(
name|p
argument_list|,
name|allowG
argument_list|)
decl_stmt|;
if|if
condition|(
name|deny
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|allow
operator|!=
literal|null
condition|)
block|{
return|return
name|deny
operator|.
name|length
argument_list|()
operator|<
name|allow
operator|.
name|length
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|allow
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|gdeny
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|gallow
operator|!=
literal|null
condition|)
block|{
return|return
name|gdeny
operator|.
name|length
argument_list|()
operator|<
name|gallow
operator|.
name|length
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
name|gallow
operator|!=
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|extractStatus
parameter_list|(
name|String
name|p
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|String
name|res
init|=
literal|null
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|p
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|&&
name|len
operator|<
name|path
operator|.
name|length
argument_list|()
condition|)
block|{
name|res
operator|=
name|path
expr_stmt|;
name|len
operator|=
name|path
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
block|}
block|}
end_class

end_unit

