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
name|user
operator|.
name|action
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|PropertyOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|commons
operator|.
name|PropertiesUtil
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
name|SecurityProvider
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
comment|/**  * Default implementation of the {@link AuthorizableActionProvider} interface  * that allows to config all actions provided by the OAK.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak AuthorizableActionProvider"
argument_list|)
annotation|@
name|Service
argument_list|(
name|AuthorizableActionProvider
operator|.
name|class
argument_list|)
annotation|@
name|Properties
argument_list|(
block|{
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|DefaultAuthorizableActionProvider
operator|.
name|ENABLED_ACTIONS
argument_list|,
name|label
operator|=
literal|"Authorizable Actions"
argument_list|,
name|description
operator|=
literal|"The set of actions that is supported by this provider implementation."
argument_list|,
name|cardinality
operator|=
literal|4
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"org.apache.jackrabbit.oak.spi.security.user.action.AccessControlAction"
argument_list|,
name|value
operator|=
literal|"AccessControlAction"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"org.apache.jackrabbit.oak.spi.security.user.action.PasswordValidationAction"
argument_list|,
name|value
operator|=
literal|"PasswordValidationAction"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"org.apache.jackrabbit.oak.spi.security.user.action.PasswordChangeAction"
argument_list|,
name|value
operator|=
literal|"PasswordChangeAction"
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
literal|"org.apache.jackrabbit.oak.spi.security.user.action.ClearMembershipAction"
argument_list|,
name|value
operator|=
literal|"ClearMembershipAction"
argument_list|)
block|}
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|AccessControlAction
operator|.
name|USER_PRIVILEGE_NAMES
argument_list|,
name|label
operator|=
literal|"Configure AccessControlAction: User Privileges"
argument_list|,
name|description
operator|=
literal|"The name of the privileges that should be granted to a given user on it's home."
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|AccessControlAction
operator|.
name|GROUP_PRIVILEGE_NAMES
argument_list|,
name|label
operator|=
literal|"Configure AccessControlAction: Group Privileges"
argument_list|,
name|description
operator|=
literal|"The name of the privileges that should be granted to a given group on it's home."
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|PasswordValidationAction
operator|.
name|CONSTRAINT
argument_list|,
name|label
operator|=
literal|"Configure PasswordValidationAction: Password Constraint"
argument_list|,
name|description
operator|=
literal|"A regular expression specifying the pattern that must be matched by a user's password."
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|DefaultAuthorizableActionProvider
implements|implements
name|AuthorizableActionProvider
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
name|DefaultAuthorizableActionProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
argument_list|>
name|SUPPORTED_ACTIONS
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Class
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
decl|>
name|of
argument_list|(
name|AccessControlAction
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AccessControlAction
operator|.
name|class
argument_list|,
name|PasswordValidationAction
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|PasswordValidationAction
operator|.
name|class
argument_list|,
name|PasswordChangeAction
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|PasswordChangeAction
operator|.
name|class
argument_list|,
name|ClearMembershipAction
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|ClearMembershipAction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_ACTIONS
init|=
operator|new
name|String
index|[]
block|{
name|AccessControlAction
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
specifier|static
specifier|final
name|String
name|ENABLED_ACTIONS
init|=
literal|"enabledActions"
decl_stmt|;
specifier|private
name|String
index|[]
name|enabledActions
init|=
name|DEFAULT_ACTIONS
decl_stmt|;
specifier|private
name|ConfigurationParameters
name|config
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|DefaultAuthorizableActionProvider
parameter_list|()
block|{}
specifier|public
name|DefaultAuthorizableActionProvider
parameter_list|(
name|ConfigurationParameters
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|activate
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
comment|//-----------------------------------------< AuthorizableActionProvider>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|getAuthorizableActions
parameter_list|(
annotation|@
name|Nonnull
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizableAction
argument_list|>
name|actions
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|className
range|:
name|enabledActions
control|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|AuthorizableAction
argument_list|>
name|cl
init|=
name|SUPPORTED_ACTIONS
operator|.
name|get
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
name|cl
operator|!=
literal|null
condition|)
block|{
name|AuthorizableAction
name|action
init|=
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|action
operator|.
name|init
argument_list|(
name|securityProvider
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|actions
operator|.
name|add
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Unable to create authorizable action"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|actions
return|;
block|}
comment|//----------------------------------------------------< SCR Integration>---
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|config
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|enabledActions
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
name|ENABLED_ACTIONS
argument_list|,
name|DEFAULT_ACTIONS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

