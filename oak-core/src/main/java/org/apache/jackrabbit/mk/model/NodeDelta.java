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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|NodeDelta
implements|implements
name|NodeDiffHandler
block|{
specifier|public
specifier|static
enum|enum
name|ConflictType
block|{
comment|/**          * same property has been added or set, but with differing values          */
name|PROPERTY_VALUE_CONFLICT
block|,
comment|/**          * child nodes with identical name have been added or modified, but          * with differing id's; the corresponding node subtrees are hence differing          * and potentially conflicting.          */
name|NODE_CONTENT_CONFLICT
block|,
comment|/**          * a modified property has been deleted          */
name|REMOVED_DIRTY_PROPERTY_CONFLICT
block|,
comment|/**          * a child node entry pointing to a modified subtree has been deleted          */
name|REMOVED_DIRTY_NODE_CONFLICT
block|}
specifier|final
name|StoredNode
name|node1
decl_stmt|;
specifier|final
name|StoredNode
name|node2
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|addedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|removedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|addedChildNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|removedChildNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|changedChildNodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|NodeDelta
parameter_list|(
name|StoredNode
name|node1
parameter_list|,
name|StoredNode
name|node2
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|node1
operator|=
name|node1
expr_stmt|;
name|this
operator|.
name|node2
operator|=
name|node2
expr_stmt|;
name|node1
operator|.
name|diff
argument_list|(
name|node2
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAddedProperties
parameter_list|()
block|{
return|return
name|addedProperties
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getRemovedProperties
parameter_list|()
block|{
return|return
name|removedProperties
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getChangedProperties
parameter_list|()
block|{
return|return
name|changedProperties
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAddedChildNodes
parameter_list|()
block|{
return|return
name|addedChildNodes
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getRemovedChildNodes
parameter_list|()
block|{
return|return
name|removedChildNodes
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getChangedChildNodes
parameter_list|()
block|{
return|return
name|changedChildNodes
return|;
block|}
specifier|public
name|boolean
name|conflictsWith
parameter_list|(
name|NodeDelta
name|other
parameter_list|)
block|{
return|return
operator|!
name|listConflicts
argument_list|(
name|other
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|Conflict
argument_list|>
name|listConflicts
parameter_list|(
name|NodeDelta
name|other
parameter_list|)
block|{
comment|// assume that both delta's were built using the *same* base node revision
if|if
condition|(
operator|!
name|node1
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"other and this NodeDelta object are expected to share common node1 instance"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Conflict
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<
name|Conflict
argument_list|>
argument_list|()
decl_stmt|;
comment|// properties
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherAdded
init|=
name|other
operator|.
name|getAddedProperties
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
name|added
range|:
name|addedProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|otherValue
init|=
name|otherAdded
operator|.
name|get
argument_list|(
name|added
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherValue
operator|!=
literal|null
operator|&&
operator|!
name|added
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|otherValue
argument_list|)
condition|)
block|{
comment|// same property added with conflicting values
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|PROPERTY_VALUE_CONFLICT
argument_list|,
name|added
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherChanged
init|=
name|other
operator|.
name|getChangedProperties
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherRemoved
init|=
name|other
operator|.
name|getRemovedProperties
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
name|changed
range|:
name|changedProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|otherValue
init|=
name|otherChanged
operator|.
name|get
argument_list|(
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherValue
operator|!=
literal|null
operator|&&
operator|!
name|changed
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|otherValue
argument_list|)
condition|)
block|{
comment|// same property changed with conflicting values
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|PROPERTY_VALUE_CONFLICT
argument_list|,
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherRemoved
operator|.
name|containsKey
argument_list|(
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// changed property has been removed
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|REMOVED_DIRTY_PROPERTY_CONFLICT
argument_list|,
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|removed
range|:
name|removedProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|otherChanged
operator|.
name|containsKey
argument_list|(
name|removed
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// removed property has been changed
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|REMOVED_DIRTY_PROPERTY_CONFLICT
argument_list|,
name|removed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// child node entries
name|otherAdded
operator|=
name|other
operator|.
name|getAddedChildNodes
argument_list|()
expr_stmt|;
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
name|added
range|:
name|addedChildNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|otherValue
init|=
name|otherAdded
operator|.
name|get
argument_list|(
name|added
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherValue
operator|!=
literal|null
operator|&&
operator|!
name|added
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|otherValue
argument_list|)
condition|)
block|{
comment|// same child node entry added with different target id's
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|NODE_CONTENT_CONFLICT
argument_list|,
name|added
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|otherChanged
operator|=
name|other
operator|.
name|getChangedChildNodes
argument_list|()
expr_stmt|;
name|otherRemoved
operator|=
name|other
operator|.
name|getRemovedChildNodes
argument_list|()
expr_stmt|;
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
name|changed
range|:
name|changedChildNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|otherValue
init|=
name|otherChanged
operator|.
name|get
argument_list|(
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherValue
operator|!=
literal|null
operator|&&
operator|!
name|changed
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|otherValue
argument_list|)
condition|)
block|{
comment|// same child node entry changed with different target id's
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|NODE_CONTENT_CONFLICT
argument_list|,
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherRemoved
operator|.
name|containsKey
argument_list|(
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// changed child node entry has been removed
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|REMOVED_DIRTY_NODE_CONFLICT
argument_list|,
name|changed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|removed
range|:
name|removedChildNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|otherChanged
operator|.
name|containsKey
argument_list|(
name|removed
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// removed child node entry has been changed
name|conflicts
operator|.
name|add
argument_list|(
operator|new
name|Conflict
argument_list|(
name|ConflictType
operator|.
name|REMOVED_DIRTY_NODE_CONFLICT
argument_list|,
name|removed
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conflicts
return|;
block|}
comment|//------------------------------------------------------< NodeDiffHandler>
specifier|public
name|void
name|propAdded
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|addedProperties
operator|.
name|put
argument_list|(
name|propName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|propChanged
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|oldValue
parameter_list|,
name|String
name|newValue
parameter_list|)
block|{
name|changedProperties
operator|.
name|put
argument_list|(
name|propName
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|propDeleted
parameter_list|(
name|String
name|propName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|removedProperties
operator|.
name|put
argument_list|(
name|propName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|ChildNodeEntry
name|added
parameter_list|)
block|{
name|addedChildNodes
operator|.
name|put
argument_list|(
name|added
operator|.
name|getName
argument_list|()
argument_list|,
name|added
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|ChildNodeEntry
name|deleted
parameter_list|)
block|{
name|removedChildNodes
operator|.
name|put
argument_list|(
name|deleted
operator|.
name|getName
argument_list|()
argument_list|,
name|deleted
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|ChildNodeEntry
name|changed
parameter_list|,
name|String
name|newId
parameter_list|)
block|{
name|changedChildNodes
operator|.
name|put
argument_list|(
name|changed
operator|.
name|getName
argument_list|()
argument_list|,
name|newId
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------< inner classes>
specifier|public
specifier|static
class|class
name|Conflict
block|{
specifier|final
name|ConflictType
name|type
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
comment|/**          * @param type conflict type          * @param name name of conflicting property or child node          */
name|Conflict
parameter_list|(
name|ConflictType
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|ConflictType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

