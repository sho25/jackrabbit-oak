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
name|Binding
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
name|Iterator
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNode
implements|implements
name|Node
block|{
specifier|protected
name|RevisionProvider
name|provider
decl_stmt|;
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
decl_stmt|;
specifier|protected
name|ChildNodeEntries
name|childEntries
decl_stmt|;
specifier|protected
name|AbstractNode
parameter_list|(
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|properties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|childEntries
operator|=
operator|new
name|ChildNodeEntriesMap
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|AbstractNode
parameter_list|(
name|Node
name|other
parameter_list|,
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|AbstractNode
condition|)
block|{
name|AbstractNode
name|srcNode
init|=
operator|(
name|AbstractNode
operator|)
name|other
decl_stmt|;
name|this
operator|.
name|properties
operator|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|srcNode
operator|.
name|properties
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|childEntries
operator|=
operator|(
name|ChildNodeEntries
operator|)
name|srcNode
operator|.
name|childEntries
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|properties
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|other
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|getChildNodeCount
argument_list|()
operator|<=
name|ChildNodeEntries
operator|.
name|CAPACITY_THRESHOLD
condition|)
block|{
name|this
operator|.
name|childEntries
operator|=
operator|new
name|ChildNodeEntriesMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|childEntries
operator|=
operator|new
name|ChildNodeEntriesTree
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|other
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ChildNode
name|cne
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|this
operator|.
name|childEntries
operator|.
name|add
argument_list|(
name|cne
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
specifier|public
name|ChildNode
name|getChildNodeEntry
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|childEntries
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
name|childEntries
operator|.
name|getNames
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
specifier|public
name|int
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|childEntries
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|getChildNodeEntries
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
name|childEntries
operator|.
name|getEntries
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
specifier|public
name|void
name|diff
parameter_list|(
name|Node
name|other
parameter_list|,
name|NodeDiffHandler
name|handler
parameter_list|)
block|{
comment|// compare properties
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|oldProps
init|=
name|getProperties
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|newProps
init|=
name|other
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|oldProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|val
init|=
name|oldProps
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|newVal
init|=
name|newProps
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|newVal
operator|==
literal|null
condition|)
block|{
name|handler
operator|.
name|propDeleted
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|val
operator|.
name|equals
argument_list|(
name|newVal
argument_list|)
condition|)
block|{
name|handler
operator|.
name|propChanged
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|newVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|newProps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|oldProps
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|handler
operator|.
name|propAdded
argument_list|(
name|name
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// compare child node entries
if|if
condition|(
name|other
operator|instanceof
name|AbstractNode
condition|)
block|{
comment|// OAK-46: Efficient diffing of large child node lists
comment|// delegate to ChildNodeEntries implementation
name|ChildNodeEntries
name|otherEntries
init|=
operator|(
operator|(
name|AbstractNode
operator|)
name|other
operator|)
operator|.
name|childEntries
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|childEntries
operator|.
name|getAdded
argument_list|(
name|otherEntries
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|handler
operator|.
name|childNodeAdded
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|childEntries
operator|.
name|getRemoved
argument_list|(
name|otherEntries
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|handler
operator|.
name|childNodeDeleted
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|childEntries
operator|.
name|getModified
argument_list|(
name|otherEntries
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ChildNode
name|old
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|ChildNode
name|modified
init|=
name|otherEntries
operator|.
name|get
argument_list|(
name|old
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|handler
operator|.
name|childNodeChanged
argument_list|(
name|old
argument_list|,
name|modified
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ChildNode
name|child
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|ChildNode
name|newChild
init|=
name|other
operator|.
name|getChildNodeEntry
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newChild
operator|==
literal|null
condition|)
block|{
name|handler
operator|.
name|childNodeDeleted
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|child
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|newChild
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|handler
operator|.
name|childNodeChanged
argument_list|(
name|child
argument_list|,
name|newChild
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|it
init|=
name|other
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ChildNode
name|child
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|getChildNodeEntry
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|handler
operator|.
name|childNodeAdded
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|iter
init|=
name|properties
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|binding
operator|.
name|writeMap
argument_list|(
literal|":props"
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|Binding
operator|.
name|StringEntryIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Binding
operator|.
name|StringEntry
name|next
parameter_list|()
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|Binding
operator|.
name|StringEntry
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
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
argument_list|)
expr_stmt|;
name|binding
operator|.
name|write
argument_list|(
literal|":inlined"
argument_list|,
name|childEntries
operator|.
name|inlined
argument_list|()
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
name|childEntries
operator|.
name|serialize
argument_list|(
name|binding
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

