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
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|HashSet
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
name|AccessControlException
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
name|Strings
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
name|Iterables
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
name|Sets
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
name|authorization
operator|.
name|cug
operator|.
name|CugExclude
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
name|cug
operator|.
name|CugPolicy
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
comment|/**  * Implementation of the {@link org.apache.jackrabbit.oak.spi.security.authorization.cug.CugPolicy}  * interface that respects the configured {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior}.  */
end_comment

begin_class
class|class
name|CugPolicyImpl
implements|implements
name|CugPolicy
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
name|CugPolicyImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|oakPath
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
specifier|final
name|int
name|importBehavior
decl_stmt|;
specifier|private
specifier|final
name|CugExclude
name|cugExclude
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|CugPolicyImpl
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|NotNull
name|PrincipalManager
name|principalManager
parameter_list|,
name|int
name|importBehavior
parameter_list|,
annotation|@
name|NotNull
name|CugExclude
name|cugExclude
parameter_list|)
block|{
name|this
argument_list|(
name|oakPath
argument_list|,
name|namePathMapper
argument_list|,
name|principalManager
argument_list|,
name|importBehavior
argument_list|,
name|cugExclude
argument_list|,
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|CugPolicyImpl
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|NotNull
name|PrincipalManager
name|principalManager
parameter_list|,
name|int
name|importBehavior
parameter_list|,
annotation|@
name|NotNull
name|CugExclude
name|cugExclude
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|ImportBehavior
operator|.
name|nameFromValue
argument_list|(
name|importBehavior
argument_list|)
expr_stmt|;
name|this
operator|.
name|oakPath
operator|=
name|oakPath
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|principalManager
operator|=
name|principalManager
expr_stmt|;
name|this
operator|.
name|importBehavior
operator|=
name|importBehavior
expr_stmt|;
name|this
operator|.
name|cugExclude
operator|=
name|cugExclude
expr_stmt|;
name|this
operator|.
name|principals
operator|.
name|addAll
argument_list|(
name|principals
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|principals
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addPrincipals
parameter_list|(
annotation|@
name|NotNull
name|Principal
modifier|...
name|principals
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|isValidPrincipal
argument_list|(
name|principal
argument_list|)
condition|)
block|{
name|modified
operator||=
name|this
operator|.
name|principals
operator|.
name|add
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modified
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removePrincipals
parameter_list|(
annotation|@
name|NotNull
name|Principal
modifier|...
name|principals
parameter_list|)
block|{
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
name|modified
operator||=
name|this
operator|.
name|principals
operator|.
name|remove
argument_list|(
name|principal
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modified
return|;
block|}
comment|//----------------------------------------< JackrabbitAccessControlList>---
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
name|Iterable
argument_list|<
name|String
argument_list|>
name|getPrincipalNames
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|principals
argument_list|,
name|Principal
operator|::
name|getName
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Validate the specified {@code principal} taking the configured      * {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior} into account.      *      *      * @param principal The principal to validate.      * @return if the principal is considered valid and can be added to the list.      * @throws AccessControlException If the principal has an invalid name or      * if {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior#ABORT} is      * configured and this principal is not known to the repository.      */
specifier|private
name|boolean
name|isValidPrincipal
parameter_list|(
annotation|@
name|Nullable
name|Principal
name|principal
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring null principal."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|String
name|name
init|=
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid principal "
operator|+
name|name
argument_list|)
throw|;
block|}
if|if
condition|(
name|cugExclude
operator|.
name|isExcluded
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|principal
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Attempt to add excluded principal {} to CUG."
argument_list|,
name|principal
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|boolean
name|isValid
init|=
literal|true
decl_stmt|;
switch|switch
condition|(
name|importBehavior
condition|)
block|{
case|case
name|ImportBehavior
operator|.
name|IGNORE
case|:
if|if
condition|(
operator|!
name|principalManager
operator|.
name|hasPrincipal
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring unknown principal {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|isValid
operator|=
literal|false
expr_stmt|;
block|}
break|break;
case|case
name|ImportBehavior
operator|.
name|BESTEFFORT
case|:
name|log
operator|.
name|debug
argument_list|(
literal|"Best effort: don't verify existence of principals."
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|//ImportBehavior.ABORT
if|if
condition|(
operator|!
name|principalManager
operator|.
name|hasPrincipal
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unknown principal "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
return|return
name|isValid
return|;
block|}
block|}
end_class

end_unit

