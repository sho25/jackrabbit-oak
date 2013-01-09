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
name|Iterator
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
name|Text
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
comment|/**  * XPATH based evaluation of {@link org.apache.jackrabbit.api.security.user.Query}s.  */
end_comment

begin_class
specifier|public
class|class
name|XPathQueryEvaluator
implements|implements
name|ConditionVisitor
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|XPathQueryEvaluator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|XPathQueryBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|UserManager
name|userManager
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
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|statement
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|public
name|XPathQueryEvaluator
parameter_list|(
name|XPathQueryBuilder
name|builder
parameter_list|,
name|UserManager
name|userManager
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|userManager
operator|=
name|userManager
expr_stmt|;
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
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|eval
parameter_list|()
throws|throws
name|RepositoryException
block|{
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
name|Iterators
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
name|Value
name|bound
init|=
name|builder
operator|.
name|getBound
argument_list|()
decl_stmt|;
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
name|String
name|searchRoot
init|=
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
decl_stmt|;
name|String
name|ntName
init|=
name|QueryUtil
operator|.
name|getNodeTypeName
argument_list|(
name|builder
operator|.
name|getSelectorType
argument_list|()
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
name|this
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
operator|.
name|append
argument_list|(
name|ignoreCase
condition|?
literal|""
else|:
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
name|ignoreCase
condition|?
literal|" "
else|:
literal|") "
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
try|try
block|{
if|if
condition|(
name|builder
operator|.
name|getGroupName
argument_list|()
operator|==
literal|null
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
if|if
condition|(
name|bound
operator|!=
literal|null
operator|&&
name|offset
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Found bound {} and offset {} in limit. Discarding offset."
argument_list|,
name|bound
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|findAuthorizables
argument_list|(
name|builder
operator|.
name|getMaxCount
argument_list|()
argument_list|,
name|offset
argument_list|)
return|;
block|}
else|else
block|{
comment|// filtering by group name included in query -> enforce offset
comment|// and limit on the result set.
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|result
init|=
name|findAuthorizables
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|filtered
init|=
name|filter
argument_list|(
name|result
argument_list|,
name|builder
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|builder
operator|.
name|isDeclaredMembersOnly
argument_list|()
argument_list|)
decl_stmt|;
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
name|filtered
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|//---------------------------------------------------< ConditionVisitor>---
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Node
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|statement
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
literal|"jcr:like(@"
argument_list|)
operator|.
name|append
argument_list|(
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
operator|.
name|append
argument_list|(
literal|"jcr:like(fn:name(),'"
argument_list|)
operator|.
name|append
argument_list|(
name|escape
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Property
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|RelationOp
name|relOp
init|=
name|condition
operator|.
name|getOp
argument_list|()
decl_stmt|;
if|if
condition|(
name|relOp
operator|==
name|RelationOp
operator|.
name|EX
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|relOp
operator|==
name|RelationOp
operator|.
name|LIKE
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"jcr:like("
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getPattern
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statement
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getOp
argument_list|()
operator|.
name|getOp
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|format
argument_list|(
name|condition
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Contains
name|condition
parameter_list|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"jcr:contains("
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getRelPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|",'"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getSearchExpr
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Impersonation
name|condition
parameter_list|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"@rep:impersonators='"
argument_list|)
operator|.
name|append
argument_list|(
name|condition
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Not
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"not("
argument_list|)
expr_stmt|;
name|condition
operator|.
name|getCondition
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|And
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Condition
name|c
range|:
name|condition
control|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|count
operator|++
operator|>
literal|0
condition|?
literal|" and "
else|:
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Condition
operator|.
name|Or
name|condition
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|int
name|pos
init|=
name|statement
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Condition
name|c
range|:
name|condition
control|)
block|{
name|statement
operator|.
name|append
argument_list|(
name|count
operator|++
operator|>
literal|0
condition|?
literal|" or "
else|:
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Surround or clause with parentheses if it contains more than one term
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
name|statement
operator|.
name|insert
argument_list|(
name|pos
argument_list|,
literal|'('
argument_list|)
expr_stmt|;
name|statement
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Escape {@code string} for matching in jcr escaped node names      *      * @param string string to escape      * @return escaped string      */
annotation|@
name|Nonnull
specifier|public
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
name|int
name|j
decl_stmt|;
do|do
block|{
name|j
operator|=
name|string
operator|.
name|indexOf
argument_list|(
literal|'%'
argument_list|,
name|k
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|<
literal|0
condition|)
block|{
comment|// jcr escape trail
name|result
operator|.
name|append
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|string
operator|.
name|substring
argument_list|(
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|j
operator|>
literal|0
operator|&&
name|string
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
operator|==
literal|'\\'
condition|)
block|{
comment|// literal occurrence of % -> jcr escape
name|result
operator|.
name|append
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|string
operator|.
name|substring
argument_list|(
name|k
argument_list|,
name|j
argument_list|)
operator|+
literal|'%'
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// wildcard occurrence of % -> jcr escape all but %
name|result
operator|.
name|append
argument_list|(
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|string
operator|.
name|substring
argument_list|(
name|k
argument_list|,
name|j
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|j
operator|+
literal|1
expr_stmt|;
block|}
do|while
condition|(
name|j
operator|>=
literal|0
condition|)
do|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
literal|'\''
operator|+
name|value
operator|.
name|getString
argument_list|()
operator|+
literal|'\''
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|value
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
literal|"xs:dateTime('"
operator|+
name|value
operator|.
name|getString
argument_list|()
operator|+
literal|"')"
return|;
default|default:
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Property of type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" not supported"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
specifier|static
name|RelationOp
name|getCollation
parameter_list|(
name|QueryBuilder
operator|.
name|Direction
name|direction
parameter_list|)
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|direction
condition|)
block|{
case|case
name|ASCENDING
case|:
return|return
name|RelationOp
operator|.
name|GT
return|;
case|case
name|DESCENDING
case|:
return|return
name|RelationOp
operator|.
name|LT
return|;
default|default:
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Unknown sort order "
operator|+
name|direction
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
name|long
name|limit
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|ParseException
block|{
name|Iterable
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|resultRows
init|=
name|root
operator|.
name|getQueryEngine
argument_list|()
operator|.
name|executeQuery
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
literal|null
argument_list|,
name|namePathMapper
argument_list|)
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
name|Predicates
operator|.
expr|<
name|Object
operator|>
name|notNull
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|filter
parameter_list|(
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
parameter_list|,
name|String
name|groupName
parameter_list|,
specifier|final
name|boolean
name|declaredMembersOnly
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
name|predicate
decl_stmt|;
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|groupName
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|==
literal|null
operator|||
operator|!
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|predicate
operator|=
name|Predicates
operator|.
name|alwaysFalse
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Group
name|group
init|=
operator|(
name|Group
operator|)
name|authorizable
decl_stmt|;
name|predicate
operator|=
operator|new
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|declaredMembersOnly
operator|)
condition|?
name|group
operator|.
name|isDeclaredMember
argument_list|(
name|authorizable
argument_list|)
else|:
name|group
operator|.
name|isMember
argument_list|(
name|authorizable
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
literal|"Cannot determine group membership for {}"
argument_list|,
name|authorizable
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
expr_stmt|;
block|}
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|authorizables
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
end_class

end_unit

