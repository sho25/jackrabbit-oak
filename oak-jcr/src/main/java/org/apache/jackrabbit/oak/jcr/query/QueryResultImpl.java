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
name|util
operator|.
name|Arrays
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
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
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
name|RowIterator
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
name|NodeIteratorAdapter
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
name|RowIteratorAdapter
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
name|jcr
operator|.
name|query
operator|.
name|PrefetchIterator
operator|.
name|PrefetchOptions
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
name|session
operator|.
name|NodeImpl
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
name|session
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
name|delegate
operator|.
name|NodeDelegate
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
name|delegate
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
name|plugins
operator|.
name|value
operator|.
name|jcr
operator|.
name|ValueFactoryImpl
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
comment|/**  * The implementation of the corresponding JCR interface.  */
end_comment

begin_class
specifier|public
class|class
name|QueryResultImpl
implements|implements
name|QueryResult
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|queryOpsLogger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.jcr.operations.query"
argument_list|)
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
name|QueryResultImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|final
name|Result
name|result
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|public
name|QueryResultImpl
parameter_list|(
name|SessionContext
name|sessionContext
parameter_list|,
name|Result
name|result
parameter_list|)
block|{
name|this
operator|.
name|sessionContext
operator|=
name|sessionContext
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionContext
operator|.
name|getSessionDelegate
argument_list|()
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getColumnNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|result
operator|.
name|getColumnNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getSelectorNames
parameter_list|()
block|{
return|return
name|result
operator|.
name|getSelectorNames
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RowIterator
name|getRows
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|RowImpl
argument_list|>
name|rowIterator
init|=
operator|new
name|Iterator
argument_list|<
name|RowImpl
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|it
init|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathSelector
decl_stmt|;
specifier|private
name|RowImpl
name|current
decl_stmt|;
specifier|private
name|int
name|rowCount
decl_stmt|;
comment|//Avoid log check for every row access
specifier|private
specifier|final
name|boolean
name|debugEnabled
init|=
name|queryOpsLogger
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
block|{
name|String
index|[]
name|columnSelectorNames
init|=
name|result
operator|.
name|getColumnSelectorNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|columnSelectorNames
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|pathSelector
operator|=
name|columnSelectorNames
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|pathSelector
operator|=
literal|null
expr_stmt|;
block|}
name|fetch
parameter_list|()
constructor_decl|;
block|}
specifier|private
name|void
name|fetch
parameter_list|()
block|{
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
operator|new
name|RowImpl
argument_list|(
name|QueryResultImpl
operator|.
name|this
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|,
name|pathSelector
argument_list|)
expr_stmt|;
if|if
condition|(
name|debugEnabled
condition|)
block|{
name|rowCount
operator|++
expr_stmt|;
if|if
condition|(
name|rowCount
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|queryOpsLogger
operator|.
name|debug
argument_list|(
literal|"Iterated over [{}] results so far"
argument_list|,
name|rowCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|current
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|RowImpl
name|next
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|RowImpl
name|r
init|=
name|current
decl_stmt|;
name|fetch
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
specifier|final
name|PrefetchIterator
argument_list|<
name|RowImpl
argument_list|>
name|prefIt
init|=
operator|new
name|PrefetchIterator
argument_list|<
name|RowImpl
argument_list|>
argument_list|(
name|sessionDelegate
operator|.
name|sync
argument_list|(
name|rowIterator
argument_list|)
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|size
operator|=
name|result
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|fastSize
operator|=
name|sessionContext
operator|.
name|getFastQueryResultSize
argument_list|()
expr_stmt|;
name|fastSizeCallback
operator|=
name|result
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|RowIteratorAdapter
argument_list|(
name|prefIt
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|prefIt
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nullable
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
name|getNode
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|tree
operator|!=
literal|null
operator|&&
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
name|NodeDelegate
name|node
init|=
operator|new
name|NodeDelegate
argument_list|(
name|sessionDelegate
argument_list|,
name|tree
argument_list|)
decl_stmt|;
return|return
name|NodeImpl
operator|.
name|createNode
argument_list|(
name|node
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeIterator
name|getNodes
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
index|[]
name|columnSelectorNames
init|=
name|result
operator|.
name|getColumnSelectorNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|columnSelectorNames
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Query contains more than one selector: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|columnSelectorNames
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|String
name|selectorName
init|=
name|columnSelectorNames
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|selectorName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"Query does not contain a selector: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|columnSelectorNames
argument_list|)
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
argument_list|>
name|nodeIterator
init|=
operator|new
name|Iterator
argument_list|<
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|it
init|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
name|current
decl_stmt|;
block|{
name|fetch
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|fetch
parameter_list|()
block|{
name|current
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ResultRow
name|r
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Tree
name|tree
init|=
name|r
operator|.
name|getTree
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
operator|&&
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|current
operator|=
name|getNode
argument_list|(
name|tree
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to fetch result node for path "
operator|+
name|tree
operator|.
name|getPath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|current
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
name|next
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
name|n
init|=
name|current
decl_stmt|;
name|fetch
argument_list|()
expr_stmt|;
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
empty_stmt|;
specifier|final
name|PrefetchIterator
argument_list|<
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
argument_list|>
name|prefIt
init|=
operator|new
name|PrefetchIterator
argument_list|<
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
argument_list|>
argument_list|(
name|sessionDelegate
operator|.
name|sync
argument_list|(
name|nodeIterator
argument_list|)
argument_list|,
operator|new
name|PrefetchOptions
argument_list|()
block|{
block|{
name|size
operator|=
name|result
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|fastSize
operator|=
name|sessionContext
operator|.
name|getFastQueryResultSize
argument_list|()
expr_stmt|;
name|fastSizeCallback
operator|=
name|result
expr_stmt|;
block|}
block|}
block|)
function|;
return|return
operator|new
name|NodeIteratorAdapter
argument_list|(
name|prefIt
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|prefIt
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
end_class

begin_function
name|Value
name|createValue
parameter_list|(
name|PropertyValue
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
end_function

unit|}
end_unit

