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
name|spi
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
name|JackrabbitAccessControlEntry
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_comment
comment|/**  * Test for {@code ImmutableACL}  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableACLTest
extends|extends
name|AbstractAccessControlListTest
block|{
specifier|private
name|Privilege
index|[]
name|testPrivileges
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
name|testPrivileges
operator|=
name|privilegesFromNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Privilege
index|[]
name|privilegesFromNames
parameter_list|(
name|String
modifier|...
name|privNames
parameter_list|)
block|{
name|Privilege
index|[]
name|p
init|=
operator|new
name|Privilege
index|[
name|privNames
operator|.
name|length
index|]
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
name|privNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Privilege
name|privilege
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Privilege
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|privilege
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|privNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|p
index|[
name|i
index|]
operator|=
name|privilege
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
specifier|protected
name|ImmutableACL
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
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
parameter_list|)
block|{
name|String
name|oakPath
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
name|ImmutableACL
argument_list|(
name|oakPath
argument_list|,
name|entries
argument_list|,
name|restrictionProvider
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertImmutable
parameter_list|(
name|JackrabbitAccessControlList
name|acl
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|msg
init|=
literal|"ACL should be immutable."
decl_stmt|;
try|try
block|{
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|acl
operator|.
name|addEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Value
index|[]
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
name|AccessControlEntry
index|[]
name|entries
init|=
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
if|if
condition|(
name|entries
operator|.
name|length
operator|>
literal|1
condition|)
block|{
try|try
block|{
name|acl
operator|.
name|orderBefore
argument_list|(
name|entries
index|[
literal|0
index|]
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
try|try
block|{
name|acl
operator|.
name|orderBefore
argument_list|(
name|entries
index|[
literal|1
index|]
argument_list|,
name|entries
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
for|for
control|(
name|AccessControlEntry
name|ace
range|:
name|entries
control|)
block|{
try|try
block|{
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|ace
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
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
name|testImmutable
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|createEntry
argument_list|(
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|acl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|acl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertImmutable
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyIsImmutable
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|acl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|acl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertImmutable
argument_list|(
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsForEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|acl
argument_list|,
name|createEmptyACL
argument_list|()
argument_list|)
expr_stmt|;
name|ACE
name|entry
init|=
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
name|createACL
argument_list|(
name|entry
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
operator|new
name|TestACL
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|JackrabbitAccessControlEntry
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|RestrictionProvider
name|rp
init|=
name|getRestrictionProvider
argument_list|()
decl_stmt|;
name|ACE
name|ace1
init|=
name|createEntry
argument_list|(
name|testPrincipal
argument_list|,
name|PrivilegeBits
operator|.
name|BUILT_IN
operator|.
name|get
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ACE
name|ace2
init|=
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|ACE
name|ace2b
init|=
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|instanceof
name|ImmutableACL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|acl
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|JackrabbitAccessControlList
name|repoAcl
init|=
name|createACL
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|repoAcl
operator|instanceof
name|ImmutableACL
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repoAcl
argument_list|,
name|repoAcl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|acl
argument_list|,
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|acl
argument_list|,
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repoAcl
argument_list|,
name|createACL
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|,
name|ace1
argument_list|,
name|ace2b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
name|createACL
argument_list|(
name|ace2
argument_list|,
name|ace1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
name|repoAcl
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
name|createEmptyACL
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
name|createACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acl
operator|.
name|equals
argument_list|(
operator|new
name|TestACL
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHashCode
parameter_list|()
throws|throws
name|Exception
block|{
name|RestrictionProvider
name|rp
init|=
name|getRestrictionProvider
argument_list|()
decl_stmt|;
name|ACE
name|ace1
init|=
name|createEntry
argument_list|(
literal|false
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_VERSION_MANAGEMENT
argument_list|)
decl_stmt|;
name|ACE
name|ace2
init|=
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|ACE
name|ace2b
init|=
name|createEntry
argument_list|(
literal|true
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|REP_READ_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|repoAcl
init|=
name|createACL
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
decl_stmt|;
name|int
name|hc
init|=
name|acl
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|hc
operator|==
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hc
operator|==
name|createACL
argument_list|(
name|ace1
argument_list|,
name|ace2b
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|repoAcl
operator|.
name|hashCode
argument_list|()
operator|==
name|createACL
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|,
name|ace1
argument_list|,
name|ace2b
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
name|createACL
argument_list|(
name|ace2
argument_list|,
name|ace1
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
name|repoAcl
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
name|createEmptyACL
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
name|createACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
operator|new
name|TestACL
argument_list|(
literal|"/anotherPath"
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hc
operator|==
operator|new
name|TestACL
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|rp
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|ace1
argument_list|,
name|ace2
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

