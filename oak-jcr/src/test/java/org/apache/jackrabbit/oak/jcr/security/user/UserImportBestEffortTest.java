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
name|security
operator|.
name|Principal
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
name|ItemExistsException
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

begin_comment
comment|/**  * Testing {@link ImportBehavior#BESTEFFORT} for user import  */
end_comment

begin_class
specifier|public
class|class
name|UserImportBestEffortTest
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
name|NAME_BESTEFFORT
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|USERPATH
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportUuidCollisionRemoveExisting
parameter_list|()
throws|throws
name|Exception
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// re-import should succeed if UUID-behavior is set accordingly
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/**      * Same as {@link #testImportUuidCollisionRemoveExisting} with the single      * difference that the initial import is saved before being overwritten.      */
annotation|@
name|Test
specifier|public
name|void
name|testImportUuidCollisionRemoveExisting2
parameter_list|()
throws|throws
name|Exception
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// re-import should succeed if UUID-behavior is set accordingly
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_REMOVE_EXISTING
argument_list|)
expr_stmt|;
comment|// saving changes of the import -> must succeed. add mandatory
comment|// props should have been created.
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
name|testImportUuidCollisionThrow
parameter_list|()
throws|throws
name|Exception
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
try|try
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_COLLISION_THROW
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"UUID collision must be handled according to the uuid behavior."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ItemExistsException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportImpersonationBestEffort
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"uFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"t\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>e358efa4-89f5-3062-b10d-d7316b65649e</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:User</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>b2f5ff47-4366-31b6-a533-d8dc3614845d</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|newUser
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newUser
argument_list|)
expr_stmt|;
name|Authorizable
name|u2
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|u2
argument_list|)
expr_stmt|;
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
name|u2
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|Impersonation
name|imp
init|=
operator|(
operator|(
name|User
operator|)
name|newUser
operator|)
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|imp
operator|.
name|allows
argument_list|(
name|subj
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportNonExistingImpersonationBestEffort
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|principalName
init|=
literal|"anybody"
decl_stmt|;
comment|// an non-existing princ-name
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
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>t</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:impersonators\" sv:type=\"String\"><sv:value>"
operator|+
name|principalName
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|isGroup
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|PrincipalIterator
name|it
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
operator|.
name|getImpersonators
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|it
operator|.
name|nextPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principalName
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

