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
name|ImportIgnoreTest
block|{
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
name|Test
specifier|public
name|void
name|testImportUnknownPrincipal
parameter_list|()
throws|throws
name|Exception
block|{
name|runImport
argument_list|()
expr_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|adminSession
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|AccessControlPolicy
index|[]
name|policies
init|=
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|target
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|policies
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|policies
index|[
literal|0
index|]
operator|instanceof
name|JackrabbitAccessControlList
argument_list|)
expr_stmt|;
name|AccessControlEntry
index|[]
name|entries
init|=
operator|(
operator|(
name|JackrabbitAccessControlList
operator|)
name|policies
index|[
literal|0
index|]
operator|)
operator|.
name|getAccessControlEntries
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entries
operator|.
name|length
argument_list|)
expr_stmt|;
name|AccessControlEntry
name|entry
init|=
name|entries
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unknownprincipal"
argument_list|,
name|entry
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|entry
operator|.
name|getPrivileges
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_WRITE
argument_list|)
argument_list|,
name|entry
operator|.
name|getPrivileges
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|instanceof
name|JackrabbitAccessControlEntry
condition|)
block|{
name|assertTrue
argument_list|(
operator|(
operator|(
name|JackrabbitAccessControlEntry
operator|)
name|entry
operator|)
operator|.
name|isAllow
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddEntry
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Principal
name|unknown
init|=
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"anotherUnknown"
return|;
block|}
block|}
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|target
operator|.
name|getPath
argument_list|()
argument_list|,
name|unknown
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
block|}
block|}
end_class

end_unit

