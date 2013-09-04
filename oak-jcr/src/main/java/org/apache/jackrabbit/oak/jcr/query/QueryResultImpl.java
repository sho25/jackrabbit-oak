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
name|annotation
operator|.
name|CheckForNull
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
name|jcr
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
name|ValueFactoryImpl
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
name|log
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
comment|/**      * The minimum number of rows / nodes to pre-fetch.      */
specifier|private
specifier|static
specifier|final
name|int
name|PREFETCH_MIN
init|=
literal|20
decl_stmt|;
comment|/**      * The maximum number of rows / nodes to pre-fetch.      */
specifier|private
specifier|static
specifier|final
name|int
name|PREFETCH_MAX
init|=
literal|100
decl_stmt|;
comment|/**      * The maximum number of milliseconds to prefetch rows / nodes.      */
specifier|private
specifier|static
specifier|final
name|int
name|PREFETCH_TIMEOUT
init|=
literal|100
decl_stmt|;
specifier|final
name|Result
name|result
decl_stmt|;
specifier|private
specifier|final
name|SessionContext
name|sessionContext
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathFilter
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
comment|// TODO the path currently contains the workspace name
comment|// TODO filter in oak-core once we support workspaces there
name|pathFilter
operator|=
literal|"/"
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
name|boolean
name|includeRow
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
comment|// a row without path (explain,...)
return|return
literal|true
return|;
block|}
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|pathFilter
argument_list|,
name|path
argument_list|)
operator|||
name|pathFilter
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// a row within this workspace
return|return
literal|true
return|;
block|}
return|return
literal|false
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
name|RowImpl
name|current
decl_stmt|;
block|{
name|fetch
parameter_list|()
constructor_decl|;
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
for|for
control|(
name|String
name|s
range|:
name|getSelectorNames
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|r
operator|.
name|getPath
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeRow
argument_list|(
name|path
argument_list|)
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
name|r
argument_list|)
expr_stmt|;
return|return;
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
name|rowIterator
argument_list|,
name|PREFETCH_MIN
argument_list|,
name|PREFETCH_TIMEOUT
argument_list|,
name|PREFETCH_MAX
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
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
name|CheckForNull
name|NodeImpl
argument_list|<
name|?
extends|extends
name|NodeDelegate
argument_list|>
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeDelegate
name|d
init|=
name|sessionDelegate
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|d
operator|==
literal|null
condition|?
literal|null
else|:
name|NodeImpl
operator|.
name|createNode
argument_list|(
name|d
argument_list|,
name|sessionContext
argument_list|)
return|;
block|}
name|String
name|getLocalPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
literal|"/"
argument_list|,
name|PathUtils
operator|.
name|relativize
argument_list|(
name|pathFilter
argument_list|,
name|path
argument_list|)
argument_list|)
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
name|selectorNames
init|=
name|getSelectorNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|getSelectorNames
argument_list|()
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// TODO verify using the last selector is allowed according to the specification,
comment|// otherwise just allow it when using XPath queries, or make XPath queries
comment|// look like they only contain one selector
comment|// throw new RepositoryException("Query contains more than one selector: " +
comment|//        Arrays.toString(getSelectorNames()));
block|}
comment|// use the last selector
specifier|final
name|String
name|selectorName
init|=
name|selectorNames
index|[
name|selectorNames
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
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
name|String
name|path
init|=
name|r
operator|.
name|getPath
argument_list|(
name|selectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeRow
argument_list|(
name|path
argument_list|)
condition|)
block|{
try|try
block|{
name|current
operator|=
name|getNode
argument_list|(
name|getLocalPath
argument_list|(
name|path
argument_list|)
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
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to fetch result node for path "
operator|+
name|path
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
name|nodeIterator
argument_list|,
name|PREFETCH_MIN
argument_list|,
name|PREFETCH_TIMEOUT
argument_list|,
name|PREFETCH_MAX
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
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
block|}
end_class

end_unit

