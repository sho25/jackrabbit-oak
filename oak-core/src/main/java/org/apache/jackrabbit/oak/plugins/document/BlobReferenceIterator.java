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
name|plugins
operator|.
name|document
package|;
end_package

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
name|List
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
name|blob
operator|.
name|ReferencedBlob
import|;
end_import

begin_comment
comment|/**  * An iterator over all referenced binaries.  *<p>  * Only top-level referenced are returned (indirection, if any, is not resolved).  * The items are returned in no particular order.  * An item might be returned multiple times.  */
end_comment

begin_class
specifier|public
class|class
name|BlobReferenceIterator
implements|implements
name|Iterator
argument_list|<
name|ReferencedBlob
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|BATCH_SIZE
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|docStore
decl_stmt|;
specifier|private
specifier|final
name|BlobCollector
name|blobCollector
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|ReferencedBlob
argument_list|>
name|batch
init|=
operator|new
name|HashSet
argument_list|<
name|ReferencedBlob
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|ReferencedBlob
argument_list|>
name|batchIterator
decl_stmt|;
specifier|private
name|boolean
name|done
decl_stmt|;
specifier|private
name|String
name|fromKey
init|=
name|NodeDocument
operator|.
name|MIN_ID_VALUE
decl_stmt|;
specifier|public
name|BlobReferenceIterator
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|docStore
operator|=
name|nodeStore
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
name|batchIterator
operator|=
name|batch
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|blobCollector
operator|=
operator|new
name|BlobCollector
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|batchIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|loadBatch
argument_list|()
expr_stmt|;
block|}
return|return
name|batchIterator
operator|.
name|hasNext
argument_list|()
operator|||
operator|!
name|done
return|;
block|}
annotation|@
name|Override
specifier|public
name|ReferencedBlob
name|next
parameter_list|()
block|{
comment|// this will load the next batch if required
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|batchIterator
operator|.
name|next
argument_list|()
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
specifier|private
name|void
name|loadBatch
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
return|return;
block|}
name|batch
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// read until at least BATCH_SIZE references are available
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|hasMore
init|=
name|loadBatchQuery
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|hasMore
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
name|BATCH_SIZE
condition|)
block|{
break|break;
block|}
block|}
name|batchIterator
operator|=
name|batch
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
specifier|private
name|boolean
name|loadBatchQuery
parameter_list|()
block|{
comment|// read about BATCH_SIZE documents
name|List
argument_list|<
name|NodeDocument
argument_list|>
name|list
init|=
name|docStore
operator|.
name|query
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|fromKey
argument_list|,
name|NodeDocument
operator|.
name|MAX_ID_VALUE
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_FLAG
argument_list|,
name|NodeDocument
operator|.
name|HAS_BINARY_VAL
argument_list|,
name|BATCH_SIZE
argument_list|)
decl_stmt|;
name|boolean
name|hasMore
init|=
literal|false
decl_stmt|;
for|for
control|(
name|NodeDocument
name|doc
range|:
name|list
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|fromKey
argument_list|)
condition|)
block|{
comment|// already read
continue|continue;
block|}
name|hasMore
operator|=
literal|true
expr_stmt|;
name|fromKey
operator|=
name|doc
operator|.
name|getId
argument_list|()
expr_stmt|;
name|blobCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|batch
argument_list|)
expr_stmt|;
block|}
return|return
name|hasMore
return|;
block|}
block|}
end_class

end_unit

