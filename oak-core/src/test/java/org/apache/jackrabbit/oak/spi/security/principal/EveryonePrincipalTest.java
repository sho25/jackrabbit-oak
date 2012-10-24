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
name|Enumeration
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
name|JackrabbitPrincipal
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
name|security
operator|.
name|AbstractSecurityTest
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
name|assertSame
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
name|EveryonePrincipalTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|Principal
name|everyone
init|=
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|everyone
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSame
parameter_list|()
block|{
name|assertSame
argument_list|(
name|everyone
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|everyone
operator|.
name|hashCode
argument_list|()
operator|==
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEqualsOtherPrincipalWithSameName
parameter_list|()
block|{
name|Principal
name|someotherEveryone
init|=
operator|new
name|Principal
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|EveryonePrincipal
operator|.
name|NAME
return|;
block|}
block|}
decl_stmt|;
name|assertFalse
argument_list|(
name|everyone
operator|.
name|equals
argument_list|(
name|someotherEveryone
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsOtherJackrabbitPrincipal
parameter_list|()
block|{
name|Principal
name|someotherEveryone
init|=
operator|new
name|OtherEveryone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|someotherEveryone
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsOtherJackrabbitGroup
parameter_list|()
block|{
name|Principal
name|someotherEveryone
init|=
operator|new
name|OtherEveryoneGroup
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|everyone
argument_list|,
name|someotherEveryone
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
class|class
name|OtherEveryone
implements|implements
name|JackrabbitPrincipal
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|EveryonePrincipal
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|JackrabbitPrincipal
condition|)
block|{
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|JackrabbitPrincipal
operator|)
name|o
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|OtherEveryoneGroup
extends|extends
name|OtherEveryone
implements|implements
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|addMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
comment|// TODO
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|()
block|{
comment|// TODO
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

