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
name|query
operator|.
name|index
operator|.
name|TraversingIndex
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
name|Filter
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
name|QueryIndex
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
name|QueryIndexProvider
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
specifier|private
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
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
comment|// TODO: Turn into a standalone class
specifier|private
specifier|final
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedLanguages
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SQL2
argument_list|,
name|SQL
argument_list|,
name|XPATH
argument_list|,
name|JQOM
argument_list|,
name|SQL2
operator|+
name|NO_LITERALS
argument_list|,
name|SQL
operator|+
name|NO_LITERALS
argument_list|,
name|XPATH
operator|+
name|NO_LITERALS
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|)
throws|throws
name|ParseException
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
name|SQL2Parser
name|parser
init|=
operator|new
name|SQL2Parser
argument_list|()
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
return|return
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|)
return|;
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
return|return
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|)
return|;
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
return|return
name|parser
operator|.
name|parse
argument_list|(
name|sql2
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
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
block|}
block|}
decl_stmt|;
specifier|public
name|QueryEngineImpl
parameter_list|(
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
block|}
comment|/**      * Get the current root node state, to run the query against.      *       * @return the node state      */
specifier|protected
specifier|abstract
name|NodeState
name|getRootState
parameter_list|()
function_decl|;
comment|/**      * Get the current root tree, to run the query against.      *       * @return the node state      */
specifier|protected
specifier|abstract
name|Root
name|getRootTree
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
name|parser
operator|.
name|getSupportedLanguages
argument_list|()
return|;
block|}
comment|/**      * Parse the query (check if it's valid) and get the list of bind variable names.      *      * @param statement      * @param language      * @return the list of bind variable names      * @throws ParseException      */
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
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|parseQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|)
decl_stmt|;
return|return
name|q
operator|.
name|getBindVariableNames
argument_list|()
return|;
block|}
specifier|private
name|Query
name|parseQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|parser
operator|.
name|parse
argument_list|(
name|statement
argument_list|,
name|language
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
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|parseQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|)
decl_stmt|;
name|q
operator|.
name|setRootTree
argument_list|(
name|getRootTree
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|setRootState
argument_list|(
name|getRootState
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|.
name|setNamePathMapper
argument_list|(
name|namePathMapper
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
name|setQueryEngine
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|q
operator|.
name|prepare
argument_list|()
expr_stmt|;
return|return
name|q
operator|.
name|executeQuery
argument_list|()
return|;
block|}
specifier|public
name|QueryIndex
name|getBestIndex
parameter_list|(
name|Query
name|query
parameter_list|,
name|NodeState
name|rootState
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|QueryIndex
name|best
init|=
literal|null
decl_stmt|;
name|double
name|bestCost
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|QueryIndex
name|index
range|:
name|getIndexes
argument_list|(
name|rootState
argument_list|)
control|)
block|{
name|double
name|cost
init|=
name|index
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"cost for "
operator|+
name|index
operator|.
name|getIndexName
argument_list|()
operator|+
literal|" is "
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cost
operator|<
name|bestCost
condition|)
block|{
name|bestCost
operator|=
name|cost
expr_stmt|;
name|best
operator|=
name|index
expr_stmt|;
block|}
block|}
if|if
condition|(
name|best
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"no indexes found - using TraversingIndex; indexProvider: "
operator|+
name|indexProvider
argument_list|)
expr_stmt|;
block|}
name|best
operator|=
operator|new
name|TraversingIndex
argument_list|()
expr_stmt|;
block|}
return|return
name|best
return|;
block|}
specifier|private
name|List
argument_list|<
name|?
extends|extends
name|QueryIndex
argument_list|>
name|getIndexes
parameter_list|(
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|indexProvider
operator|.
name|getQueryIndexes
argument_list|(
name|rootState
argument_list|)
return|;
block|}
block|}
end_class

end_unit

