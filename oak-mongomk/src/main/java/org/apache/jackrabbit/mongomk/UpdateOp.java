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
name|mongomk
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * A MongoDB "update" operation for one node.  */
end_comment

begin_class
specifier|public
class|class
name|UpdateOp
block|{
comment|/**      * The node id, which contains the depth of the path      * (0 for root, 1 for children of the root), and then the path.      */
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"_id"
decl_stmt|;
comment|/**      * The last revision. Key: machine id, value: revision.      */
specifier|static
specifier|final
name|String
name|LAST_REV
init|=
literal|"_lastRev"
decl_stmt|;
comment|/**      * The list of recent revisions for this node, where this node is the      * root of the commit. Key: revision, value: true or the base revision of an      * un-merged branch commit.      */
specifier|static
specifier|final
name|String
name|REVISIONS
init|=
literal|"_revisions"
decl_stmt|;
comment|/**      * The list of revision to root commit depth mappings to find out if a      * revision is actually committed.      */
specifier|static
specifier|final
name|String
name|COMMIT_ROOT
init|=
literal|"_commitRoot"
decl_stmt|;
comment|/**      * The number of previous documents (documents that contain old revisions of      * this node). This property is only set if multiple documents per node      * exist. This is the case when a node is updated very often in a short      * time, such that the document gets very big.      */
specifier|static
specifier|final
name|String
name|PREVIOUS
init|=
literal|"_prev"
decl_stmt|;
comment|/**      * Whether this node is deleted. Key: revision, value: true/false.      */
specifier|static
specifier|final
name|String
name|DELETED
init|=
literal|"_deleted"
decl_stmt|;
comment|/**      * Revision collision markers set by commits with modifications, which      * overlap with un-merged branch commits.      * Key: revision, value:      */
specifier|static
specifier|final
name|String
name|COLLISIONS
init|=
literal|"_collisions"
decl_stmt|;
comment|/**      * The modified time (5 second resolution).      */
specifier|static
specifier|final
name|String
name|MODIFIED
init|=
literal|"_modified"
decl_stmt|;
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|String
name|key
decl_stmt|;
specifier|final
name|boolean
name|isNew
decl_stmt|;
name|boolean
name|isDelete
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Create an update operation for the given document. The commit root is assumed      * to be the path, unless this is changed later on.      *       * @param path the node path (for nodes)      * @param key the primary key      * @param isNew whether this is a new document      */
name|UpdateOp
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|isNew
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|isNew
operator|=
name|isNew
expr_stmt|;
block|}
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|isNew
return|;
block|}
name|void
name|setDelete
parameter_list|(
name|boolean
name|isDelete
parameter_list|)
block|{
name|this
operator|.
name|isDelete
operator|=
name|isDelete
expr_stmt|;
block|}
comment|/**      * Add a new or update an existing map entry.      * The property is a map of sub-names / values.      *       * @param property the property      * @param subName the entry name      * @param value the value      */
name|void
name|setMapEntry
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|subName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP_ENTRY
expr_stmt|;
name|op
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
operator|+
literal|"."
operator|+
name|subName
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove a map entry.      * The property is a map of sub-names / values.      *       * @param property the property      * @param subName the entry name      */
specifier|public
name|void
name|removeMapEntry
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|subName
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|REMOVE_MAP_ENTRY
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
operator|+
literal|"."
operator|+
name|subName
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set a map to a single key-value pair.      * The property is a map of sub-names / values.      *       * @param property the property      * @param subName the entry name      * @param value the value      */
specifier|public
name|void
name|setMap
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|subName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP
expr_stmt|;
name|op
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
operator|+
literal|"."
operator|+
name|subName
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the property to the given value.      *       * @param property the property name      * @param value the value      */
name|void
name|set
parameter_list|(
name|String
name|property
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|SET
expr_stmt|;
name|op
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Do not set the property (after it has been set).      *       * @param property the property name      */
name|void
name|unset
parameter_list|(
name|String
name|property
parameter_list|)
block|{
name|changes
operator|.
name|remove
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
comment|/**      * Do not set the property entry (after it has been set).      * The property is a map of sub-names / values.      *       * @param property the property name      * @param subName the entry name      */
name|void
name|unsetMapEntry
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|subName
parameter_list|)
block|{
name|changes
operator|.
name|remove
argument_list|(
name|property
operator|+
literal|"."
operator|+
name|subName
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the named key exists or is absent in the MongoDB document. This      * method can be used to make a conditional update.      *      * @param property the property name      * @param subName the entry name      */
name|void
name|containsMapEntry
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|subName
parameter_list|,
name|boolean
name|exists
parameter_list|)
block|{
if|if
condition|(
name|isNew
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot use containsMapEntry() on new document"
argument_list|)
throw|;
block|}
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|CONTAINS_MAP_ENTRY
expr_stmt|;
name|op
operator|.
name|value
operator|=
name|exists
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
operator|+
literal|"."
operator|+
name|subName
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increment the value.      *       * @param property the key      * @param value the increment      */
name|void
name|increment
parameter_list|(
name|String
name|property
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|()
decl_stmt|;
name|op
operator|.
name|type
operator|=
name|Operation
operator|.
name|Type
operator|.
name|INCREMENT
expr_stmt|;
name|op
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|changes
operator|.
name|put
argument_list|(
name|property
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Long
name|getIncrement
parameter_list|(
name|String
name|property
parameter_list|)
block|{
name|Operation
name|op
init|=
name|changes
operator|.
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|op
operator|.
name|type
operator|!=
name|Operation
operator|.
name|Type
operator|.
name|INCREMENT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an increment operation"
argument_list|)
throw|;
block|}
return|return
operator|(
name|Long
operator|)
name|op
operator|.
name|value
return|;
block|}
specifier|public
name|UpdateOp
name|getReverseOperation
parameter_list|()
block|{
name|UpdateOp
name|reverse
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|key
argument_list|,
name|isNew
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
name|e
range|:
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Operation
name|r
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getReverse
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|reverse
operator|.
name|changes
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|reverse
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"key: "
operator|+
name|key
operator|+
literal|" "
operator|+
operator|(
name|isNew
condition|?
literal|"new"
else|:
literal|"update"
operator|)
operator|+
literal|" "
operator|+
name|changes
return|;
block|}
comment|/**      * A MongoDB operation for a given key within a document.       */
specifier|public
specifier|static
class|class
name|Operation
block|{
comment|/**          * The MongoDB operation type.          */
specifier|public
enum|enum
name|Type
block|{
comment|/**              * Set the value.               * The sub-key is not used.              */
name|SET
block|,
comment|/**              * Increment the Long value with the provided Long value.              * The sub-key is not used.              */
name|INCREMENT
block|,
comment|/**              * Add the sub-key / value pair.              * The value in the stored node is a map.              */
name|SET_MAP_ENTRY
block|,
comment|/**              * Remove the sub-key / value pair.              * The value in the stored node is a map.              */
name|REMOVE_MAP_ENTRY
block|,
comment|/**              * Checks if the sub-key is present in a map or not.              */
name|CONTAINS_MAP_ENTRY
block|,
comment|/**              * Set the sub-key / value pair.              * The value in the stored node is a map.              */
name|SET_MAP
block|,           }
comment|/**          * The operation type.          */
name|Type
name|type
decl_stmt|;
comment|/**          * The value, if any.          */
name|Object
name|value
decl_stmt|;
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
operator|+
literal|" "
operator|+
name|value
return|;
block|}
specifier|public
name|Operation
name|getReverse
parameter_list|()
block|{
name|Operation
name|reverse
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INCREMENT
case|:
name|reverse
operator|=
operator|new
name|Operation
argument_list|()
expr_stmt|;
name|reverse
operator|.
name|type
operator|=
name|Type
operator|.
name|INCREMENT
expr_stmt|;
name|reverse
operator|.
name|value
operator|=
operator|-
operator|(
name|Long
operator|)
name|value
expr_stmt|;
break|break;
case|case
name|SET
case|:
case|case
name|REMOVE_MAP_ENTRY
case|:
case|case
name|SET_MAP
case|:
case|case
name|CONTAINS_MAP_ENTRY
case|:
comment|// nothing to do
break|break;
case|case
name|SET_MAP_ENTRY
case|:
name|reverse
operator|=
operator|new
name|Operation
argument_list|()
expr_stmt|;
name|reverse
operator|.
name|type
operator|=
name|Type
operator|.
name|REMOVE_MAP_ENTRY
expr_stmt|;
break|break;
block|}
return|return
name|reverse
return|;
block|}
block|}
block|}
end_class

end_unit

