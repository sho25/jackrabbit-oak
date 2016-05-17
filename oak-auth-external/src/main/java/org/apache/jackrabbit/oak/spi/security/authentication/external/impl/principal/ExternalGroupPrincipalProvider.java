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
name|authentication
operator|.
name|external
operator|.
name|impl
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
name|security
operator|.
name|acl
operator|.
name|Group
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|Enumeration
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
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
name|Predicates
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
name|ItemBasedPrincipal
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
name|Authorizable
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
name|commons
operator|.
name|iterator
operator|.
name|AbstractLazyIterator
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
name|PropertyValue
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
name|QueryEngine
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
name|Result
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
name|ResultRow
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
name|api
operator|.
name|Type
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
name|query
operator|.
name|PropertyValues
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
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncConfig
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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
name|PrincipalImpl
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
name|AuthorizableType
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
name|util
operator|.
name|UserUtil
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
comment|/**  * Implementation of the {@code PrincipalProvider} interface that exposes  * 'external' principals of type {@link java.security.acl.Group}. 'External'  * refers to the fact that these principals are defined and managed by an  * {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider}.  *  * For performance reasons this implementation doesn't lookup principals on the IDP  * but relies on a persisted cache inside the repository where the names of these  * external principals are synchronized to based on a configurable expiration time.  *  * Currently, the implementation respects the {@code rep:externalPrincipalNames}  * properties, where group membership of external users gets synchronized if  * {@link DefaultSyncConfig.User#getDynamicMembership() dynamic membership} has  * been enabled.  *  * Please note that in contrast to the default principal provider implementation  * shipped with Oak the group principals known and exposed by this provider are  * not backed by an authorizable group and thus cannot be retrieved using  * Jackrabbit user management API.  *  * @since Oak 1.5.3  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.impl.DynamicSyncContext  */
end_comment

begin_class
class|class
name|ExternalGroupPrincipalProvider
implements|implements
name|PrincipalProvider
implements|,
name|ExternalIdentityConstants
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
name|ExternalGroupPrincipalProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BINDING_PRINCIPAL_NAMES
init|=
literal|"principalNames"
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|UserManager
name|userManager
decl_stmt|;
name|ExternalGroupPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|UserConfiguration
name|uc
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|userManager
operator|=
name|uc
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------< PrincipalProvider>---
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
name|Result
name|result
init|=
name|findPrincipals
argument_list|(
name|principalName
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|new
name|ExternalGroupPrincipal
argument_list|(
name|principalName
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
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupMembership
parameter_list|(
annotation|@
name|Nonnull
name|Principal
name|principal
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|principal
operator|instanceof
name|Group
operator|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|principal
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
operator|(
operator|(
name|ItemBasedPrincipal
operator|)
name|principal
operator|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getGroupPrincipals
argument_list|(
name|t
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getGroupPrincipals
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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
try|try
block|{
return|return
name|getGroupPrincipals
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|userID
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|PrincipalManager
operator|.
name|SEARCH_TYPE_NOT_GROUP
operator|!=
name|searchType
condition|)
block|{
name|Result
name|result
init|=
name|findPrincipals
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|nameHint
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|Iterators
operator|.
name|filter
argument_list|(
operator|new
name|GroupPrincipalIterator
argument_list|(
name|nameHint
argument_list|,
name|result
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|Iterators
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
name|findPrincipals
argument_list|(
literal|null
argument_list|,
name|searchType
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupPrincipals
parameter_list|(
annotation|@
name|CheckForNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|authorizable
operator|!=
literal|null
operator|&&
operator|!
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|authorizable
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getGroupPrincipals
argument_list|(
name|userTree
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
specifier|private
name|Set
argument_list|<
name|Group
argument_list|>
name|getGroupPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|userTree
parameter_list|)
block|{
if|if
condition|(
name|userTree
operator|.
name|exists
argument_list|()
operator|&&
name|UserUtil
operator|.
name|isType
argument_list|(
name|userTree
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
operator|&&
name|userTree
operator|.
name|hasProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
condition|)
block|{
name|PropertyState
name|ps
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Group
argument_list|>
name|groupPrincipals
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|principalName
range|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|groupPrincipals
operator|.
name|add
argument_list|(
operator|new
name|ExternalGroupPrincipal
argument_list|(
name|principalName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|groupPrincipals
return|;
block|}
block|}
comment|// group principals cannot be retrieved
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
comment|/**      * Runs an Oak {@link org.apache.jackrabbit.oak.query.Query} searching for      * {@link #REP_EXTERNAL_PRINCIPAL_NAMES} properties that match the given      * name or name hint.      *      * @param nameHint The principal name or name hint to be searched for.      * @param exactMatch boolean flag indicating if the query should search for      *                   exact matching.      * @return The query result.      */
annotation|@
name|CheckForNull
specifier|private
name|Result
name|findPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|nameHint
parameter_list|,
name|boolean
name|exactMatch
parameter_list|)
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|bindings
init|=
name|buildBinding
argument_list|(
name|nameHint
argument_list|,
name|exactMatch
argument_list|)
decl_stmt|;
name|String
name|op
init|=
operator|(
name|exactMatch
operator|)
condition|?
literal|" = "
else|:
literal|" LIKE "
decl_stmt|;
name|String
name|statement
init|=
literal|"SELECT '"
operator|+
name|REP_EXTERNAL_PRINCIPAL_NAMES
operator|+
literal|"' FROM [rep:User] WHERE PROPERTY(["
operator|+
name|REP_EXTERNAL_PRINCIPAL_NAMES
operator|+
literal|"], '"
operator|+
name|PropertyType
operator|.
name|TYPENAME_STRING
operator|+
literal|"')"
operator|+
name|op
operator|+
literal|"$"
operator|+
name|BINDING_PRINCIPAL_NAMES
operator|+
name|QueryEngine
operator|.
name|INTERNAL_SQL2_QUERY
decl_stmt|;
return|return
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
name|bindings
argument_list|,
name|namePathMapper
operator|.
name|getSessionLocalMappings
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Build the map used for the query bindings.      *      * @param nameHint The name hint      * @param exactMatch boolean flag indicating if the query should search for exact matching.      * @return the bindings      */
annotation|@
name|Nonnull
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|buildBinding
parameter_list|(
annotation|@
name|Nonnull
name|String
name|nameHint
parameter_list|,
name|boolean
name|exactMatch
parameter_list|)
block|{
name|String
name|val
init|=
name|nameHint
decl_stmt|;
if|if
condition|(
operator|!
name|exactMatch
condition|)
block|{
comment|// not-exact query matching required => add leading and trailing %
if|if
condition|(
name|nameHint
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|val
operator|=
literal|"%"
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|nameHint
operator|.
name|replace
argument_list|(
literal|"%"
argument_list|,
literal|"\\%"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"_"
argument_list|,
literal|"\\_"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
name|val
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
name|BINDING_PRINCIPAL_NAMES
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|//------------------------------------------------------< inner classes>---
comment|/**      * Implementation of the {@link Group} interface representing external group      * identities that are<strong>not</strong> represented as authorizable group      * in the repository's user management.      */
specifier|private
specifier|final
class|class
name|ExternalGroupPrincipal
extends|extends
name|PrincipalImpl
implements|implements
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
block|{
specifier|public
name|ExternalGroupPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|)
block|{
name|super
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addMember
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
if|if
condition|(
name|isMember
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Adding members to external group principals is not supported."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isMember
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Removing members from external group principals is not supported."
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|member
operator|instanceof
name|Group
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|String
name|name
init|=
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|member
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
operator|(
operator|(
name|ItemBasedPrincipal
operator|)
name|member
operator|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserUtil
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
condition|)
block|{
name|PropertyState
name|ps
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
return|return
operator|(
name|ps
operator|!=
literal|null
operator|&&
name|Iterables
operator|.
name|contains
argument_list|(
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
name|name
argument_list|)
operator|)
return|;
block|}
block|}
else|else
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|member
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|Value
index|[]
name|vs
init|=
name|a
operator|.
name|getProperty
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
decl_stmt|;
if|if
condition|(
name|vs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Value
name|v
range|:
name|vs
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Result
name|result
init|=
name|findPrincipals
argument_list|(
name|getName
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
operator|new
name|MemberIterator
argument_list|(
name|result
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
name|Iterators
operator|.
expr|<
name|Principal
operator|>
name|emptyIterator
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * {@link Group} principal iterator converting the query results of      * {@link #findPrincipals(String, int)} and {@link #findPrincipals(int)}.      * Since each result row provides the values of the {@code PropertyState},      * which matched the query, this iterator needs to filter the individual      * property values.      *      * Additional the iterator keeps track of principal names that have already      * been served and will not return duplicates.      *      * @see #findPrincipals(String, int)      * @see #findPrincipals(int)      */
specifier|private
specifier|final
class|class
name|GroupPrincipalIterator
extends|extends
name|AbstractLazyIterator
argument_list|<
name|Principal
argument_list|>
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|processed
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|queryString
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|rows
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|propValues
init|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
decl_stmt|;
specifier|private
name|GroupPrincipalIterator
parameter_list|(
annotation|@
name|Nullable
name|String
name|queryString
parameter_list|,
annotation|@
name|Nonnull
name|Result
name|queryResult
parameter_list|)
block|{
name|this
operator|.
name|queryString
operator|=
name|queryString
expr_stmt|;
name|rows
operator|=
name|queryResult
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Principal
name|getNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|propValues
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|rows
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|propValues
operator|=
name|rows
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|(
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|propValues
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
block|}
block|}
while|while
condition|(
name|propValues
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|principalName
init|=
name|propValues
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|principalName
operator|!=
literal|null
operator|&&
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|principalName
argument_list|)
operator|&&
name|matchesQuery
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|processed
operator|.
name|add
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
return|return
operator|new
name|ExternalGroupPrincipal
argument_list|(
name|principalName
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|boolean
name|matchesQuery
parameter_list|(
annotation|@
name|Nonnull
name|String
name|principalName
parameter_list|)
block|{
if|if
condition|(
name|queryString
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|principalName
operator|.
name|contains
argument_list|(
name|queryString
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * {@code Principal} iterator representing the members of a given      * {@link ExternalGroupPrincipal}. The members are collected through an      * Oak {@link org.apache.jackrabbit.oak.query.Query Query}.      *      * Note that the query result is subject to permission evaluation for      * the editing {@link Root} based on the accessibility of the individual      * {@link #REP_EXTERNAL_PRINCIPAL_NAMES} properties that contain the      * exact name of the external group principal.      *      * @see ExternalGroupPrincipal#members()      */
specifier|private
specifier|final
class|class
name|MemberIterator
extends|extends
name|AbstractLazyIterator
argument_list|<
name|Principal
argument_list|>
block|{
comment|/**          * The query results containing the path of the user accounts          * (i.e. members) that contain the target group principal in the          * {@link #REP_EXTERNAL_PRINCIPAL_NAMES} property values.          */
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|rows
decl_stmt|;
specifier|private
name|MemberIterator
parameter_list|(
annotation|@
name|Nonnull
name|Result
name|queryResult
parameter_list|)
block|{
name|rows
operator|=
name|queryResult
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Principal
name|getNext
parameter_list|()
block|{
while|while
condition|(
name|rows
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|userPath
init|=
name|rows
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizableByPath
argument_list|(
name|userPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
return|return
name|authorizable
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

