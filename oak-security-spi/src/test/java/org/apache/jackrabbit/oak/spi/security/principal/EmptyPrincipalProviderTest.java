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
name|principal
package|;
end_package

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
name|assertNull
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
name|PrincipalManager
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

begin_class
specifier|public
class|class
name|EmptyPrincipalProviderTest
block|{
specifier|private
name|PrincipalProvider
name|principalProvider
init|=
name|EmptyPrincipalProvider
operator|.
name|INSTANCE
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"testUser"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipal
parameter_list|()
block|{
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetMembershipPrincipals
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|principalProvider
operator|.
name|getMembershipPrincipals
argument_list|(
name|testPrincipal
argument_list|)
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
name|testGetPrincipals
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|principalProvider
operator|.
name|getPrincipals
argument_list|(
literal|"userId"
argument_list|)
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
name|testFindPrincipalsByHint
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|testPrincipal
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|,
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFindPrincipalsByType
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|)
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

