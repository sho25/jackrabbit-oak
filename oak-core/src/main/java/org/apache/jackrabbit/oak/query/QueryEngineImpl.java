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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|CoreValue
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
name|CoreValueFactory
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
name|query
operator|.
name|index
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
name|query
operator|.
name|index
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
name|query
operator|.
name|index
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
name|query
operator|.
name|index
operator|.
name|TraversingIndex
import|;
end_import

begin_class
specifier|public
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
specifier|private
specifier|static
specifier|final
name|String
name|XPATH
init|=
literal|"xpath"
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|mk
decl_stmt|;
specifier|private
specifier|final
name|CoreValueFactory
name|vf
decl_stmt|;
specifier|private
specifier|final
name|SQL2Parser
name|parserSQL2
decl_stmt|;
specifier|private
specifier|final
name|QueryIndexProvider
name|indexProvider
decl_stmt|;
specifier|public
name|QueryEngineImpl
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|CoreValueFactory
name|valueFactory
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
name|this
operator|.
name|vf
operator|=
name|valueFactory
expr_stmt|;
name|this
operator|.
name|indexProvider
operator|=
name|indexProvider
expr_stmt|;
name|parserSQL2
operator|=
operator|new
name|SQL2Parser
argument_list|(
name|vf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
block|{
comment|// TODO the list of index providers should be configurable as well
name|indexProvider
operator|.
name|init
argument_list|()
expr_stmt|;
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
condition|)
block|{
name|q
operator|=
name|parserSQL2
operator|.
name|parse
argument_list|(
name|statement
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
name|q
operator|=
name|parserSQL2
operator|.
name|parse
argument_list|(
name|sql2
argument_list|)
expr_stmt|;
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
return|return
name|q
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResultImpl
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
name|CoreValue
argument_list|>
name|bindings
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
name|setMicroKernel
argument_list|(
name|mk
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
name|CoreValue
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
argument_list|(
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|QueryIndex
name|getBestIndex
parameter_list|(
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
argument_list|()
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|cost
operator|<
name|bestCost
condition|)
block|{
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
name|best
operator|=
operator|new
name|TraversingIndex
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
return|return
name|best
return|;
block|}
specifier|private
name|List
argument_list|<
name|QueryIndex
argument_list|>
name|getIndexes
parameter_list|()
block|{
return|return
name|indexProvider
operator|.
name|getQueryIndexes
argument_list|(
name|mk
argument_list|)
return|;
block|}
block|}
end_class

end_unit

