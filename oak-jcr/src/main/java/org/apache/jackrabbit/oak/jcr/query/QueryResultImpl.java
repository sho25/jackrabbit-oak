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
name|SessionDelegate
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
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|final
name|Result
name|result
decl_stmt|;
specifier|final
name|String
name|pathFilter
decl_stmt|;
specifier|public
name|QueryResultImpl
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Result
name|result
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
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
elseif|else
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
name|it
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
return|return
operator|new
name|RowIteratorAdapter
argument_list|(
name|it
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
name|NodeImpl
name|getNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
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
operator|new
name|NodeImpl
argument_list|(
name|d
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
name|getSelectorNames
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|NodeImpl
argument_list|>
name|it
init|=
operator|new
name|Iterator
argument_list|<
name|NodeImpl
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
name|String
name|path
init|=
name|r
operator|.
name|getPath
argument_list|()
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
decl_stmt|;
return|return
operator|new
name|NodeIteratorAdapter
argument_list|(
name|it
argument_list|)
return|;
block|}
name|Value
name|createValue
parameter_list|(
name|CoreValue
name|value
parameter_list|)
block|{
return|return
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

