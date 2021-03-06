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
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|ImportUUIDBehavior
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|api
operator|.
name|JackrabbitRepository
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|query
operator|.
name|QueryEngineSettings
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
name|spi
operator|.
name|xml
operator|.
name|ProtectedItemImporter
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
name|assertArrayEquals
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

begin_class
specifier|public
specifier|abstract
class|class
name|CugImportBaseTest
block|{
specifier|static
specifier|final
name|String
name|TEST_NODE_NAME
init|=
literal|"testNode"
decl_stmt|;
specifier|static
specifier|final
name|String
name|TEST_NODE_PATH
init|=
literal|"/testNode"
decl_stmt|;
specifier|static
specifier|final
name|String
name|TEST_GROUP_PRINCIPAL_NAME
init|=
literal|"testPrincipal"
decl_stmt|;
specifier|static
specifier|final
name|String
name|XML_CUG_POLICY
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>"
operator|+
name|TEST_GROUP_PRINCIPAL_NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"<sv:value>"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
specifier|static
specifier|final
name|String
name|XML_CHILD_WITH_CUG
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"child\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>oak:Unstructured</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:mixinTypes\" sv:type=\"Name\"><sv:value>rep:CugMixin</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>"
operator|+
name|TEST_GROUP_PRINCIPAL_NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"<sv:value>"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
specifier|static
specifier|final
name|String
name|XML_NESTED_CUG_POLICY
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
specifier|private
name|Repository
name|repo
decl_stmt|;
specifier|private
name|Session
name|adminSession
decl_stmt|;
specifier|private
name|Group
name|testGroup
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
name|ConfigurationParameters
name|config
init|=
name|getConfigurationParameters
argument_list|()
decl_stmt|;
name|SecurityProvider
name|securityProvider
init|=
name|CugSecurityProvider
operator|.
name|newTestSecurityProvider
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|QueryEngineSettings
name|queryEngineSettings
init|=
operator|new
name|QueryEngineSettings
argument_list|()
decl_stmt|;
name|queryEngineSettings
operator|.
name|setFailTraversal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|()
decl_stmt|;
name|jcr
operator|.
name|with
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|jcr
operator|.
name|with
argument_list|(
name|queryEngineSettings
argument_list|)
expr_stmt|;
name|repo
operator|=
name|jcr
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|adminSession
operator|=
name|repo
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|TEST_NODE_NAME
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
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
name|adminSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|getNode
argument_list|(
name|TEST_NODE_PATH
argument_list|)
operator|.
name|remove
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
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|adminSession
operator|.
name|logout
argument_list|()
expr_stmt|;
if|if
condition|(
name|repo
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repo
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|repo
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
name|ConfigurationParameters
name|getConfigurationParameters
parameter_list|()
block|{
name|String
name|importBehavior
init|=
name|getImportBehavior
argument_list|()
decl_stmt|;
if|if
condition|(
name|importBehavior
operator|!=
literal|null
condition|)
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|getImportBehavior
argument_list|()
argument_list|,
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[]
block|{
name|TEST_NODE_PATH
block|}
argument_list|)
decl_stmt|;
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|params
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
block|}
specifier|abstract
name|String
name|getImportBehavior
parameter_list|()
function_decl|;
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|TEST_NODE_PATH
return|;
block|}
name|Session
name|getImportSession
parameter_list|()
block|{
return|return
name|adminSession
return|;
block|}
name|Node
name|getTargetNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getImportSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|getTargetPath
argument_list|()
argument_list|)
return|;
block|}
name|void
name|doImport
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|xml
parameter_list|)
throws|throws
name|Exception
block|{
name|doImport
argument_list|(
name|parentPath
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
block|}
name|void
name|doImport
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|xml
parameter_list|,
name|int
name|importUUIDBehavior
parameter_list|)
throws|throws
name|Exception
block|{
name|doImport
argument_list|(
name|getImportSession
argument_list|()
argument_list|,
name|parentPath
argument_list|,
name|xml
argument_list|,
name|importUUIDBehavior
argument_list|)
expr_stmt|;
block|}
name|void
name|doImport
parameter_list|(
name|Session
name|importSession
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|String
name|xml
parameter_list|,
name|int
name|importUUIDBehavior
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|in
decl_stmt|;
if|if
condition|(
name|xml
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'<'
condition|)
block|{
name|in
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|xml
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|xml
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|importSession
operator|.
name|importXML
argument_list|(
name|parentPath
argument_list|,
name|in
argument_list|,
name|importUUIDBehavior
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|assertPrincipalNames
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|expectedPrincipalNames
parameter_list|,
annotation|@
name|NotNull
name|Value
index|[]
name|principalNames
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedPrincipalNames
operator|.
name|size
argument_list|()
argument_list|,
name|principalNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalNames
argument_list|)
argument_list|,
name|principalName
lambda|->
block|{
try|try
block|{
return|return
operator|(
name|principalName
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|principalName
operator|.
name|getString
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
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedPrincipalNames
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugValidPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|testGroup
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|TEST_GROUP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|Node
name|targetNode
init|=
name|getTargetNode
argument_list|()
decl_stmt|;
name|targetNode
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_CUG_POLICY
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugValidPrincipalsNoMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|testGroup
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|TEST_GROUP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_CUG_POLICY
argument_list|)
expr_stmt|;
try|try
block|{
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|CommitFailedException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|CommitFailedException
operator|)
name|cause
operator|)
operator|.
name|isAccessControlViolation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|22
argument_list|,
operator|(
operator|(
name|CommitFailedException
operator|)
name|cause
operator|)
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeWithCugValidPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|testGroup
operator|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|createGroup
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|TEST_GROUP_PRINCIPAL_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_CHILD_WITH_CUG
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugWithoutPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xmlCugPolicyWithoutPrincipals
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xmlCugPolicyWithoutPrincipals
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getTargetNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugWithEmptyPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xmlCugPolicyEmptyPrincipals
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"rep:cugPolicy\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\"></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|getTargetNode
argument_list|()
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xmlCugPolicyEmptyPrincipals
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|String
name|propPath
init|=
name|getTargetPath
argument_list|()
operator|+
literal|"/"
operator|+
name|CugConstants
operator|.
name|REP_CUG_POLICY
operator|+
literal|"/"
operator|+
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
decl_stmt|;
name|assertTrue
argument_list|(
name|getImportSession
argument_list|()
operator|.
name|propertyExists
argument_list|(
name|propPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[
literal|0
index|]
argument_list|,
name|getImportSession
argument_list|()
operator|.
name|getProperty
argument_list|(
name|propPath
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCug
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_NESTED_CUG_POLICY
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|getImportSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCugWithMixin
parameter_list|()
throws|throws
name|Exception
block|{
name|getTargetNode
argument_list|()
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_NESTED_CUG_POLICY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getTargetNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|cugPolicy
init|=
name|getTargetNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cugPolicy
operator|.
name|hasProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cugPolicy
operator|.
name|hasNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedCugSave
parameter_list|()
throws|throws
name|Exception
block|{
name|getTargetNode
argument_list|()
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_NESTED_CUG_POLICY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getTargetNode
argument_list|()
operator|.
name|hasNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|cugPolicy
init|=
name|getTargetNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cugPolicy
operator|.
name|hasProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cugPolicy
operator|.
name|hasNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugWithInvalidName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"someOtherNode\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:CugPolicy</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalNames\" sv:type=\"String\" sv:multiple=\"true\">"
operator|+
literal|"<sv:value>"
operator|+
name|EveryonePrincipal
operator|.
name|NAME
operator|+
literal|"</sv:value>"
operator|+
literal|"</sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|getTargetNode
argument_list|()
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
try|try
block|{
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|getImportSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugAtUnsupportedPath
parameter_list|()
throws|throws
name|Exception
block|{
name|doImport
argument_list|(
literal|"/"
argument_list|,
name|XML_CHILD_WITH_CUG
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|getImportSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"child"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|getImportSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"child/rep:cugPolicy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

