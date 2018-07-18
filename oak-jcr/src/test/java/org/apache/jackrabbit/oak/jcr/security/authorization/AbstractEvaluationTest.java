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
name|authorization
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
name|Collections
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
name|Credentials
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
name|PathNotFoundException
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
name|SimpleCredentials
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
name|AccessControlList
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
name|Maps
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|test
operator|.
name|api
operator|.
name|security
operator|.
name|AbstractAccessControlTest
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

begin_comment
comment|/**  * Base class for testing permission evaluation using JCR API.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractEvaluationTest
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
name|Value
argument_list|>
name|EMPTY_RESTRICTIONS
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|REP_WRITE
init|=
literal|"rep:write"
decl_stmt|;
specifier|protected
name|Privilege
index|[]
name|readPrivileges
decl_stmt|;
specifier|protected
name|Privilege
index|[]
name|modPropPrivileges
decl_stmt|;
specifier|protected
name|Privilege
index|[]
name|readWritePrivileges
decl_stmt|;
specifier|protected
name|Privilege
index|[]
name|repWritePrivileges
decl_stmt|;
specifier|protected
name|String
name|path
decl_stmt|;
specifier|protected
name|String
name|childNPath
decl_stmt|;
specifier|protected
name|String
name|childNPath2
decl_stmt|;
specifier|protected
name|String
name|childPPath
decl_stmt|;
specifier|protected
name|String
name|childchildPPath
decl_stmt|;
specifier|protected
name|String
name|siblingPath
decl_stmt|;
specifier|protected
name|User
name|testUser
decl_stmt|;
specifier|protected
name|Credentials
name|creds
decl_stmt|;
specifier|protected
name|Group
name|testGroup
decl_stmt|;
specifier|protected
name|Session
name|testSession
decl_stmt|;
specifier|protected
name|AccessControlManager
name|testAcMgr
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ACL
argument_list|>
name|toRestore
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
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
name|readPrivileges
operator|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
expr_stmt|;
name|modPropPrivileges
operator|=
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_MODIFY_PROPERTIES
argument_list|)
expr_stmt|;
name|readWritePrivileges
operator|=
name|privilegesFromNames
argument_list|(
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|,
name|REP_WRITE
block|}
argument_list|)
expr_stmt|;
name|repWritePrivileges
operator|=
name|privilegesFromName
argument_list|(
name|REP_WRITE
argument_list|)
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
comment|// create the testUser
name|String
name|uid
init|=
name|generateId
argument_list|(
literal|"testUser"
argument_list|)
decl_stmt|;
name|creds
operator|=
operator|new
name|SimpleCredentials
argument_list|(
name|uid
argument_list|,
name|uid
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|testUser
operator|=
name|uMgr
operator|.
name|createUser
argument_list|(
name|uid
argument_list|,
name|uid
argument_list|)
expr_stmt|;
name|UserManager
name|umgr
init|=
name|getUserManager
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|testGroup
operator|=
name|umgr
operator|.
name|createGroup
argument_list|(
name|generateId
argument_list|(
literal|"testGroup"
argument_list|)
argument_list|)
expr_stmt|;
name|testGroup
operator|.
name|addMember
argument_list|(
name|testUser
argument_list|)
expr_stmt|;
comment|// create some nodes below the test root in order to apply ac-stuff
name|Node
name|node
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|Node
name|cn1
init|=
name|node
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|Property
name|cp1
init|=
name|node
operator|.
name|setProperty
argument_list|(
name|propertyName1
argument_list|,
literal|"anyValue"
argument_list|)
decl_stmt|;
name|Node
name|cn2
init|=
name|node
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|Property
name|ccp1
init|=
name|cn1
operator|.
name|setProperty
argument_list|(
name|propertyName1
argument_list|,
literal|"childNodeProperty"
argument_list|)
decl_stmt|;
name|Node
name|n2
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|path
operator|=
name|node
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|childNPath
operator|=
name|cn1
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|childNPath2
operator|=
name|cn2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|childPPath
operator|=
name|cp1
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|childchildPPath
operator|=
name|ccp1
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|siblingPath
operator|=
name|n2
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|=
name|createTestSession
argument_list|()
expr_stmt|;
name|testAcMgr
operator|=
name|getAccessControlManager
argument_list|(
name|testSession
argument_list|)
expr_stmt|;
comment|/*         precondition:         testuser must have READ-only permission on test-node and below         */
name|assertReadOnly
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|assertReadOnly
argument_list|(
name|childNPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|protected
name|void
name|tearDown
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
operator|&&
name|testSession
operator|.
name|isLive
argument_list|()
condition|)
block|{
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// restore in reverse order
for|for
control|(
name|String
name|path
range|:
name|toRestore
operator|.
name|keySet
argument_list|()
control|)
block|{
name|toRestore
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|.
name|restore
argument_list|()
expr_stmt|;
block|}
name|toRestore
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|testUser
operator|!=
literal|null
condition|)
block|{
name|testUser
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
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Session
name|createTestSession
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getHelper
argument_list|()
operator|.
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|creds
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|UserManager
name|getUserManager
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|NotExecutableException
block|{
if|if
condition|(
operator|!
operator|(
name|session
operator|instanceof
name|JackrabbitSession
operator|)
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
try|try
block|{
return|return
operator|(
operator|(
name|JackrabbitSession
operator|)
name|session
operator|)
operator|.
name|getUserManager
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|static
name|String
name|generateId
parameter_list|(
annotation|@
name|NotNull
name|String
name|hint
parameter_list|)
block|{
return|return
name|hint
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|boolean
name|canReadNode
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|nodePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|session
operator|.
name|getNode
argument_list|(
name|nodePath
argument_list|)
expr_stmt|;
return|return
name|session
operator|.
name|nodeExists
argument_list|(
name|nodePath
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
return|return
name|session
operator|.
name|nodeExists
argument_list|(
name|nodePath
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Group
name|getTestGroup
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|testGroup
return|;
block|}
specifier|protected
name|String
name|getActions
parameter_list|(
name|String
modifier|...
name|actions
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|action
range|:
name|actions
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|createGlobRestriction
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"rep:glob"
argument_list|,
name|testSession
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|void
name|assertHasRepoPrivilege
parameter_list|(
annotation|@
name|NotNull
name|String
name|privName
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|Exception
block|{
name|Privilege
index|[]
name|privs
init|=
name|privilegesFromName
argument_list|(
name|privName
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|isAllow
argument_list|,
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
literal|null
argument_list|,
name|privs
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertHasPrivilege
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|String
name|privName
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|Exception
block|{
name|assertHasPrivileges
argument_list|(
name|path
argument_list|,
name|privilegesFromName
argument_list|(
name|privName
argument_list|)
argument_list|,
name|isAllow
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertHasPrivileges
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|isAllow
argument_list|,
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|testAcMgr
operator|.
name|hasPrivileges
argument_list|(
name|path
argument_list|,
name|privileges
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"PathNotFoundException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
specifier|protected
name|void
name|assertReadOnly
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|Privilege
index|[]
name|privs
init|=
name|testAcMgr
operator|.
name|getPrivileges
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|,
name|privs
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|modify
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|,
annotation|@
name|NotNull
name|String
name|privilege
parameter_list|,
name|boolean
name|isAllow
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|path
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|privilege
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|EMPTY_RESTRICTIONS
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|modify
parameter_list|(
name|String
name|path
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|path
argument_list|,
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
name|restrictions
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
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|modify
parameter_list|(
name|String
name|path
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
index|[]
argument_list|>
name|mvRestrictions
parameter_list|)
throws|throws
name|Exception
block|{
comment|// remember for restore during tearDown
name|rememberForRestore
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|JackrabbitAccessControlList
name|tmpl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|tmpl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|,
name|mvRestrictions
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|tmpl
operator|.
name|getPath
argument_list|()
argument_list|,
name|tmpl
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|tmpl
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|allow
parameter_list|(
annotation|@
name|Nullable
name|String
name|nPath
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|,
name|EMPTY_RESTRICTIONS
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|allow
parameter_list|(
annotation|@
name|Nullable
name|String
name|nPath
parameter_list|,
annotation|@
name|NotNull
name|Privilege
index|[]
name|privileges
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|allow
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|principal
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|,
name|EMPTY_RESTRICTIONS
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|deny
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|false
argument_list|,
name|EMPTY_RESTRICTIONS
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|deny
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|testUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|false
argument_list|,
name|restrictions
argument_list|)
return|;
block|}
specifier|protected
name|JackrabbitAccessControlList
name|deny
parameter_list|(
name|String
name|nPath
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|modify
argument_list|(
name|nPath
argument_list|,
name|principal
argument_list|,
name|privileges
argument_list|,
literal|false
argument_list|,
name|EMPTY_RESTRICTIONS
argument_list|)
return|;
block|}
specifier|private
name|void
name|rememberForRestore
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|toRestore
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|toRestore
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|ACL
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|ACL
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|remove
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|AccessControlEntry
argument_list|>
name|entries
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|ACL
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|AccessControlList
name|list
init|=
name|getList
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|remove
operator|=
operator|(
name|list
operator|==
literal|null
operator|)
expr_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|Collections
operator|.
name|addAll
argument_list|(
name|entries
argument_list|,
name|list
operator|.
name|getAccessControlEntries
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|restore
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AccessControlList
name|list
init|=
name|getList
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|remove
condition|)
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
name|path
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|AccessControlEntry
name|ace
range|:
name|list
operator|.
name|getAccessControlEntries
argument_list|()
control|)
block|{
name|list
operator|.
name|removeAccessControlEntry
argument_list|(
name|ace
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccessControlEntry
name|ace
range|:
name|entries
control|)
block|{
name|list
operator|.
name|addAccessControlEntry
argument_list|(
name|ace
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|ace
operator|.
name|getPrivileges
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nullable
specifier|private
name|AccessControlList
name|getList
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|superuser
operator|.
name|nodeExists
argument_list|(
name|path
argument_list|)
condition|)
block|{
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|policy
operator|instanceof
name|AccessControlList
condition|)
block|{
return|return
operator|(
name|AccessControlList
operator|)
name|policy
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

