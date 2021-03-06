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
name|principalbased
operator|.
name|impl
package|;
end_package

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
name|JackrabbitAccessControlManager
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|principalbased
operator|.
name|impl
operator|.
name|Constants
operator|.
name|NT_REP_PRINCIPAL_POLICY
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
name|principalbased
operator|.
name|impl
operator|.
name|Constants
operator|.
name|REP_PRINCIPAL_NAME
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
name|principalbased
operator|.
name|impl
operator|.
name|Constants
operator|.
name|REP_PRINCIPAL_POLICY
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

begin_class
specifier|public
class|class
name|ImportBesteffortTest
extends|extends
name|ImportBaseTest
block|{
annotation|@
name|Override
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
name|Test
specifier|public
name|void
name|testTransientPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|transientSystemUser
init|=
name|getUserManager
argument_list|()
operator|.
name|createSystemUser
argument_list|(
literal|"transientSystemUser"
argument_list|,
name|INTERMEDIATE_PATH
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\""
operator|+
name|REP_PRINCIPAL_POLICY
operator|+
literal|"\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\""
operator|+
name|JCR_PRIMARYTYPE
operator|+
literal|"\" sv:type=\"Name\"><sv:value>"
operator|+
name|NT_REP_PRINCIPAL_POLICY
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\""
operator|+
name|REP_PRINCIPAL_NAME
operator|+
literal|"\" sv:type=\"String\"><sv:value>"
operator|+
name|transientSystemUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
decl_stmt|;
name|doImport
argument_list|(
name|transientSystemUser
operator|.
name|getPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|PrincipalPolicyImpl
name|policy
init|=
name|getPrincipalPolicyImpl
argument_list|(
name|transientSystemUser
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|getAccessControlManager
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|policy
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|transientSystemUser
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policy
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

