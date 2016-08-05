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
name|annotation
operator|.
name|Nullable
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
name|oak
operator|.
name|AbstractSecurityTest
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|user
operator|.
name|UserConfigurationImpl
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
name|Context
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
name|SecurityConfiguration
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
name|principal
operator|.
name|PrincipalConfiguration
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
name|PrincipalManagerImpl
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
name|UserConfiguration
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
name|PrincipalConfigurationImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|PrincipalConfigurationImpl
name|pc1
decl_stmt|;
specifier|private
name|PrincipalConfigurationImpl
name|pc2
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|pc1
operator|=
operator|new
name|PrincipalConfigurationImpl
argument_list|()
expr_stmt|;
name|pc2
operator|=
operator|new
name|PrincipalConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|pc1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PrincipalConfiguration
operator|.
name|NAME
argument_list|,
name|pc2
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
name|testGetContext
parameter_list|()
block|{
name|assertSame
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|,
name|pc1
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|Context
operator|.
name|DEFAULT
argument_list|,
name|pc2
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetParameters
parameter_list|()
block|{
name|assertSame
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|pc1
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|,
name|pc2
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrincipalManager
parameter_list|()
block|{
name|pc1
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalManager2
parameter_list|()
block|{
name|pc1
operator|.
name|setSecurityProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|PrincipalManager
name|pm
init|=
name|pc1
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pm
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pm
operator|instanceof
name|PrincipalManagerImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalManager3
parameter_list|()
block|{
name|PrincipalManager
name|pm
init|=
name|pc2
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pm
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pm
operator|instanceof
name|PrincipalManagerImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGetPrincipalProvider
parameter_list|()
block|{
name|pc1
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalProvider2
parameter_list|()
block|{
name|pc1
operator|.
name|setSecurityProvider
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|pc1
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getUserPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|,
name|pp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalProvider3
parameter_list|()
block|{
name|PrincipalProvider
name|pp
init|=
name|pc2
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|getUserConfiguration
argument_list|()
operator|.
name|getUserPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|,
name|pp
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetPrincipalProvider4
parameter_list|()
block|{
name|PrincipalConfigurationImpl
name|pc3
init|=
operator|new
name|PrincipalConfigurationImpl
argument_list|()
decl_stmt|;
specifier|final
name|SecurityProvider
name|sp
init|=
operator|new
name|SecurityProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|getConfigurations
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
if|if
condition|(
name|configClass
operator|.
name|equals
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
operator|(
name|T
operator|)
operator|new
name|UserConfigurationImpl
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getUserPrincipalProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
block|}
decl_stmt|;
name|pc3
operator|.
name|setSecurityProvider
argument_list|(
name|sp
argument_list|)
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|pc3
operator|.
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|PrincipalProviderImpl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

