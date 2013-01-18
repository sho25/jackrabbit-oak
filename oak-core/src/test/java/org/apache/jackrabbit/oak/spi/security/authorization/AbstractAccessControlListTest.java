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
name|HashMap
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
name|collect
operator|.
name|Lists
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
name|oak
operator|.
name|TestNameMapper
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
name|GlobalNameMapper
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
name|NameMapper
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionDefinition
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
name|assertNotNull
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

begin_comment
comment|/**  * AbstractAccessControlListTest... TODO  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAccessControlListTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
specifier|final
name|String
name|testPath
init|=
literal|"/testPath"
decl_stmt|;
specifier|protected
name|String
name|getTestPath
parameter_list|()
block|{
return|return
name|testPath
return|;
block|}
specifier|protected
name|AbstractAccessControlList
name|createEmptyACL
parameter_list|()
block|{
return|return
name|createACL
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|JackrabbitAccessControlEntry
operator|>
name|emptyList
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
specifier|protected
name|AbstractAccessControlList
name|createACL
parameter_list|(
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
parameter_list|)
block|{
return|return
name|createACL
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|entries
argument_list|)
return|;
block|}
specifier|protected
name|AbstractAccessControlList
name|createACL
parameter_list|(
name|String
name|jcrPath
parameter_list|,
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
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
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|AbstractAccessControlList
name|createACL
parameter_list|(
name|String
name|jcrPath
parameter_list|,
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
specifier|protected
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|createTestEntries
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
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
operator|new
name|ACE
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"testPrincipal"
operator|+
name|i
argument_list|)
argument_list|,
operator|new
name|Privilege
index|[]
block|{
name|getPrivilegeManager
argument_list|()
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
block|}
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPath
parameter_list|()
block|{
name|NameMapper
name|nameMapper
init|=
operator|new
name|GlobalNameMapper
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"jr"
argument_list|,
literal|"http://jackrabbit.apache.org"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|NamePathMapper
name|npMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|nameMapper
argument_list|)
decl_stmt|;
comment|// map of jcr-path to standard jcr-path
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paths
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|getTestPath
argument_list|()
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|"/jr:testPath"
argument_list|,
literal|"/jr:testPath"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|"/{http://jackrabbit.apache.org}testPath"
argument_list|,
literal|"/jr:testPath"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
operator|.
name|keySet
argument_list|()
control|)
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|path
argument_list|,
name|Collections
operator|.
expr|<
name|JackrabbitAccessControlEntry
operator|>
name|emptyList
argument_list|()
argument_list|,
name|npMapper
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|acl
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOakPath
parameter_list|()
block|{
name|NamePathMapper
name|npMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|TestNameMapper
argument_list|()
argument_list|)
decl_stmt|;
comment|// map of jcr-path to oak path
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paths
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
name|getTestPath
argument_list|()
argument_list|,
name|getTestPath
argument_list|()
argument_list|)
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|String
name|oakPath
init|=
literal|'/'
operator|+
name|TestNameMapper
operator|.
name|TEST_PREFIX
operator|+
literal|":testPath"
decl_stmt|;
name|String
name|jcrPath
init|=
literal|'/'
operator|+
name|TestNameMapper
operator|.
name|TEST_LOCAL_PREFIX
operator|+
literal|":testPath"
decl_stmt|;
name|paths
operator|.
name|put
argument_list|(
name|jcrPath
argument_list|,
name|oakPath
argument_list|)
expr_stmt|;
name|jcrPath
operator|=
literal|"/{"
operator|+
name|TestNameMapper
operator|.
name|TEST_URI
operator|+
literal|"}testPath"
expr_stmt|;
name|paths
operator|.
name|put
argument_list|(
name|jcrPath
argument_list|,
name|oakPath
argument_list|)
expr_stmt|;
comment|// test if oak-path is properly set.
for|for
control|(
name|String
name|path
range|:
name|paths
operator|.
name|keySet
argument_list|()
control|)
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|path
argument_list|,
name|Collections
operator|.
expr|<
name|JackrabbitAccessControlEntry
operator|>
name|emptyList
argument_list|()
argument_list|,
name|npMapper
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|acl
operator|.
name|getOakPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyAcl
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|acl
operator|.
name|getEntries
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
operator|.
name|length
argument_list|,
name|acl
operator|.
name|getEntries
argument_list|()
operator|.
name|size
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
name|assertTrue
argument_list|(
name|acl
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSize
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|createTestEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|acl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsEmpty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|createTestEntries
argument_list|()
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetEntries
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|JackrabbitAccessControlEntry
argument_list|>
name|aces
init|=
name|createTestEntries
argument_list|()
decl_stmt|;
name|AbstractAccessControlList
name|acl
init|=
name|createACL
argument_list|(
name|aces
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|acl
operator|.
name|getEntries
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aces
operator|.
name|size
argument_list|()
argument_list|,
name|acl
operator|.
name|getEntries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aces
operator|.
name|size
argument_list|()
argument_list|,
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|acl
operator|.
name|getEntries
argument_list|()
operator|.
name|containsAll
argument_list|(
name|aces
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
argument_list|)
operator|.
name|containsAll
argument_list|(
name|aces
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
name|String
index|[]
name|restrNames
init|=
name|acl
operator|.
name|getRestrictionNames
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|restrNames
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|restrNames
argument_list|)
decl_stmt|;
for|for
control|(
name|RestrictionDefinition
name|def
range|:
name|getRestrictionProvider
argument_list|()
operator|.
name|getSupportedRestrictions
argument_list|(
name|getTestPath
argument_list|()
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|names
operator|.
name|remove
argument_list|(
name|def
operator|.
name|getJcrName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|names
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionType
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
for|for
control|(
name|RestrictionDefinition
name|def
range|:
name|getRestrictionProvider
argument_list|()
operator|.
name|getSupportedRestrictions
argument_list|(
name|getTestPath
argument_list|()
argument_list|)
control|)
block|{
name|int
name|reqType
init|=
name|acl
operator|.
name|getRestrictionType
argument_list|(
name|def
operator|.
name|getJcrName
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reqType
operator|>
name|PropertyType
operator|.
name|UNDEFINED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|def
operator|.
name|getRequiredType
argument_list|()
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRestrictionTypeForUnknownName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractAccessControlList
name|acl
init|=
name|createEmptyACL
argument_list|()
decl_stmt|;
comment|// for backwards compatibility getRestrictionType(String) must return
comment|// UNDEFINED for a unknown restriction name:
name|assertEquals
argument_list|(
name|PropertyType
operator|.
name|UNDEFINED
argument_list|,
name|acl
operator|.
name|getRestrictionType
argument_list|(
literal|"unknownRestrictionName"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

