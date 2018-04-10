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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|List
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
name|annotation
operator|.
name|CheckForNull
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
name|spi
operator|.
name|security
operator|.
name|AbstractCompositeConfigurationTest
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
name|ConfigurationBase
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|CompositePrincipalConfigurationTest
extends|extends
name|AbstractCompositeConfigurationTest
argument_list|<
name|PrincipalConfiguration
argument_list|>
block|{
specifier|private
specifier|final
name|Root
name|root
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PrincipalConfiguration
name|principalConfigurationMock
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|compositeConfiguration
operator|=
operator|new
name|CompositePrincipalConfiguration
argument_list|()
expr_stmt|;
name|principalConfigurationMock
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PrincipalConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|principalConfigurationMock
operator|.
name|getParameters
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertSize
parameter_list|(
name|int
name|expected
parameter_list|,
name|CompositePrincipalProvider
name|pp
parameter_list|)
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|CompositePrincipalProvider
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"providers"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PrincipalProvider
argument_list|>
name|providers
init|=
operator|(
name|List
argument_list|<
name|PrincipalProvider
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
name|pp
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|providers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyGetPrincipalManager
parameter_list|()
block|{
name|PrincipalManager
name|pMgr
init|=
name|getComposite
argument_list|()
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
name|assertTrue
argument_list|(
name|pMgr
operator|instanceof
name|PrincipalManagerImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyGetProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|PrincipalProvider
name|pp
init|=
name|getComposite
argument_list|()
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
name|assertFalse
argument_list|(
name|pp
operator|instanceof
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|EmptyPrincipalProvider
operator|.
name|INSTANCE
argument_list|,
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleGetPrincipalManager
parameter_list|()
block|{
name|PrincipalConfiguration
name|testConfig
init|=
operator|new
name|TestPrincipalConfiguration
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|PrincipalManager
name|pMgr
init|=
name|getComposite
argument_list|()
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
name|assertTrue
argument_list|(
name|pMgr
operator|instanceof
name|PrincipalManagerImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleGetProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|PrincipalConfiguration
name|testConfig
init|=
operator|new
name|TestPrincipalConfiguration
argument_list|()
decl_stmt|;
name|addConfiguration
argument_list|(
name|testConfig
argument_list|)
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|getComposite
argument_list|()
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
name|assertFalse
argument_list|(
name|pp
operator|instanceof
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testConfig
operator|.
name|getPrincipalProvider
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
name|testMultipleGetPrincipalManager
parameter_list|()
block|{
name|addConfiguration
argument_list|(
name|principalConfigurationMock
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
operator|new
name|TestPrincipalConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|PrincipalManager
name|pMgr
init|=
name|getComposite
argument_list|()
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
name|assertTrue
argument_list|(
name|pMgr
operator|instanceof
name|PrincipalManagerImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleGetPrincipalProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|addConfiguration
argument_list|(
name|principalConfigurationMock
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
operator|new
name|TestPrincipalConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|getComposite
argument_list|()
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
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|assertSize
argument_list|(
literal|2
argument_list|,
operator|(
name|CompositePrincipalProvider
operator|)
name|pp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithEmptyPrincipalProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|addConfiguration
argument_list|(
operator|new
name|TestEmptyConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|PrincipalProvider
name|pp
init|=
name|getComposite
argument_list|()
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
name|assertSame
argument_list|(
name|EmptyPrincipalProvider
operator|.
name|INSTANCE
argument_list|,
name|pp
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
operator|new
name|TestPrincipalConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|pp
operator|=
name|getComposite
argument_list|()
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
name|assertFalse
argument_list|(
name|pp
operator|instanceof
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
name|principalConfigurationMock
argument_list|)
expr_stmt|;
name|pp
operator|=
name|getComposite
argument_list|()
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
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|assertSize
argument_list|(
literal|2
argument_list|,
operator|(
name|CompositePrincipalProvider
operator|)
name|pp
argument_list|)
expr_stmt|;
name|addConfiguration
argument_list|(
operator|new
name|TestEmptyConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|pp
operator|=
name|getComposite
argument_list|()
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
name|assertTrue
argument_list|(
name|pp
operator|instanceof
name|CompositePrincipalProvider
argument_list|)
expr_stmt|;
name|assertSize
argument_list|(
literal|2
argument_list|,
operator|(
name|CompositePrincipalProvider
operator|)
name|pp
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
class|class
name|TestPrincipalConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|PrincipalConfiguration
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PrincipalManagerImpl
argument_list|(
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|TestPrincipalProvider
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|PrincipalConfiguration
operator|.
name|NAME
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TestPrincipalProvider
implements|implements
name|PrincipalProvider
block|{
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getMembershipPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|ImmutableSet
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
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|getPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userID
parameter_list|)
block|{
return|return
name|ImmutableSet
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
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|findPrincipals
parameter_list|(
annotation|@
name|Nullable
name|String
name|nameHint
parameter_list|,
name|int
name|searchType
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|findPrincipals
parameter_list|(
name|int
name|searchType
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|final
class|class
name|TestEmptyConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|PrincipalConfiguration
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PrincipalManagerImpl
argument_list|(
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
name|EmptyPrincipalProvider
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|PrincipalConfiguration
operator|.
name|NAME
return|;
block|}
block|}
block|}
end_class

end_unit

