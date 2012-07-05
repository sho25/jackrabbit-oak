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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|util
operator|.
name|UnmodifiableIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|StoredNode
extends|extends
name|AbstractNode
block|{
specifier|private
specifier|final
name|Id
name|id
decl_stmt|;
specifier|public
name|StoredNode
parameter_list|(
name|Id
name|id
parameter_list|,
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|StoredNode
parameter_list|(
name|Id
name|id
parameter_list|,
name|RevisionProvider
name|provider
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
parameter_list|,
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|cneIt
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|properties
operator|.
name|putAll
argument_list|(
name|properties
argument_list|)
expr_stmt|;
while|while
condition|(
name|cneIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|childEntries
operator|.
name|add
argument_list|(
name|cneIt
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|StoredNode
parameter_list|(
name|Id
name|id
parameter_list|,
name|Node
name|node
parameter_list|,
name|RevisionProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|,
name|provider
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|Id
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
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
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|properties
argument_list|)
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
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
operator|new
name|UnmodifiableIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|super
operator|.
name|getChildNodeEntries
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
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
operator|new
name|UnmodifiableIterator
argument_list|<
name|String
argument_list|>
argument_list|(
name|super
operator|.
name|getChildNodeNames
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|deserialize
parameter_list|(
name|Binding
name|binding
parameter_list|)
throws|throws
name|Exception
block|{
name|Binding
operator|.
name|StringEntryIterator
name|iter
init|=
name|binding
operator|.
name|readStringMap
argument_list|(
literal|":props"
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
name|Binding
operator|.
name|StringEntry
name|entry
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
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
expr_stmt|;
block|}
name|boolean
name|inlined
init|=
name|binding
operator|.
name|readIntValue
argument_list|(
literal|":inlined"
argument_list|)
operator|!=
literal|0
decl_stmt|;
if|if
condition|(
name|inlined
condition|)
block|{
name|childEntries
operator|=
name|ChildNodeEntriesMap
operator|.
name|deserialize
argument_list|(
name|binding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childEntries
operator|=
name|ChildNodeEntriesTree
operator|.
name|deserialize
argument_list|(
name|provider
argument_list|,
name|binding
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

