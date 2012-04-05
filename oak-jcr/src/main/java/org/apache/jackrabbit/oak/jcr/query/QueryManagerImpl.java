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
name|jcr
operator|.
name|SessionContext
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
name|SessionImpl
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
name|WorkspaceImpl
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
name|query
operator|.
name|CoreValue
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
name|HashMap
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
operator|.
name|Entry
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
init|=
operator|new
name|QueryObjectModelFactoryImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|QueryEngine
name|queryEngine
decl_stmt|;
specifier|public
name|QueryManagerImpl
parameter_list|(
name|WorkspaceImpl
name|workspace
parameter_list|,
name|SessionContext
argument_list|<
name|SessionImpl
argument_list|>
name|sessionContext
parameter_list|)
block|{
name|queryEngine
operator|=
name|sessionContext
operator|.
name|getConnection
argument_list|()
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Query
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
comment|// TODO getQuery(Node node): is it needed?
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Feature not implemented"
argument_list|)
throw|;
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// create a new instance each time because the array is mutable
comment|// (the caller could modify it)
name|String
index|[]
name|s
init|=
block|{
name|Query
operator|.
name|JCR_JQOM
block|,
name|Query
operator|.
name|JCR_SQL2
block|,
name|Query
operator|.
name|XPATH
block|}
decl_stmt|;
return|return
name|s
return|;
block|}
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
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|bindVariableMap
parameter_list|,
name|long
name|limit
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
name|bindMap
init|=
name|convertMap
argument_list|(
name|bindVariableMap
argument_list|)
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
name|bindMap
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryResultImpl
argument_list|(
name|r
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
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
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
name|CoreValue
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
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
name|ValueConverter
operator|.
name|convert
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

