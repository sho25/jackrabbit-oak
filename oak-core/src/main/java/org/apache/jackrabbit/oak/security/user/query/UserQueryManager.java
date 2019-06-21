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
name|user
operator|.
name|query
package|;
end_package

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
name|Set
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
name|Iterators
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
name|Group
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
name|Query
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
name|QueryBuilder
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
name|commons
operator|.
name|PathUtils
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
name|DeclaredMembershipPredicate
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
name|UserManagerImpl
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
name|principal
operator|.
name|EveryonePrincipal
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
name|UserConstants
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
name|util
operator|.
name|ISO9075
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
name|util
operator|.
name|Text
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

begin_import
import|import static
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
operator|.
name|NO_BINDINGS
import|;
end_import

begin_import
import|import static
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
name|query
operator|.
name|QueryUtil
operator|.
name|getID
import|;
end_import

begin_comment
comment|/**  * Query manager for user specific searches.  */
end_comment

begin_class
specifier|public
class|class
name|UserQueryManager
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
name|UserQueryManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserManagerImpl
name|userManager
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
specifier|public
name|UserQueryManager
parameter_list|(
annotation|@
name|NotNull
name|UserManagerImpl
name|userManager
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|NotNull
name|ConfigurationParameters
name|config
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|userManager
operator|=
name|userManager
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
comment|/**      * Find the authorizables matching the specified user {@code Query}.      *      * @param query A query object.      * @return An iterator of authorizables that match the specified query.      * @throws RepositoryException If an error occurs.      */
annotation|@
name|NotNull
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
annotation|@
name|NotNull
name|Query
name|query
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|XPathQueryBuilder
name|builder
init|=
operator|new
name|XPathQueryBuilder
argument_list|()
decl_stmt|;
name|query
operator|.
name|build
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|getMaxCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
name|String
name|statement
init|=
name|buildXPathStatement
argument_list|(
name|builder
argument_list|)
decl_stmt|;
specifier|final
name|String
name|groupId
init|=
name|builder
operator|.
name|getGroupID
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupId
operator|==
literal|null
operator|||
name|isEveryone
argument_list|(
name|groupId
argument_list|)
condition|)
block|{
name|long
name|offset
init|=
name|builder
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|findAuthorizables
argument_list|(
name|statement
argument_list|,
name|builder
operator|.
name|getMaxCount
argument_list|()
argument_list|,
name|offset
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupId
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
else|else
block|{
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|result
argument_list|,
name|authorizable
lambda|->
operator|!
name|groupId
operator|.
name|equals
argument_list|(
name|getID
argument_list|(
name|authorizable
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// filtering by group name included in query -> enforce offset and limit on the result set.
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|findAuthorizables
argument_list|(
name|statement
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
name|filter
decl_stmt|;
if|if
condition|(
name|builder
operator|.
name|isDeclaredMembersOnly
argument_list|()
condition|)
block|{
name|filter
operator|=
operator|new
name|DeclaredMembershipPredicate
argument_list|(
name|userManager
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|=
operator|new
name|GroupPredicate
argument_list|(
name|userManager
argument_list|,
name|groupId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|ResultIterator
operator|.
name|create
argument_list|(
name|builder
operator|.
name|getOffset
argument_list|()
argument_list|,
name|builder
operator|.
name|getMaxCount
argument_list|()
argument_list|,
name|Iterators
operator|.
name|filter
argument_list|(
name|result
argument_list|,
name|filter
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**      * Find the authorizables matching the following search parameters within      * the sub-tree defined by an authorizable tree:      *      * @param relPath          A relative path (or a name) pointing to properties within      *                         the tree defined by a given authorizable node.      * @param value            The property value to look for.      * @param authorizableType Filter the search results to only return authorizable      *                         trees of a given type. Passing {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableType#AUTHORIZABLE} indicates that      *                         no filtering for a specific authorizable type is desired. However, properties      *                         might still be search in the complete sub-tree of authorizables depending      *                         on the other query parameters.      * @return An iterator of authorizable trees that match the specified      *         search parameters and filters or an empty iterator if no result can be      *         found.      * @throws javax.jcr.RepositoryException If an error occurs.      */
annotation|@
name|NotNull
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|Nullable
name|String
name|value
parameter_list|,
annotation|@
name|NotNull
name|AuthorizableType
name|authorizableType
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|findAuthorizables
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|,
name|authorizableType
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Find the authorizables matching the following search parameters within      * the sub-tree defined by an authorizable tree:      *      * @param relPath          A relative path (or a name) pointing to properties within      *                         the tree defined by a given authorizable node.      * @param value            The property value to look for.      * @param authorizableType Filter the search results to only return authorizable      *                         trees of a given type. Passing {@link org.apache.jackrabbit.oak.spi.security.user.AuthorizableType#AUTHORIZABLE} indicates that      *                         no filtering for a specific authorizable type is desired. However, properties      *                         might still be search in the complete sub-tree of authorizables depending      *                         on the other query parameters.      * @param exact            A boolean flag indicating if the value must match exactly or not.s      * @return An iterator of authorizable trees that match the specified      *         search parameters and filters or an empty iterator if no result can be      *         found.      * @throws javax.jcr.RepositoryException If an error occurs.      */
annotation|@
name|NotNull
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|Nullable
name|String
name|value
parameter_list|,
annotation|@
name|NotNull
name|AuthorizableType
name|authorizableType
parameter_list|,
name|boolean
name|exact
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|statement
init|=
name|buildXPathStatement
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|,
name|authorizableType
argument_list|,
name|exact
argument_list|)
decl_stmt|;
return|return
name|findAuthorizables
argument_list|(
name|statement
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|authorizableType
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
annotation|@
name|NotNull
specifier|private
name|String
name|buildXPathStatement
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|,
annotation|@
name|Nullable
name|String
name|value
parameter_list|,
annotation|@
name|NotNull
name|AuthorizableType
name|type
parameter_list|,
name|boolean
name|exact
parameter_list|)
block|{
name|String
name|searchRoot
init|=
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|QueryUtil
operator|.
name|getSearchRoot
argument_list|(
name|type
argument_list|,
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|stmt
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|searchRoot
argument_list|)
decl_stmt|;
name|String
name|propName
init|=
name|Text
operator|.
name|getName
argument_list|(
name|relPath
argument_list|)
decl_stmt|;
name|String
name|path
decl_stmt|;
name|String
name|ntName
decl_stmt|;
if|if
condition|(
name|relPath
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
operator|&&
operator|!
name|isReserved
argument_list|(
name|propName
argument_list|)
condition|)
block|{
comment|// arbitrary property specified in query and no explicit relative path specified
comment|// -> need to search within the whole in the authorizable tree
name|path
operator|=
literal|null
expr_stmt|;
name|ntName
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|getQueryPath
argument_list|(
name|relPath
argument_list|)
expr_stmt|;
name|ntName
operator|=
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|QueryUtil
operator|.
name|getNodeTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
name|stmt
operator|.
name|append
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|stmt
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ntName
operator|!=
literal|null
condition|)
block|{
name|stmt
operator|.
name|append
argument_list|(
literal|"element(*,"
argument_list|)
operator|.
name|append
argument_list|(
name|ntName
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stmt
operator|.
name|append
argument_list|(
literal|"element(*)"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// property must exist
name|stmt
operator|.
name|append
argument_list|(
literal|"[@"
argument_list|)
operator|.
name|append
argument_list|(
name|propName
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stmt
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
operator|(
name|exact
operator|)
condition|?
literal|"@"
else|:
literal|"jcr:like(@"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
name|ISO9075
operator|.
name|encode
argument_list|(
name|propName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|exact
condition|)
block|{
name|stmt
operator|.
name|append
argument_list|(
literal|"='"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
name|value
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"''"
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stmt
operator|.
name|append
argument_list|(
literal|",'%"
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
name|QueryUtil
operator|.
name|escapeForQuery
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|append
argument_list|(
literal|"%')"
argument_list|)
expr_stmt|;
block|}
name|stmt
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
return|return
name|stmt
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|String
name|buildXPathStatement
parameter_list|(
annotation|@
name|NotNull
name|XPathQueryBuilder
name|builder
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Condition
name|condition
init|=
name|builder
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|String
name|sortCol
init|=
name|builder
operator|.
name|getSortProperty
argument_list|()
decl_stmt|;
name|QueryBuilder
operator|.
name|Direction
name|sortDir
init|=
name|builder
operator|.
name|getSortDirection
argument_list|()
decl_stmt|;
name|Value
name|bound
init|=
name|builder
operator|.
name|getBound
argument_list|()
decl_stmt|;
if|if
condition|(
name|bound
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sortCol
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring bound {} since no sort order is specified"
argument_list|,
name|bound
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Condition
name|boundCondition
init|=
name|builder
operator|.
name|property
argument_list|(
name|sortCol
argument_list|,
name|QueryUtil
operator|.
name|getCollation
argument_list|(
name|sortDir
argument_list|)
argument_list|,
name|bound
argument_list|)
decl_stmt|;
if|if
condition|(
name|condition
operator|==
literal|null
condition|)
block|{
name|condition
operator|=
name|boundCondition
expr_stmt|;
block|}
else|else
block|{
name|condition
operator|=
name|builder
operator|.
name|and
argument_list|(
name|condition
argument_list|,
name|boundCondition
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|StringBuilder
name|statement
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|searchRoot
init|=
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|QueryUtil
operator|.
name|getSearchRoot
argument_list|(
name|builder
operator|.
name|getSelectorType
argument_list|()
argument_list|,
name|config
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ntName
init|=
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|QueryUtil
operator|.
name|getNodeTypeName
argument_list|(
name|builder
operator|.
name|getSelectorType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|statement
operator|.
name|append
argument_list|(
name|searchRoot
argument_list|)
operator|.
name|append
argument_list|(
literal|"//element(*,"
argument_list|)
operator|.
name|append
argument_list|(
name|ntName
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
name|ConditionVisitor
name|visitor
init|=
operator|new
name|XPathConditionVisitor
argument_list|(
name|statement
argument_list|,
name|namePathMapper
argument_list|,
name|userManager
argument_list|)
decl_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|condition
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortCol
operator|!=
literal|null
condition|)
block|{
name|boolean
name|ignoreCase
init|=
name|builder
operator|.
name|getSortIgnoreCase
argument_list|()
decl_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
expr_stmt|;
if|if
condition|(
name|ignoreCase
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"fn:lower-case("
argument_list|)
operator|.
name|append
argument_list|(
name|sortCol
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statement
operator|.
name|append
argument_list|(
name|sortCol
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|sortDir
operator|.
name|getDirection
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|statement
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
annotation|@
name|NotNull
name|String
name|statement
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|offset
parameter_list|,
annotation|@
name|Nullable
name|AuthorizableType
name|type
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Result
name|query
init|=
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
operator|.
name|XPATH
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
name|NO_BINDINGS
argument_list|,
name|namePathMapper
operator|.
name|getSessionLocalMappings
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|resultRows
init|=
name|query
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|resultRows
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|ResultRowToAuthorizable
argument_list|(
name|userManager
argument_list|,
name|root
argument_list|,
name|type
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|authorizables
argument_list|,
operator|new
name|UniqueResultPredicate
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
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid user query: "
operator|+
name|statement
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nullable
specifier|private
specifier|static
name|String
name|getQueryPath
parameter_list|(
annotation|@
name|NotNull
name|String
name|relPath
parameter_list|)
block|{
if|if
condition|(
name|relPath
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
comment|// just a single segment -> don't include the path in the query
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// compute the relative path excluding the trailing property name
name|String
index|[]
name|segments
init|=
name|Text
operator|.
name|explode
argument_list|(
name|relPath
argument_list|,
literal|'/'
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segments
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesCurrent
argument_list|(
name|segments
index|[
name|i
index|]
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|segments
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isReserved
parameter_list|(
annotation|@
name|NotNull
name|String
name|propName
parameter_list|)
block|{
return|return
name|UserConstants
operator|.
name|GROUP_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
operator|||
name|UserConstants
operator|.
name|USER_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isEveryone
parameter_list|(
annotation|@
name|NotNull
name|String
name|groupId
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Group
name|gr
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|groupId
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|gr
operator|==
literal|null
condition|)
block|{
comment|// compatibility with original code that didn't check for existence of the group
return|return
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|groupId
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|gr
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Predicate asserting that a given user/group is only included once in the      * result set.      */
specifier|private
specifier|static
specifier|final
class|class
name|UniqueResultPredicate
implements|implements
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|authorizableIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Authorizable
name|input
parameter_list|)
block|{
name|String
name|id
init|=
name|getID
argument_list|(
name|input
argument_list|)
decl_stmt|;
return|return
name|id
operator|!=
literal|null
operator|&&
name|authorizableIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

