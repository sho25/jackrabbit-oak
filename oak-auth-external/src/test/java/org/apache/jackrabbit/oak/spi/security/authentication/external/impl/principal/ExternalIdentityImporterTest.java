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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
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
name|security
operator|.
name|PrivilegedExceptionAction
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|authentication
operator|.
name|SystemSubject
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
name|authentication
operator|.
name|external
operator|.
name|TestSecurityProvider
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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

begin_comment
comment|/**  * Test XML import of external users/groups with protection of external identity  * properties turned on.  */
end_comment

begin_class
specifier|public
class|class
name|ExternalIdentityImporterTest
block|{
specifier|public
specifier|static
specifier|final
name|String
name|XML_EXTERNAL_USER
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:authorizableId\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>tPrinc</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:externalId\" sv:type=\"String\"><sv:value>idp;ext-t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:lastSynced\" sv:type=\"Date\"><sv:value>2016-05-03T10:03:08.061+02:00</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|XML_EXTERNAL_USER_WITH_PRINCIPAL_NAMES
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:authorizableId\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>tPrinc</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:externalId\" sv:type=\"String\"><sv:value>idp;ext-t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:externalPrincipalNames\" sv:type=\"String\"><sv:value>grPrinc</sv:value><sv:value>gr2Princ</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:lastSynced\" sv:type=\"Date\"><sv:value>2016-05-03T10:03:08.061+02:00</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
specifier|private
name|Repository
name|repo
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
name|SecurityProvider
name|securityProvider
init|=
name|TestSecurityProvider
operator|.
name|newTestSecurityProvider
argument_list|(
name|getConfigurationParameters
argument_list|()
argument_list|,
operator|new
name|ExternalPrincipalConfiguration
argument_list|()
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
block|}
annotation|@
name|Nonnull
name|ConfigurationParameters
name|getConfigurationParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
name|Session
name|createSession
parameter_list|(
name|boolean
name|isSystem
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|isSystem
condition|)
block|{
return|return
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Session
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Session
name|run
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
return|return
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
return|;
block|}
block|}
name|Node
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
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
return|return
name|importSession
operator|.
name|getNode
argument_list|(
name|parentPath
argument_list|)
return|;
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
name|assertHasProperties
parameter_list|(
annotation|@
name|Nonnull
name|Node
name|node
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|propertyNames
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|pN
range|:
name|propertyNames
control|)
block|{
name|assertTrue
argument_list|(
name|node
operator|.
name|hasProperty
argument_list|(
name|pN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|assertNotHasProperties
parameter_list|(
annotation|@
name|Nonnull
name|Node
name|node
parameter_list|,
annotation|@
name|Nonnull
name|String
modifier|...
name|propertyNames
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|pN
range|:
name|propertyNames
control|)
block|{
name|assertFalse
argument_list|(
name|node
operator|.
name|hasProperty
argument_list|(
name|pN
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|importExternalUser
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|createSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|doImport
argument_list|(
name|s
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|XML_EXTERNAL_USER
argument_list|)
decl_stmt|;
name|assertHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
expr_stmt|;
name|assertNotHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|importExternalUserAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|createSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|doImport
argument_list|(
name|s
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|XML_EXTERNAL_USER
argument_list|)
decl_stmt|;
name|assertHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
expr_stmt|;
name|assertNotHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|importExternalUserWithPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|createSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|doImport
argument_list|(
name|s
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|XML_EXTERNAL_USER_WITH_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
name|assertHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|assertNotHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|importExternalUserWithPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|createSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Node
name|parent
init|=
name|doImport
argument_list|(
name|s
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|,
name|XML_EXTERNAL_USER_WITH_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
name|assertHasProperties
argument_list|(
name|parent
operator|.
name|getNode
argument_list|(
literal|"t"
argument_list|)
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|,
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

