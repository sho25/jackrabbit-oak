begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|jcr
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|nodetype
operator|.
name|NodeType
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
name|InvalidQueryException
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
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
name|QueryResult
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
name|qom
operator|.
name|QueryObjectModelFactory
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
name|SessionQueryEngine
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
name|jcr
operator|.
name|SessionDelegate
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
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|QueryObjectModelFactoryImpl
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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

begin_comment
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|QueryManagerImpl
implements|implements
name|QueryManager
block|{
specifier|private
specifier|final
name|QueryObjectModelFactoryImpl
name|qomFactory
decl_stmt|;
specifier|private
specifier|final
name|SessionQueryEngine
name|queryEngine
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|supportedQueryLanguages
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|QueryManagerImpl
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|qomFactory
operator|=
operator|new
name|QueryObjectModelFactoryImpl
argument_list|(
name|this
argument_list|,
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
argument_list|)
expr_stmt|;
name|queryEngine
operator|=
name|sessionDelegate
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|supportedQueryLanguages
operator|.
name|addAll
argument_list|(
name|queryEngine
operator|.
name|getSupportedQueryLanguages
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|QueryImpl
name|createQuery
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|supportedQueryLanguages
operator|.
name|contains
argument_list|(
name|language
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
literal|"The specified language is not supported: "
operator|+
name|language
argument_list|)
throw|;
block|}
return|return
operator|new
name|QueryImpl
argument_list|(
name|this
argument_list|,
name|statement
argument_list|,
name|language
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryObjectModelFactory
name|getQOMFactory
parameter_list|()
block|{
return|return
name|qomFactory
return|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|isNodeType
argument_list|(
name|NodeType
operator|.
name|NT_QUERY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
literal|"Not an nt:query node: "
operator|+
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|statement
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"statement"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|String
name|language
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"language"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
name|QueryImpl
name|query
init|=
name|createQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|)
decl_stmt|;
name|query
operator|.
name|setStoredQueryPath
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getSupportedQueryLanguages
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|queryEngine
operator|.
name|getSupportedQueryLanguages
argument_list|()
argument_list|)
decl_stmt|;
comment|// JQOM is supported in this level only (converted to JCR_SQL2)
name|list
operator|.
name|add
argument_list|(
name|Query
operator|.
name|JCR_JQOM
argument_list|)
expr_stmt|;
comment|// create a new instance each time because the array is mutable
comment|// (the caller could modify it)
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Parse the query and get the bind variable names.      *       * @param statement the query statement      * @param language the query language      * @return the bind variable names      */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|parse
parameter_list|(
name|String
name|statement
parameter_list|,
name|String
name|language
parameter_list|)
throws|throws
name|InvalidQueryException
block|{
try|try
block|{
return|return
name|queryEngine
operator|.
name|getBindVariableNames
argument_list|(
name|statement
argument_list|,
name|language
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
name|InvalidQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|QueryResult
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|bindVariableMap
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|bindMap
init|=
name|convertMap
argument_list|(
name|bindVariableMap
argument_list|)
decl_stmt|;
name|NamePathMapper
name|namePathMapper
init|=
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
decl_stmt|;
name|Result
name|r
init|=
name|queryEngine
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|language
argument_list|,
name|limit
argument_list|,
name|offset
argument_list|,
name|bindMap
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryResultImpl
argument_list|(
name|sessionDelegate
argument_list|,
name|r
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|convertMap
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|bindVariableMap
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|PropertyValue
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|e
range|:
name|bindVariableMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|PropertyValues
operator|.
name|create
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|""
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|public
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|sessionDelegate
return|;
block|}
name|void
name|ensureIsAlive
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// check session status
if|if
condition|(
operator|!
name|sessionDelegate
operator|.
name|isAlive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"This session has been closed."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

