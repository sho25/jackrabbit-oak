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
name|base
operator|.
name|Predicate
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
name|ImmutableSet
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
name|Iterators
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
name|Maps
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

begin_class
specifier|public
specifier|final
class|class
name|TestPrincipalProvider
implements|implements
name|PrincipalProvider
block|{
specifier|public
specifier|static
specifier|final
name|Principal
name|UNKNOWN
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"unknown"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|exposesEveryone
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|public
name|TestPrincipalProvider
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TestPrincipalProvider
parameter_list|(
name|boolean
name|exposesEveryone
parameter_list|)
block|{
name|this
operator|.
name|exposesEveryone
operator|=
name|exposesEveryone
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|TestPrincipals
operator|.
name|asMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TestPrincipalProvider
parameter_list|(
name|String
modifier|...
name|principalNames
parameter_list|)
block|{
name|this
operator|.
name|exposesEveryone
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|Maps
operator|.
name|toMap
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|principalNames
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
operator|new
name|PrincipalImpl
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterable
argument_list|<
name|Principal
argument_list|>
name|getTestPrincipals
parameter_list|()
block|{
return|return
name|principals
operator|.
name|values
argument_list|()
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|Principal
argument_list|>
name|all
parameter_list|()
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|all
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|principals
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|all
operator|.
name|add
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|all
return|;
block|}
specifier|public
specifier|static
name|String
name|getIDFromPrincipal
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|)
block|{
return|return
name|principal
operator|.
name|getName
argument_list|()
operator|+
literal|"_id"
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
specifier|static
name|String
name|getPrincipalNameFromID
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|endsWith
argument_list|(
literal|"_id"
argument_list|)
condition|)
block|{
return|return
name|id
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|id
operator|.
name|lastIndexOf
argument_list|(
literal|"_id"
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
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
if|if
condition|(
name|exposesEveryone
operator|&&
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
return|return
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|principals
operator|.
name|get
argument_list|(
name|principalName
argument_list|)
return|;
block|}
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
if|if
condition|(
name|principals
operator|.
name|equals
argument_list|(
name|TestPrincipals
operator|.
name|asMap
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|TestPrincipals
operator|.
name|membership
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|principals
operator|.
name|values
argument_list|()
operator|.
name|contains
argument_list|(
name|principal
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
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
name|String
name|pName
init|=
name|getPrincipalNameFromID
argument_list|(
name|userID
argument_list|)
decl_stmt|;
if|if
condition|(
name|pName
operator|!=
literal|null
condition|)
block|{
name|Principal
name|p
init|=
name|principals
operator|.
name|get
argument_list|(
name|pName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Principal
argument_list|>
name|s
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|s
operator|.
name|addAll
argument_list|(
name|getMembershipPrincipals
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
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
name|Iterables
operator|.
name|filter
argument_list|(
name|all
argument_list|()
argument_list|,
operator|new
name|SearchTypePredicate
argument_list|(
name|nameHint
argument_list|,
name|searchType
argument_list|)
argument_list|)
operator|.
name|iterator
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
name|findPrincipals
argument_list|(
literal|null
argument_list|,
name|searchType
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|SearchTypePredicate
implements|implements
name|Predicate
argument_list|<
name|Principal
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|searchType
decl_stmt|;
specifier|private
specifier|final
name|String
name|nameHint
decl_stmt|;
specifier|private
name|SearchTypePredicate
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
name|this
operator|.
name|searchType
operator|=
name|searchType
expr_stmt|;
name|this
operator|.
name|nameHint
operator|=
name|nameHint
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
if|if
condition|(
name|nameHint
operator|!=
literal|null
operator|&&
name|principal
operator|!=
literal|null
operator|&&
operator|!
name|principal
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|nameHint
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|searchType
condition|)
block|{
case|case
name|PrincipalManager
operator|.
name|SEARCH_TYPE_ALL
case|:
return|return
literal|true
return|;
case|case
name|PrincipalManager
operator|.
name|SEARCH_TYPE_GROUP
case|:
return|return
name|principal
operator|instanceof
name|GroupPrincipal
return|;
case|case
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
case|:
return|return
operator|!
operator|(
name|principal
operator|instanceof
name|GroupPrincipal
operator|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestGroup
extends|extends
name|PrincipalImpl
implements|implements
name|GroupPrincipal
block|{
specifier|private
specifier|final
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
decl_stmt|;
specifier|public
name|TestGroup
parameter_list|(
name|String
name|name
parameter_list|,
name|Principal
modifier|...
name|members
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|mset
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|members
argument_list|)
decl_stmt|;
name|this
operator|.
name|members
operator|=
name|Iterators
operator|.
name|asEnumeration
argument_list|(
name|mset
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
name|Principal
name|member
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
return|return
name|members
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestPrincipals
block|{
specifier|private
specifier|static
specifier|final
name|Principal
name|a
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Principal
name|ac
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"ac"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|GroupPrincipal
name|gr1
init|=
operator|new
name|TestGroup
argument_list|(
literal|"tGr1"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|GroupPrincipal
name|gr2
init|=
operator|new
name|TestGroup
argument_list|(
literal|"tGr2"
argument_list|,
name|a
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|GroupPrincipal
name|gr3
init|=
operator|new
name|TestGroup
argument_list|(
literal|"gr2"
argument_list|,
name|gr2
argument_list|,
name|ac
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Principal
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|a
operator|.
name|getName
argument_list|()
argument_list|,
name|a
argument_list|)
decl|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|new
name|PrincipalImpl
argument_list|(
literal|"b"
argument_list|)
argument_list|)
decl|.
name|put
argument_list|(
name|ac
operator|.
name|getName
argument_list|()
argument_list|,
name|ac
argument_list|)
decl|.
name|put
argument_list|(
name|gr1
operator|.
name|getName
argument_list|()
argument_list|,
name|gr1
argument_list|)
decl|.
name|put
argument_list|(
name|gr2
operator|.
name|getName
argument_list|()
argument_list|,
name|gr2
argument_list|)
decl|.
name|put
argument_list|(
name|gr3
operator|.
name|getName
argument_list|()
argument_list|,
name|gr3
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Principal
argument_list|>
name|asMap
parameter_list|()
block|{
return|return
name|principals
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|Principal
argument_list|>
name|membership
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"a"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|gr2
argument_list|,
name|gr3
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"ac"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|gr3
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|gr2
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|gr3
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|principals
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

