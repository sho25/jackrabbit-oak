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
name|security
operator|.
name|acl
operator|.
name|Group
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
name|Enumeration
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
name|annotation
operator|.
name|Nonnull
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
name|Objects
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
name|ImmutableSet
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
name|core
operator|.
name|ImmutableRoot
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
name|core
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
name|core
operator|.
name|TreeTypeProvider
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
name|security
operator|.
name|SecurityProviderImpl
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProviderImpl
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
name|security
operator|.
name|SecurityProvider
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
name|AccessControlConfiguration
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
name|OpenAccessControlConfiguration
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
name|ReadStatus
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
name|Ignore
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
name|NT_UNSTRUCTURED
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
name|assertSame
import|;
end_import

begin_comment
comment|/**  * CompiledPermissionImplTest... TODO  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"work in progress"
argument_list|)
specifier|public
class|class
name|CompiledPermissionImplTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PermissionConstants
block|{
specifier|private
name|Principal
name|userPrincipal
decl_stmt|;
specifier|private
name|Principal
name|group1
decl_stmt|;
specifier|private
name|Principal
name|group2
decl_stmt|;
specifier|private
name|Principal
name|group3
decl_stmt|;
specifier|private
name|PrivilegeBitsProvider
name|pbp
decl_stmt|;
specifier|private
name|RestrictionProvider
name|rp
decl_stmt|;
specifier|private
name|String
name|node1Path
init|=
literal|"/nodeName1"
decl_stmt|;
specifier|private
name|String
name|node2Path
init|=
name|node1Path
operator|+
literal|"/nodeName2"
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|allPaths
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|rootAndUsers
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|nodePaths
decl_stmt|;
annotation|@
name|Before
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
name|userPrincipal
operator|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|group1
operator|=
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|group2
operator|=
operator|new
name|GroupImpl
argument_list|(
literal|"group2"
argument_list|)
expr_stmt|;
name|group3
operator|=
operator|new
name|GroupImpl
argument_list|(
literal|"group3"
argument_list|)
expr_stmt|;
name|pbp
operator|=
operator|new
name|PrivilegeBitsProvider
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|rp
operator|=
operator|new
name|RestrictionProviderImpl
argument_list|(
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
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
argument_list|)
decl_stmt|;
name|NodeUtil
name|system
init|=
name|rootNode
operator|.
name|getChild
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
name|NodeUtil
name|perms
init|=
name|system
operator|.
name|addChild
argument_list|(
name|REP_PERMISSION_STORE
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
decl_stmt|;
name|perms
operator|.
name|addChild
argument_list|(
name|userPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
expr_stmt|;
name|perms
operator|.
name|addChild
argument_list|(
name|group1
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
expr_stmt|;
name|perms
operator|.
name|addChild
argument_list|(
name|group2
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
expr_stmt|;
name|perms
operator|.
name|addChild
argument_list|(
name|group3
operator|.
name|getName
argument_list|()
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|)
expr_stmt|;
name|NodeUtil
name|testNode
init|=
name|rootNode
operator|.
name|addChild
argument_list|(
literal|"nodeName1"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setString
argument_list|(
literal|"propName1"
argument_list|,
literal|"strValue"
argument_list|)
expr_stmt|;
name|NodeUtil
name|testNode2
init|=
name|testNode
operator|.
name|addChild
argument_list|(
literal|"nodeName2"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|testNode2
operator|.
name|setString
argument_list|(
literal|"propName2"
argument_list|,
literal|"strValue"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|allPaths
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|node1Path
argument_list|,
name|node2Path
argument_list|)
expr_stmt|;
name|rootAndUsers
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
expr_stmt|;
name|nodePaths
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|node1Path
argument_list|,
name|node2Path
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
name|root
operator|.
name|getTree
argument_list|(
name|PERMISSIONS_STORE_PATH
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
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
operator|new
name|SecurityProviderImpl
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlConfiguration
name|getAccessControlConfiguration
parameter_list|()
block|{
return|return
operator|new
name|OpenAccessControlConfiguration
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|allPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus1
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|cp
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
name|node1Path
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node2Path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus2
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|Collections
operator|.
expr|<
name|Restriction
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|allPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus3
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_ALL
argument_list|,
name|cp
argument_list|,
name|allPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus4
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group2
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|allPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus5
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group2
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|allPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus6
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_THIS
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_ALL
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus7
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
name|node1Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_PROPERTIES
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus8
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|userPrincipal
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group2
argument_list|,
name|node1Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|userPrincipal
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|ReadStatus
operator|.
name|ALLOW_THIS
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|ReadStatus
operator|.
name|ALLOW_THIS
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus9
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_PROPERTIES
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus10
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus11
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group2
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group2
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|,
name|group2
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|treePaths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|node1Path
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|cp
argument_list|,
name|treePaths
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node2Path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus12
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_THIS
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus13
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_THIS
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node1Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus14
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node1Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetReadStatus15
parameter_list|()
throws|throws
name|Exception
block|{
name|setupPermission
argument_list|(
name|group1
argument_list|,
literal|"/"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node1Path
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|setupPermission
argument_list|(
name|group1
argument_list|,
name|node2Path
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|)
expr_stmt|;
name|CompiledPermissionImpl
name|cp
init|=
name|createPermissions
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group1
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_NODES
argument_list|,
name|cp
argument_list|,
name|rootAndUsers
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|cp
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node1Path
argument_list|)
argument_list|)
expr_stmt|;
name|assertReadStatus
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_PROPERTIES
argument_list|,
name|cp
argument_list|,
name|nodePaths
argument_list|)
expr_stmt|;
block|}
comment|// TODO: tests with restrictions
comment|// TODO: complex tests with entries for paths outside of the tested hierarchy
comment|// TODO: tests for isGranted
comment|// TODO: tests for hasPrivilege/getPrivileges
comment|// TODO: tests for path base evaluation
specifier|private
name|CompiledPermissionImpl
name|createPermissions
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|ImmutableTree
name|permissionsTree
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|root
argument_list|,
name|TreeTypeProvider
operator|.
name|EMPTY
argument_list|)
operator|.
name|getTree
argument_list|(
name|PERMISSIONS_STORE_PATH
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompiledPermissionImpl
argument_list|(
name|principals
argument_list|,
name|permissionsTree
argument_list|,
name|pbp
argument_list|,
name|rp
argument_list|)
return|;
block|}
specifier|private
name|void
name|setupPermission
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|int
name|index
parameter_list|,
name|String
name|privilegeName
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|setupPermission
argument_list|(
name|principal
argument_list|,
name|path
argument_list|,
name|isAllow
argument_list|,
name|index
argument_list|,
name|privilegeName
argument_list|,
name|Collections
operator|.
expr|<
name|Restriction
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setupPermission
parameter_list|(
name|Principal
name|principal
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|int
name|index
parameter_list|,
name|String
name|privilegeName
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|PrivilegeBits
name|pb
init|=
name|pbp
operator|.
name|getBits
argument_list|(
name|privilegeName
argument_list|)
decl_stmt|;
name|String
name|name
init|=
operator|(
operator|(
name|isAllow
operator|)
condition|?
name|PREFIX_ALLOW
else|:
name|PREFIX_DENY
operator|)
operator|+
literal|"-"
operator|+
name|Objects
operator|.
name|hashCode
argument_list|(
name|path
argument_list|,
name|principal
argument_list|,
name|index
argument_list|,
name|pb
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
decl_stmt|;
name|Tree
name|principalRoot
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PERMISSIONS_STORE_PATH
operator|+
literal|'/'
operator|+
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Tree
name|entry
init|=
name|principalRoot
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSIONS
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|REP_INDEX
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProperty
argument_list|(
name|pb
operator|.
name|asPropertyState
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
name|entry
operator|.
name|setProperty
argument_list|(
name|restriction
operator|.
name|getProperty
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
specifier|private
name|void
name|assertReadStatus
parameter_list|(
name|ReadStatus
name|expectedTrees
parameter_list|,
name|CompiledPermissions
name|cp
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|treePaths
parameter_list|)
block|{
name|assertReadStatus
argument_list|(
name|expectedTrees
argument_list|,
name|expectedTrees
argument_list|,
name|cp
argument_list|,
name|treePaths
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertReadStatus
parameter_list|(
name|ReadStatus
name|expectedTrees
parameter_list|,
name|ReadStatus
name|expectedProperties
parameter_list|,
name|CompiledPermissions
name|cp
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|treePaths
parameter_list|)
block|{
for|for
control|(
name|String
name|path
range|:
name|treePaths
control|)
block|{
name|Tree
name|node
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"Tree "
operator|+
name|path
argument_list|,
name|expectedTrees
argument_list|,
name|cp
operator|.
name|getReadStatus
argument_list|(
name|node
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"Property jcr:primaryType "
operator|+
name|path
argument_list|,
name|expectedProperties
argument_list|,
name|cp
operator|.
name|getReadStatus
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|GroupImpl
implements|implements
name|Group
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|GroupImpl
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

