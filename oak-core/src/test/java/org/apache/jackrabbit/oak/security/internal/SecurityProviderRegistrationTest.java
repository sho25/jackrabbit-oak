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
name|internal
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Nonnull
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|PropertyState
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
name|api
operator|.
name|Tree
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|plugins
operator|.
name|tree
operator|.
name|TreeLocation
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
name|authorization
operator|.
name|AuthorizationConfigurationImpl
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
name|authorization
operator|.
name|composite
operator|.
name|CompositeAuthorizationConfiguration
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
name|principal
operator|.
name|PrincipalConfigurationImpl
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
name|CompositeConfiguration
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
name|authorization
operator|.
name|AuthorizationConfiguration
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
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
name|CompositePrincipalConfiguration
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
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|Constants
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
name|SecurityProviderRegistrationTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|PROPS
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
literal|"prop"
argument_list|,
literal|"val"
argument_list|)
decl_stmt|;
specifier|private
name|SecurityProviderRegistration
name|registration
init|=
operator|new
name|SecurityProviderRegistration
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|void
name|assertContext
parameter_list|(
annotation|@
name|Nonnull
name|Context
name|context
parameter_list|,
name|int
name|expectedSize
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
name|boolean
name|isDefined
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
name|c
init|=
name|context
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"CompositeContext"
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|c
operator|.
name|getDeclaredField
argument_list|(
literal|"delegatees"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedSize
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|f
operator|.
name|get
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|expectedSize
argument_list|,
operator|(
operator|(
name|Context
index|[]
operator|)
name|f
operator|.
name|get
argument_list|(
name|context
argument_list|)
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|isDefined
argument_list|,
name|context
operator|.
name|definesContextRoot
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isDefined
argument_list|,
name|context
operator|.
name|definesTree
argument_list|(
name|tree
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isDefined
argument_list|,
name|context
operator|.
name|definesProperty
argument_list|(
name|tree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"abc"
argument_list|,
literal|"abc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|isDefined
argument_list|,
name|context
operator|.
name|definesLocation
argument_list|(
name|TreeLocation
operator|.
name|create
argument_list|(
name|tree
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizationRanking
parameter_list|()
throws|throws
name|Exception
block|{
name|Field
name|f
init|=
name|registration
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"authorizationConfiguration"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|testAc
init|=
operator|new
name|TestAuthorizationConfiguration
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|testAc
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|AuthorizationConfigurationImpl
name|ac
init|=
operator|new
name|AuthorizationConfigurationImpl
argument_list|()
decl_stmt|;
name|ac
operator|.
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|CompositeConfiguration
operator|.
name|PARAM_RANKING
argument_list|,
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|ac
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|testAc2
init|=
operator|new
name|TestAuthorizationConfiguration
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|Constants
operator|.
name|SERVICE_RANKING
argument_list|,
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|testAc2
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|CompositeAuthorizationConfiguration
name|cac
init|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AuthorizationConfiguration
argument_list|>
name|list
init|=
name|cac
operator|.
name|getConfigurations
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ac
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|testAc2
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|testAc
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizationContext
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Field
name|f
init|=
name|registration
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"authorizationConfiguration"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|ac
init|=
operator|new
name|AuthorizationConfigurationImpl
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|ac
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|CompositeAuthorizationConfiguration
name|cac
init|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
decl_stmt|;
name|Context
name|ctx
init|=
name|cac
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|1
argument_list|,
name|t
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|ac1
init|=
operator|new
name|TestAuthorizationConfiguration
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|ac1
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cac
operator|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cac
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|AuthorizationConfiguration
name|ac2
init|=
operator|new
name|TestAuthorizationConfiguration
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindAuthorizationConfiguration
argument_list|(
name|ac2
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cac
operator|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cac
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|3
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// unbind again:
name|registration
operator|.
name|unbindAuthorizationConfiguration
argument_list|(
name|ac1
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cac
operator|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cac
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration
operator|.
name|unbindAuthorizationConfiguration
argument_list|(
name|ac
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cac
operator|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cac
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|1
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration
operator|.
name|unbindAuthorizationConfiguration
argument_list|(
name|ac2
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cac
operator|=
operator|(
name|CompositeAuthorizationConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cac
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPrincipalContext
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Field
name|f
init|=
name|registration
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"principalConfiguration"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PrincipalConfiguration
name|pc
init|=
operator|new
name|PrincipalConfigurationImpl
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindPrincipalConfiguration
argument_list|(
name|pc
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|CompositePrincipalConfiguration
name|cpc
init|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
decl_stmt|;
name|Context
name|ctx
init|=
name|cpc
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|// expected size = 0 because PrincipalConfigurationImpl comes with the default ctx
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|PrincipalConfiguration
name|pc1
init|=
operator|new
name|TestPrincipalConfiguration
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindPrincipalConfiguration
argument_list|(
name|pc1
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cpc
operator|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cpc
operator|.
name|getContext
argument_list|()
expr_stmt|;
comment|// expected size 1 because the PrincipalConfigurationImpl comes with the default ctx
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|1
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|PrincipalConfiguration
name|pc2
init|=
operator|new
name|TestPrincipalConfiguration
argument_list|()
decl_stmt|;
name|registration
operator|.
name|bindPrincipalConfiguration
argument_list|(
name|pc2
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cpc
operator|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cpc
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// unbind again:
name|registration
operator|.
name|unbindPrincipalConfiguration
argument_list|(
name|pc
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cpc
operator|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cpc
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration
operator|.
name|unbindPrincipalConfiguration
argument_list|(
name|pc1
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cpc
operator|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cpc
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|1
argument_list|,
name|t
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registration
operator|.
name|unbindPrincipalConfiguration
argument_list|(
name|pc2
argument_list|,
name|PROPS
argument_list|)
expr_stmt|;
name|cpc
operator|=
operator|(
name|CompositePrincipalConfiguration
operator|)
name|f
operator|.
name|get
argument_list|(
name|registration
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|cpc
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|assertContext
argument_list|(
name|ctx
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|TestAuthorizationConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|AuthorizationConfiguration
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
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
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
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
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
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
name|Context
name|getContext
parameter_list|()
block|{
return|return
operator|new
name|ContextImpl
argument_list|()
return|;
block|}
block|}
specifier|private
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
literal|null
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
literal|null
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
operator|new
name|ContextImpl
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ContextImpl
implements|implements
name|Context
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

