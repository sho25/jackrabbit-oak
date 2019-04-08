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
name|namepath
operator|.
name|impl
operator|.
name|LocalNameMapper
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
name|impl
operator|.
name|NamePathMapperImpl
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
name|privilege
operator|.
name|PrivilegeConstants
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
name|Privilege
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
name|List
import|;
end_import

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
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
operator|.
name|REP_POLICY
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|RemappedPrivilegeNamesTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|LOCAL_NAME_MAPPINGS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"internal"
argument_list|,
literal|"b"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|,
literal|"c"
argument_list|,
literal|"http://jackrabbit.apache.org/oak/ns/1.0"
argument_list|)
decl_stmt|;
specifier|private
name|NamePathMapperImpl
name|remapped
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NamePathMapper
name|getNamePathMapper
parameter_list|()
block|{
if|if
condition|(
name|remapped
operator|==
literal|null
condition|)
block|{
name|remapped
operator|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|root
argument_list|,
name|LOCAL_NAME_MAPPINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|remapped
return|;
block|}
specifier|protected
name|Privilege
index|[]
name|privilegesFromNames
parameter_list|(
annotation|@
name|NotNull
name|String
modifier|...
name|privilegeNames
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Iterable
argument_list|<
name|String
argument_list|>
name|jcrNames
init|=
name|Iterables
operator|.
name|transform
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|privilegeNames
argument_list|)
argument_list|,
name|s
lambda|->
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|privilegesFromNames
argument_list|(
name|jcrNames
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AccessControlException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAddEntryWithOakPrivilegeName
parameter_list|()
throws|throws
name|Exception
block|{
name|Privilege
index|[]
name|privs
init|=
operator|new
name|Privilege
index|[]
block|{
name|when
argument_list|(
name|mock
argument_list|(
name|Privilege
operator|.
name|class
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
operator|.
name|getMock
argument_list|()
block|}
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddEntryWithJcrPrivilegeName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ACE
argument_list|>
name|entries
init|=
name|acl
operator|.
name|getEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getBitsProvider
argument_list|()
operator|.
name|getBits
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|entries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPrivilegeBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteEntryWithJcrPrivilegeName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|Tree
name|aceTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_POLICY
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
name|Iterable
argument_list|<
name|String
argument_list|>
name|privNames
init|=
name|TreeUtil
operator|.
name|getNames
argument_list|(
name|aceTree
argument_list|,
name|REP_PRIVILEGES
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|privNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
