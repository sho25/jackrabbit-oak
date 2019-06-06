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
name|LinkedHashSet
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
name|AggregationFilter
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
enum|enum
name|CompositionType
block|{
comment|/**          * Break as soon as any one of the aggregated permission providers          * denies a privilege (default setup)          */
name|AND
block|,
comment|/**          * Check all aggregated permission providers for one that could provide          * a privilege (multiplexing setup)          */
name|OR
block|;
comment|/**          * Returns the corresponding composition type.          * @param type          *            String representation of the composition type, or          *            {@code null}          * @return corresponding composition type, or {@code AND} if the          *         provided type is {@code null}          */
specifier|static
name|CompositionType
name|fromString
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|)
block|{
name|String
name|or
init|=
name|OR
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|or
operator|.
name|equals
argument_list|(
name|type
argument_list|)
operator|||
name|or
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|OR
return|;
block|}
else|else
block|{
return|return
name|AND
return|;
block|}
block|}
block|}
specifier|private
name|CompositionType
name|compositionType
init|=
name|CompositionType
operator|.
name|AND
decl_stmt|;
specifier|private
name|AggregationFilter
name|aggregationFilter
init|=
name|AggregationFilter
operator|.
name|DEFAULT
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
name|NotNull
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
specifier|public
name|void
name|withCompositionType
parameter_list|(
annotation|@
name|Nullable
name|String
name|ct
parameter_list|)
block|{
name|this
operator|.
name|compositionType
operator|=
name|CompositionType
operator|.
name|fromString
argument_list|(
name|ct
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|withAggregationFilter
parameter_list|(
annotation|@
name|NotNull
name|AggregationFilter
name|aggregationFilter
parameter_list|)
block|{
name|this
operator|.
name|aggregationFilter
operator|=
name|aggregationFilter
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
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
name|authorizationConfiguration
lambda|->
name|authorizationConfiguration
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
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
argument_list|,
name|aggregationFilter
argument_list|)
return|;
block|}
block|}
annotation|@
name|NotNull
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
name|Set
argument_list|<
name|RestrictionProvider
argument_list|>
name|rps
init|=
operator|new
name|LinkedHashSet
argument_list|<>
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
name|RestrictionProvider
name|rp
init|=
name|c
operator|.
name|getRestrictionProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|RestrictionProvider
operator|.
name|EMPTY
operator|!=
name|rp
condition|)
block|{
name|rps
operator|.
name|add
argument_list|(
name|rp
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
name|NotNull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|String
name|workspaceName
parameter_list|,
annotation|@
name|NotNull
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
argument_list|<>
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
name|AggregatedPermissionProvider
name|aggrProvider
init|=
operator|(
name|AggregatedPermissionProvider
operator|)
name|pProvider
decl_stmt|;
name|aggrPermissionProviders
operator|.
name|add
argument_list|(
name|aggrProvider
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggregationFilter
operator|.
name|stop
argument_list|(
name|aggrProvider
argument_list|,
name|principals
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring permission provider of '{}': Not an AggregatedPermissionProvider"
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
name|CompositePermissionProvider
operator|.
name|create
argument_list|(
name|root
argument_list|,
name|aggrPermissionProviders
argument_list|,
name|getContext
argument_list|()
argument_list|,
name|compositionType
argument_list|,
name|getRootProvider
argument_list|()
argument_list|,
name|getTreeProvider
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

