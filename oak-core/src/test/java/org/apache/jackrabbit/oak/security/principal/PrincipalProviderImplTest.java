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
name|security
operator|.
name|principal
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
name|Iterator
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
name|GroupPrincipal
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
name|PrincipalProvider
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

begin_class
specifier|public
class|class
name|PrincipalProviderImplTest
extends|extends
name|AbstractPrincipalProviderTest
block|{
specifier|protected
name|PrincipalProvider
name|createPrincipalProvider
parameter_list|()
block|{
return|return
operator|new
name|PrincipalProviderImpl
argument_list|(
name|root
argument_list|,
name|getUserConfiguration
argument_list|()
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneMembers
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|everyone
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|everyone
operator|instanceof
name|EveryonePrincipal
argument_list|)
expr_stmt|;
name|Group
name|everyoneGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|everyoneGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Principal
name|ep
init|=
name|principalProvider
operator|.
name|getPrincipal
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|everyoneMembers
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|Collections
operator|.
name|list
argument_list|(
operator|(
operator|(
name|GroupPrincipal
operator|)
name|ep
operator|)
operator|.
name|members
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|all
init|=
name|principalProvider
operator|.
name|findPrincipals
argument_list|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
argument_list|)
decl_stmt|;
while|while
condition|(
name|all
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Principal
name|p
init|=
name|all
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|everyone
operator|.
name|equals
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|assertFalse
argument_list|(
name|everyoneMembers
operator|.
name|contains
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|everyoneMembers
operator|.
name|contains
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|everyoneGroup
operator|!=
literal|null
condition|)
block|{
name|everyoneGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

