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
name|query
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
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
name|JcrConstants
operator|.
name|JCR_SYSTEM
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
name|plugins
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|namepath
operator|.
name|LocalNameMapper
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
name|namepath
operator|.
name|NamePathMapperImpl
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
name|query
operator|.
name|xpath
operator|.
name|XPathToSQL2Converter
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
name|state
operator|.
name|NodeState
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
import|import
name|org
operator|.
name|slf4j
operator|.
name|MDC
import|;
end_import

begin_comment
comment|/**  * The query engine implementation.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|QueryEngineImpl
implements|implements
name|QueryEngine
block|{
comment|/**      * Used to instruct the {@link QueryEngineImpl} on how to act with respect of the SQL2      * optimisation.      */
specifier|public
specifier|static
enum|enum
name|QuerySelectionMode
block|{
comment|/**          * Will execute the cheapest (default).          */
name|CHEAPEST
block|,
comment|/**          * Will use the original SQL2 query.          */
name|ORIGINAL
block|,
comment|/**          * Will force the computed alternate query to be executed. If available.          */
name|ALTERNATIVE
block|}
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|ID_COUNTER
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MDC_QUERY_ID
init|=
literal|"oak.query.id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OAK_QUERY_ANALYZE
init|=
literal|"oak.query.analyze"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SQL2
init|=
literal|"JCR-SQL2"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SQL
init|=
literal|"sql"
decl_stmt|;
specifier|static
specifier|final
name|String
name|XPATH
init|=
literal|"xpath"
decl_stmt|;
specifier|static
specifier|final
name|String
name|JQOM
init|=
literal|"JCR-JQOM"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NO_LITERALS
init|=
literal|"-noLiterals"
decl_stmt|;
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueryEngineImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|SUPPORTED_LANGUAGES
init|=
name|of
argument_list|(
name|SQL2
argument_list|,
name|SQL2
operator|+
name|NO_LITERALS
argument_list|,
name|SQL
argument_list|,
name|SQL
operator|+
name|NO_LITERALS
argument_list|,
name|XPATH
argument_list|,
name|XPATH
operator|+
name|NO_LITERALS
argument_list|,
name|JQOM
argument_list|)
decl_stmt|;
comment|/**      * Whether node traversal is enabled. This is enabled by default, and can be      * disabled for testing purposes.      */
specifier|private
name|boolean
name|traversalEnabled
init|=
literal|true
decl_stmt|;
comment|/**      * Which query to select in case multiple options are available. Whether the      * query engine should pick the one with the lowest expected cost (default),      * or the original, or the alternative.      */
specifier|private
name|QuerySelectionMode
name|querySelectionMode
init|=
name|QuerySelectionMode
operator|.
name|CHEAPEST
decl_stmt|;
comment|/**      * Get the execution context for a single query execution.      *       * @return the context      */
specifier|protected
specifier|abstract
name|ExecutionContext
name|getExecutionContext
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedQueryLanguages
parameter_list|()
block|{
return|return
name|SUPPORTED_LANGUAGES
return|;
block|}
comment|/**      * Parse the query (check if it's valid) and get the list of bind variable names.      *      * @param statement query statement      * @param language query language      * @param mappings namespace prefix mappings      * @return the list of bind variable names      * @throws ParseException      */
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getBindVariableNames
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|qs
init|=
name|parseQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|,
name|getExecutionContext
argument_list|()
argument_list|,
name|mappings
argument_list|)
decl_stmt|;
return|return
name|qs
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getBindVariableNames
argument_list|()
return|;
block|}
comment|/**      * Parse the query.      *       * @param statement the statement      * @param language the language      * @param context the context      * @param mappings the mappings      * @return the list of queries, where the first is the original, and all      *         others are alternatives (for example, a "union" query)      */
specifier|private
specifier|static
name|List
argument_list|<
name|Query
argument_list|>
name|parseQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|ExecutionContext
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
block|{
name|boolean
name|isInternal
init|=
name|SQL2Parser
operator|.
name|isInternal
argument_list|(
name|statement
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInternal
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Parsing {} statement: {}"
argument_list|,
name|language
argument_list|,
name|statement
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing {} statement: {}"
argument_list|,
name|language
argument_list|,
name|statement
argument_list|)
expr_stmt|;
block|}
name|NamePathMapper
name|mapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|context
operator|.
name|getRoot
argument_list|()
argument_list|,
name|mappings
argument_list|)
argument_list|)
decl_stmt|;
name|NodeState
name|types
init|=
name|context
operator|.
name|getBaseState
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|QueryEngineSettings
name|settings
init|=
name|context
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|SQL2Parser
name|parser
init|=
operator|new
name|SQL2Parser
argument_list|(
name|mapper
argument_list|,
name|types
argument_list|,
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|language
operator|.
name|endsWith
argument_list|(
name|NO_LITERALS
argument_list|)
condition|)
block|{
name|language
operator|=
name|language
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|language
operator|.
name|length
argument_list|()
operator|-
name|NO_LITERALS
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setAllowNumberLiterals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setAllowTextLiterals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|Query
name|q
decl_stmt|;
if|if
condition|(
name|SQL2
operator|.
name|equals
argument_list|(
name|language
argument_list|)
operator|||
name|JQOM
operator|.
name|equals
argument_list|(
name|language
argument_list|)
condition|)
block|{
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SQL
operator|.
name|equals
argument_list|(
name|language
argument_list|)
condition|)
block|{
name|parser
operator|.
name|setSupportSQL1
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|XPATH
operator|.
name|equals
argument_list|(
name|language
argument_list|)
condition|)
block|{
name|XPathToSQL2Converter
name|converter
init|=
operator|new
name|XPathToSQL2Converter
argument_list|()
decl_stmt|;
name|String
name|sql2
init|=
name|converter
operator|.
name|convert
argument_list|(
name|statement
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"XPath> SQL2: {}"
argument_list|,
name|sql2
argument_list|)
expr_stmt|;
try|try
block|{
comment|// OAK-874: No artificial XPath selector name in wildcards
name|parser
operator|.
name|setIncludeSelectorNameInWildcardColumns
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|q
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|sql2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|ParseException
name|e2
init|=
operator|new
name|ParseException
argument_list|(
name|statement
operator|+
literal|" converted to SQL-2 "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unsupported language: "
operator|+
name|language
argument_list|,
literal|0
argument_list|)
throw|;
block|}
name|queries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|isSql2Optimisation
argument_list|()
condition|)
block|{
if|if
condition|(
name|q
operator|.
name|isInternal
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Skipping optimisation as internal query."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Attempting optimisation"
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
name|q
operator|.
name|buildAlternativeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|q2
operator|!=
name|q
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Alternative query available: {}"
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|q2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// initialising all the queries.
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
try|try
block|{
name|query
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ParseException
name|e2
init|=
operator|new
name|ParseException
argument_list|(
name|query
operator|.
name|getStatement
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
return|return
name|queries
return|;
block|}
annotation|@
name|Override
specifier|public
name|Result
name|executeQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|bindings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
name|bindings
argument_list|,
name|mappings
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Result
name|executeQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|offset
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|bindings
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|limit
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Limit may not be negative, is: "
operator|+
name|limit
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Offset may not be negative, is: "
operator|+
name|offset
argument_list|)
throw|;
block|}
comment|// avoid having to deal with null arguments
if|if
condition|(
name|bindings
operator|==
literal|null
condition|)
block|{
name|bindings
operator|=
name|NO_BINDINGS
expr_stmt|;
block|}
if|if
condition|(
name|mappings
operator|==
literal|null
condition|)
block|{
name|mappings
operator|=
name|NO_MAPPINGS
expr_stmt|;
block|}
name|ExecutionContext
name|context
init|=
name|getExecutionContext
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
name|parseQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|,
name|context
argument_list|,
name|mappings
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|q
range|:
name|queries
control|)
block|{
name|q
operator|.
name|setExecutionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|q
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
name|q
operator|.
name|setOffset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
if|if
condition|(
name|bindings
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|PropertyValue
argument_list|>
name|e
range|:
name|bindings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|q
operator|.
name|bindValue
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|q
operator|.
name|setTraversalEnabled
argument_list|(
name|traversalEnabled
argument_list|)
expr_stmt|;
block|}
name|boolean
name|mdc
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|prepareAndSelect
argument_list|(
name|queries
argument_list|)
decl_stmt|;
name|mdc
operator|=
name|setupMDC
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|executeQuery
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|mdc
condition|)
block|{
name|clearMDC
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Prepare all the available queries and by based on the {@link QuerySelectionMode} flag return      * the appropriate.      *       * @param queries the list of queries to be executed. Cannot be null.      *      If there are multiple, the first one is the original, and the second the alternative.      * @return the query      */
annotation|@
name|Nonnull
specifier|private
name|Query
name|prepareAndSelect
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|Query
argument_list|>
name|queries
parameter_list|)
block|{
name|Query
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|checkNotNull
argument_list|(
name|queries
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// we only have the original query so we prepare and return it.
name|result
operator|=
name|queries
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"No alternatives found. Query: {}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|double
name|bestCost
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
comment|// Always prepare all of the queries and compute the cheapest as
comment|// it's the default behaviour. That way, we always log the cost and
comment|// can more easily analyze problems. The querySelectionMode flag can
comment|// be used to override the cheapest.
for|for
control|(
name|Query
name|q
range|:
name|checkNotNull
argument_list|(
name|queries
argument_list|)
control|)
block|{
name|q
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|double
name|cost
init|=
name|q
operator|.
name|getEstimatedCost
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"cost: {} for query {}"
argument_list|,
name|cost
argument_list|,
name|q
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|containsUnfilteredFullTextCondition
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"contains an unfiltered fulltext condition"
argument_list|)
expr_stmt|;
name|cost
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|cost
operator|<
name|bestCost
condition|)
block|{
name|result
operator|=
name|q
expr_stmt|;
name|bestCost
operator|=
name|cost
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|querySelectionMode
condition|)
block|{
case|case
name|ORIGINAL
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|"Forcing the original SQL2 query to be executed by flag"
argument_list|)
expr_stmt|;
name|result
operator|=
name|queries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|ALTERNATIVE
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|"Forcing the alternative SQL2 query to be executed by flag"
argument_list|)
expr_stmt|;
name|result
operator|=
name|queries
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
break|break;
comment|// CHEAPEST is the default behaviour
case|case
name|CHEAPEST
case|:
default|default:
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|void
name|setTraversalEnabled
parameter_list|(
name|boolean
name|traversalEnabled
parameter_list|)
block|{
name|this
operator|.
name|traversalEnabled
operator|=
name|traversalEnabled
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|setupMDC
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|boolean
name|mdcEnabled
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|q
operator|.
name|isMeasureOrExplainEnabled
argument_list|()
condition|)
block|{
name|MDC
operator|.
name|put
argument_list|(
name|OAK_QUERY_ANALYZE
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|mdcEnabled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|MDC
operator|.
name|put
argument_list|(
name|MDC_QUERY_ID
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ID_COUNTER
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mdcEnabled
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|mdcEnabled
return|;
block|}
specifier|private
specifier|static
name|void
name|clearMDC
parameter_list|()
block|{
name|MDC
operator|.
name|remove
argument_list|(
name|MDC_QUERY_ID
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|remove
argument_list|(
name|OAK_QUERY_ANALYZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Instruct the query engine on how to behave with regards to the SQL2 optimised query if      * available.      *       * @param querySelectionMode cannot be null      */
specifier|protected
name|void
name|setQuerySelectionMode
parameter_list|(
annotation|@
name|Nonnull
name|QuerySelectionMode
name|querySelectionMode
parameter_list|)
block|{
name|this
operator|.
name|querySelectionMode
operator|=
name|querySelectionMode
expr_stmt|;
block|}
block|}
end_class

end_unit

