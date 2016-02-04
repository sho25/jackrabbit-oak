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
name|authorization
operator|.
name|composite
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
name|base
operator|.
name|Function
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
name|Lists
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
name|AggregatedPermissionProvider
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
name|EmptyPermissionProvider
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
name|CompositeRestrictionProvider
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * {@link CompositeAuthorizationConfiguration} that combines different  * authorization models. This implementation has the following characteristics:  *  *<h2>AccessControlManager</h2>  *<ul>  *<li>This method will return an aggregation of {@code AccessControlManager}s in case  *     multiple {@code AuthorizationConfiguration}s are present (see {@code CompositeAccessControlManager}).</li>  *<li>If the composite only contains a single entry the {@code AccessControlManager}  *     of this implementation is return without extra wrapping.</li>  *<li>If the list of configurations is empty an {@code IllegalStateException} is thrown.</li>  *</ul>  *  *<h2>PermissionProvider</h2>  *<ul>  *<li>This method will return an aggregation of {@code PermissionProvider}s in case  *     multiple {@code AuthorizationConfiguration}s exposing an {@link AggregatedPermissionProvider}  *     are present (see {@link CompositePermissionProvider}. Note however, that  *     providers not implementing the {@code AggregatedPermissionProvider} extension  *     will be ignored.</li>  *<li>If the composite only contains a single entry the {@code PermissionProvider}  *     of this implementation is return without extra wrapping.</li>  *<li>If the list of configurations is empty an {@code IllegalStateException} is thrown.</li>  *</ul>  *  *<h2>RestrictionProvider</h2>   *<ul>   *<li>This method will return an aggregation of {@code RestrictionProvider}s in case   *     multiple {@code AuthorizationConfiguration}s are present (see {@code CompositeRestrictionProvider}).</li>   *<li>If the composite only contains a single entry the {@code RestrictionProvider}   *     of this implementation is return without extra wrapping.</li>   *<li>If the list of configurations is empty {@link RestrictionProvider#EMPTY } is returned.</li>   *</ul>  *  */
end_comment

begin_class
specifier|public
class|class
name|CompositeAuthorizationConfiguration
extends|extends
name|CompositeConfiguration
argument_list|<
name|AuthorizationConfiguration
argument_list|>
implements|implements
name|AuthorizationConfiguration
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CompositeAuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|CompositeAuthorizationConfiguration
parameter_list|()
block|{
name|super
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CompositeAuthorizationConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|AuthorizationConfiguration
operator|.
name|NAME
argument_list|,
name|securityProvider
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizationConfiguration
argument_list|>
name|configurations
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|configurations
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
case|case
literal|1
case|:
return|return
name|configurations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
default|default:
name|List
argument_list|<
name|AccessControlManager
argument_list|>
name|mgrs
init|=
name|Lists
operator|.
name|transform
argument_list|(
name|configurations
argument_list|,
operator|new
name|Function
argument_list|<
name|AuthorizationConfiguration
argument_list|,
name|AccessControlManager
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|apply
parameter_list|(
name|AuthorizationConfiguration
name|authorizationConfiguration
parameter_list|)
block|{
return|return
name|authorizationConfiguration
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|,
name|mgrs
argument_list|)
return|;
block|}
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
name|List
argument_list|<
name|AuthorizationConfiguration
argument_list|>
name|configurations
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|configurations
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|RestrictionProvider
operator|.
name|EMPTY
return|;
case|case
literal|1
case|:
return|return
name|configurations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getRestrictionProvider
argument_list|()
return|;
default|default:
name|List
argument_list|<
name|RestrictionProvider
argument_list|>
name|rps
init|=
operator|new
name|ArrayList
argument_list|<
name|RestrictionProvider
argument_list|>
argument_list|(
name|configurations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AuthorizationConfiguration
name|c
range|:
name|configurations
control|)
block|{
if|if
condition|(
name|RestrictionProvider
operator|.
name|EMPTY
operator|!=
name|c
condition|)
block|{
name|rps
operator|.
name|add
argument_list|(
name|c
operator|.
name|getRestrictionProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|rps
argument_list|)
return|;
block|}
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
specifier|final
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizationConfiguration
argument_list|>
name|configurations
init|=
name|getConfigurations
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|configurations
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
case|case
literal|1
case|:
return|return
name|configurations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|)
return|;
default|default:
name|List
argument_list|<
name|AggregatedPermissionProvider
argument_list|>
name|aggrPermissionProviders
init|=
operator|new
name|ArrayList
argument_list|(
name|configurations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AuthorizationConfiguration
name|conf
range|:
name|configurations
control|)
block|{
name|PermissionProvider
name|pProvider
init|=
name|conf
operator|.
name|getPermissionProvider
argument_list|(
name|root
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|)
decl_stmt|;
if|if
condition|(
name|pProvider
operator|instanceof
name|AggregatedPermissionProvider
condition|)
block|{
name|aggrPermissionProviders
operator|.
name|add
argument_list|(
operator|(
name|AggregatedPermissionProvider
operator|)
name|pProvider
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring permission provider of '{}': missing implementation of AggregatedPermissionProvider"
argument_list|,
name|conf
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|PermissionProvider
name|pp
decl_stmt|;
switch|switch
condition|(
name|aggrPermissionProviders
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|pp
operator|=
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|pp
operator|=
name|aggrPermissionProviders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default :
name|pp
operator|=
operator|new
name|CompositePermissionProvider
argument_list|(
name|root
argument_list|,
name|aggrPermissionProviders
argument_list|,
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|pp
return|;
block|}
block|}
block|}
end_class

end_unit

