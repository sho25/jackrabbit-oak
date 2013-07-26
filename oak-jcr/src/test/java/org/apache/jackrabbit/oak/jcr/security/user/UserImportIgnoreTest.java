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
name|user
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
name|List
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|principal
operator|.
name|PrincipalIterator
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
name|Authorizable
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
name|Impersonation
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
name|xml
operator|.
name|ImportBehavior
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
comment|/**  * Testing {@link ImportBehavior#IGNORE} for user/group import  */
end_comment

begin_class
specifier|public
class|class
name|UserImportIgnoreTest
extends|extends
name|AbstractImportTest
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_IGNORE
return|;
block|}
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getPathsToRemove
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|GROUPPATH
operator|+
literal|"/gFolder"
argument_list|,
name|USERPATH
operator|+
literal|"/t"
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportSelfAsGroupIgnore
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|invalidId
init|=
literal|"0120a4f9-196a-3f9e-b9f5-23f31f914da7"
decl_stmt|;
comment|// uuid of the group itself
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// no exception during import -> member must have been ignored though.
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|assertNotDeclaredMember
argument_list|(
operator|(
name|Group
operator|)
name|a
argument_list|,
name|invalidId
argument_list|,
name|adminSession
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"'g1' was not imported as Group."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportInvalidImpersonationIgnore
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"anybody"
argument_list|)
expr_stmt|;
comment|// an non-existing princ-name
name|invalid
operator|.
name|add
argument_list|(
literal|"administrators"
argument_list|)
expr_stmt|;
comment|// a group
name|invalid
operator|.
name|add
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
comment|// principal of the user itself.
for|for
control|(
name|String
name|principalName
range|:
name|invalid
control|)
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<sv:node sv:name=\"t\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:password\" sv:type=\"String\"><sv:value>{sha1}8efd86fb78a56a5145ed7739dcb00c78581c5375</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property><sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>"
operator|+
name|principalName
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|Subject
name|subj
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|subj
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|doImport
argument_list|(
name|USERPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// no exception during import: no impersonation must be granted
comment|// for the invalid principal name
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|Impersonation
name|imp
init|=
operator|(
operator|(
name|User
operator|)
name|a
operator|)
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|Subject
name|s
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|s
operator|.
name|getPrincipals
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|imp
operator|.
name|allows
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PrincipalIterator
name|it
init|=
name|imp
operator|.
name|getImpersonators
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertFalse
argument_list|(
name|principalName
operator|.
name|equals
argument_list|(
name|it
operator|.
name|nextPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Importing 't' didn't create a User."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|adminSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportNonExistingMemberIgnore
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// random uuid
name|invalid
operator|.
name|add
argument_list|(
name|getExistingUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// uuid of non-authorizable node
for|for
control|(
name|String
name|id
range|:
name|invalid
control|)
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|id
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
try|try
block|{
comment|// there should be no exception during import,
comment|// but invalid members must be ignored.
name|doImport
argument_list|(
name|GROUPPATH
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"g1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|assertNotDeclaredMember
argument_list|(
operator|(
name|Group
operator|)
name|a
argument_list|,
name|id
argument_list|,
name|adminSession
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"'g1' was not imported as Group."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|adminSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

