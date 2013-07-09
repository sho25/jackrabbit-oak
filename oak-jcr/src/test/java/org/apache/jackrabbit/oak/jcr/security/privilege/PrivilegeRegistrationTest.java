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
name|privilege
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|AccessDeniedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NamespaceException
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
name|Repository
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Workspace
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
name|jcr
operator|.
name|Jcr
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

begin_comment
comment|/**  * Test privilege registration.  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeRegistrationTest
extends|extends
name|AbstractPrivilegeTest
block|{
specifier|private
name|Repository
name|repository
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|PrivilegeManager
name|privilegeManager
decl_stmt|;
annotation|@
name|Before
specifier|public
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
comment|// create a separate repository in order to be able to remove registered privileges.
name|repository
operator|=
operator|new
name|Jcr
argument_list|()
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|session
operator|=
name|getAdminSession
argument_list|()
expr_stmt|;
name|privilegeManager
operator|=
name|getPrivilegeManager
argument_list|(
name|session
argument_list|)
expr_stmt|;
comment|// make sure the guest session has read access
try|try
block|{
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|session
argument_list|,
literal|"/"
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// failed to initialize
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
name|repository
operator|=
literal|null
expr_stmt|;
name|privilegeManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|Session
name|getReadOnlySession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
name|getHelper
argument_list|()
operator|.
name|getReadOnlyCredentials
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|Session
name|getAdminSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
name|getHelper
argument_list|()
operator|.
name|getSuperuserCredentials
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisterPrivilegeWithReadOnly
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|readOnly
init|=
name|getReadOnlySession
argument_list|()
decl_stmt|;
try|try
block|{
name|getPrivilegeManager
argument_list|(
name|readOnly
argument_list|)
operator|.
name|registerPrivilege
argument_list|(
literal|"test"
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
name|fail
argument_list|(
literal|"Only admin is allowed to register privileges."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|readOnly
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomDefinitionsWithCyclicReferences
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"cycl-1"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"cycl-1"
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cyclic definitions must be detected upon registration."
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
annotation|@
name|Test
specifier|public
name|void
name|testCustomEquivalentDefinitions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"custom4"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"custom5"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"custom2"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"custom4"
block|,
literal|"custom5"
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|equivalent
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|equivalent
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"custom4"
block|,
literal|"custom5"
block|}
argument_list|)
expr_stmt|;
name|equivalent
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"custom2"
block|,
literal|"custom4"
block|}
argument_list|)
expr_stmt|;
name|equivalent
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"custom2"
block|,
literal|"custom5"
block|}
argument_list|)
expr_stmt|;
name|int
name|cnt
init|=
literal|6
decl_stmt|;
for|for
control|(
name|String
index|[]
name|aggrNames
range|:
name|equivalent
control|)
block|{
try|try
block|{
comment|// the equivalent definition to 'custom1'
name|String
name|name
init|=
literal|"custom"
operator|+
operator|(
name|cnt
operator|++
operator|)
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|aggrNames
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Equivalent '"
operator|+
name|name
operator|+
literal|"' definitions must be detected."
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
name|testRegisterBuiltInPrivilege
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|builtIns
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|builtIns
operator|.
name|put
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|builtIns
operator|.
name|put
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_LIFECYCLE_MANAGEMENT
argument_list|,
operator|new
name|String
index|[]
block|{
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
block|}
argument_list|)
expr_stmt|;
name|builtIns
operator|.
name|put
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_WRITE
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|builtIns
operator|.
name|put
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|builtInName
range|:
name|builtIns
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|builtInName
argument_list|,
literal|false
argument_list|,
name|builtIns
operator|.
name|get
argument_list|(
name|builtInName
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Privilege name "
operator|+
name|builtInName
operator|+
literal|" already in use -> Exception expected"
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
name|builtInName
range|:
name|builtIns
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|builtInName
argument_list|,
literal|true
argument_list|,
name|builtIns
operator|.
name|get
argument_list|(
name|builtInName
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Privilege name "
operator|+
name|builtInName
operator|+
literal|" already in use -> Exception expected"
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
name|testRegisterInvalidNewAggregate
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newAggregates
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// same as jcr:read
name|newAggregates
operator|.
name|put
argument_list|(
literal|"jcrReadAggregate"
argument_list|,
name|getAggregateNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// aggregated combining built-in and an unknown privilege
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newAggregate2"
argument_list|,
name|getAggregateNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
literal|"unknownPrivilege"
argument_list|)
argument_list|)
expr_stmt|;
comment|// aggregate containing unknown privilege
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newAggregate3"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"unknownPrivilege"
argument_list|)
argument_list|)
expr_stmt|;
comment|// custom aggregated contains itself
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newAggregate4"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"newAggregate"
argument_list|)
argument_list|)
expr_stmt|;
comment|// same as rep:write
name|newAggregates
operator|.
name|put
argument_list|(
literal|"repWriteAggregate"
argument_list|,
name|getAggregateNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_ADD_CHILD_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_NODE_TYPE_MANAGEMENT
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_CHILD_NODES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_REMOVE_NODE
argument_list|)
argument_list|)
expr_stmt|;
comment|// aggregated combining built-in and unknown custom
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newAggregate5"
argument_list|,
name|getAggregateNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|,
literal|"unknownPrivilege"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newAggregates
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|newAggregates
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"New aggregate "
operator|+
name|name
operator|+
literal|" referring to unknown Privilege  -> Exception expected"
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
name|testRegisterInvalidNewAggregate2
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newCustomPrivs
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"new"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"new2"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"new3"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"new"
argument_list|,
literal|"new2"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newCustomPrivs
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|isAbstract
init|=
literal|true
decl_stmt|;
name|String
index|[]
name|aggrNames
init|=
name|newCustomPrivs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newAggregates
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// other illegal aggregates already represented by registered definition.
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newA2"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newA3"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"new2"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newAggregates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|isAbstract
init|=
literal|false
decl_stmt|;
name|String
index|[]
name|aggrNames
init|=
name|newAggregates
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid aggregation in definition '"
operator|+
name|name
operator|.
name|toString
argument_list|()
operator|+
literal|"' : Exception expected"
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
name|testRegisterPrivilegeWithIllegalName
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|illegal
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// invalid privilege name
name|illegal
operator|.
name|put
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|""
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"invalid:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|".e:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// invalid aggregate names
name|illegal
operator|.
name|put
argument_list|(
literal|"newPrivilege"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"invalid:privilegeName"
block|}
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"newPrivilege"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|".e:privilegeName"
block|}
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"newPrivilege"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"newPrivilege"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|illegalName
range|:
name|illegal
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|illegalName
argument_list|,
literal|true
argument_list|,
name|illegal
operator|.
name|get
argument_list|(
name|illegalName
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Illegal name -> Exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamespaceException
name|e
parameter_list|)
block|{
comment|// success
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
name|testRegisterReservedName
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|illegal
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// invalid privilege name
name|illegal
operator|.
name|put
argument_list|(
literal|null
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"jcr:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"rep:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"nt:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"mix:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"sv:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"xml:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|illegal
operator|.
name|put
argument_list|(
literal|"xmlns:privilegeName"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// invalid aggregate names
name|illegal
operator|.
name|put
argument_list|(
literal|"newPrivilege"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jcr:privilegeName"
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|illegalName
range|:
name|illegal
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|illegalName
argument_list|,
literal|true
argument_list|,
name|illegal
operator|.
name|get
argument_list|(
name|illegalName
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Illegal name -> Exception expected"
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
name|testRegisterCustomPrivileges
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Workspace
name|workspace
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|workspace
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|registerNamespace
argument_list|(
literal|"test"
argument_list|,
literal|"http://www.apache.org/jackrabbit/test"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newCustomPrivs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"new"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"test:new"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newCustomPrivs
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|isAbstract
init|=
literal|true
decl_stmt|;
name|String
index|[]
name|aggrNames
init|=
name|newCustomPrivs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Privilege
name|registered
init|=
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
decl_stmt|;
comment|// validate definition
name|Privilege
name|privilege
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|privilege
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|privilege
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|privilege
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|privilege
operator|.
name|getDeclaredAggregatePrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertContainsDeclared
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newAggregates
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|// a new aggregate of custom privileges
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newA2"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"test:new"
argument_list|,
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
comment|// a new aggregate of custom and built-in privilege
name|newAggregates
operator|.
name|put
argument_list|(
literal|"newA1"
argument_list|,
name|getAggregateNames
argument_list|(
literal|"new"
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
comment|// aggregating built-in privileges
name|newAggregates
operator|.
name|put
argument_list|(
literal|"aggrBuiltIn"
argument_list|,
name|getAggregateNames
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|,
name|PrivilegeConstants
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newAggregates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|isAbstract
init|=
literal|false
decl_stmt|;
name|String
index|[]
name|aggrNames
init|=
name|newAggregates
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
expr_stmt|;
name|Privilege
name|p
init|=
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|name
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|n
range|:
name|aggrNames
control|)
block|{
name|assertContainsDeclared
argument_list|(
name|p
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|assertContainsDeclared
argument_list|(
name|privilegeManager
operator|.
name|getPrivilege
argument_list|(
name|PrivilegeConstants
operator|.
name|JCR_ALL
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testRegisterCustomPrivilegesVisibleInContent
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Workspace
name|workspace
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
name|workspace
operator|.
name|getNamespaceRegistry
argument_list|()
operator|.
name|registerNamespace
argument_list|(
literal|"test"
argument_list|,
literal|"http://www.apache.org/jackrabbit/test"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|newCustomPrivs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"new"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|newCustomPrivs
operator|.
name|put
argument_list|(
literal|"test:new"
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|newCustomPrivs
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|isAbstract
init|=
literal|true
decl_stmt|;
name|String
index|[]
name|aggrNames
init|=
name|newCustomPrivs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Privilege
name|registered
init|=
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|name
argument_list|,
name|isAbstract
argument_list|,
name|aggrNames
argument_list|)
decl_stmt|;
name|Node
name|privilegeRoot
init|=
name|session
operator|.
name|getNode
argument_list|(
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|privilegeRoot
operator|.
name|hasNode
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|privNode
init|=
name|privilegeRoot
operator|.
name|getNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|privNode
operator|.
name|getProperty
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_IS_ABSTRACT
argument_list|)
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|privNode
operator|.
name|hasProperty
argument_list|(
name|PrivilegeConstants
operator|.
name|REP_AGGREGATES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomPrivilegeVisibleToNewSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|boolean
name|isAbstract
init|=
literal|false
decl_stmt|;
name|String
name|privName
init|=
literal|"testCustomPrivilegeVisibleToNewSession"
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|privName
argument_list|,
name|isAbstract
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Session
name|s2
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|PrivilegeManager
name|pm
init|=
name|getPrivilegeManager
argument_list|(
name|s2
argument_list|)
decl_stmt|;
name|Privilege
name|priv
init|=
name|pm
operator|.
name|getPrivilege
argument_list|(
name|privName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|privName
argument_list|,
name|priv
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isAbstract
argument_list|,
name|priv
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|priv
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testCustomPrivilegeVisibleAfterRefresh
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s2
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|PrivilegeManager
name|pm
init|=
name|getPrivilegeManager
argument_list|(
name|s2
argument_list|)
decl_stmt|;
try|try
block|{
name|boolean
name|isAbstract
init|=
literal|false
decl_stmt|;
name|String
name|privName
init|=
literal|"testCustomPrivilegeVisibleAfterRefresh"
decl_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
name|privName
argument_list|,
name|isAbstract
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// before refreshing: privilege not visible
try|try
block|{
name|Privilege
name|priv
init|=
name|pm
operator|.
name|getPrivilege
argument_list|(
name|privName
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Custom privilege will show up after Session#refresh()"
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
comment|// latest after refresh privilege manager must be updated
name|s2
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Privilege
name|priv
init|=
name|pm
operator|.
name|getPrivilege
argument_list|(
name|privName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|privName
argument_list|,
name|priv
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isAbstract
argument_list|,
name|priv
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|priv
operator|.
name|isAggregate
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @since oak      */
annotation|@
name|Test
specifier|public
name|void
name|testRegisterPrivilegeWithPendingChanges
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|session
operator|.
name|hasPendingChanges
argument_list|()
argument_list|)
expr_stmt|;
name|privilegeManager
operator|.
name|registerPrivilege
argument_list|(
literal|"new"
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
name|fail
argument_list|(
literal|"Privileges may not be registered while there are pending changes."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

