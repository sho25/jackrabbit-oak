begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
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
name|mk
operator|.
name|store
operator|.
name|PersistHook
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
name|store
operator|.
name|RevisionProvider
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
name|store
operator|.
name|RevisionStore
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|MutableNode
extends|extends
name|AbstractNode
implements|implements
name|PersistHook
block|{
specifier|public
name|MutableNode
parameter_list|(
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MutableNode
parameter_list|(
name|Node
name|other
parameter_list|,
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ChildNodeEntry
name|add
parameter_list|(
name|ChildNodeEntry
name|newEntry
parameter_list|)
block|{
name|ChildNodeEntry
name|existing
init|=
name|childEntries
operator|.
name|add
argument_list|(
name|newEntry
argument_list|)
decl_stmt|;
if|if
condition|(
name|childEntries
operator|.
name|getCount
argument_list|()
operator|>
name|ChildNodeEntries
operator|.
name|CAPACITY_THRESHOLD
operator|&&
name|childEntries
operator|.
name|inlined
argument_list|()
condition|)
block|{
name|ChildNodeEntries
name|entries
init|=
operator|new
name|ChildNodeEntriesTree
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|iter
init|=
name|childEntries
operator|.
name|getEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|childEntries
operator|=
name|entries
expr_stmt|;
block|}
return|return
name|existing
return|;
block|}
specifier|public
name|ChildNodeEntry
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|childEntries
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|ChildNodeEntry
name|rename
parameter_list|(
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
return|return
name|childEntries
operator|.
name|rename
argument_list|(
name|oldName
argument_list|,
name|newName
argument_list|)
return|;
block|}
comment|//----------------------------------------------------------< PersistHook>
annotation|@
name|Override
specifier|public
name|void
name|prePersist
parameter_list|(
name|RevisionStore
name|store
parameter_list|,
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|childEntries
operator|.
name|inlined
argument_list|()
condition|)
block|{
comment|// persist dirty buckets
operator|(
operator|(
name|ChildNodeEntriesTree
operator|)
name|childEntries
operator|)
operator|.
name|persistDirtyBuckets
argument_list|(
name|store
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|postPersist
parameter_list|(
name|RevisionStore
name|store
parameter_list|,
name|RevisionStore
operator|.
name|PutToken
name|token
parameter_list|)
throws|throws
name|Exception
block|{
comment|// there's nothing to do
block|}
block|}
end_class

end_unit

