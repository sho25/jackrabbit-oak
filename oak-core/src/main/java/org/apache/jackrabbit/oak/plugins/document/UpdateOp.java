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
name|Collections
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
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * A DocumentStore "update" operation for one document.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|UpdateOp
block|{
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
name|boolean
name|isNew
decl_stmt|;
specifier|private
name|boolean
name|isDelete
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|conditions
decl_stmt|;
comment|/**      * Create an update operation for the document with the given id. The commit      * root is assumed to be the path, unless this is changed later on.      *      * @param id the primary key      * @param isNew whether this is a new document      */
specifier|public
name|UpdateOp
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|,
name|boolean
name|isNew
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
name|isNew
argument_list|,
literal|false
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
name|UpdateOp
parameter_list|(
annotation|@
name|Nonnull
name|String
name|id
parameter_list|,
name|boolean
name|isNew
parameter_list|,
name|boolean
name|isDelete
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|conditions
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|checkNotNull
argument_list|(
name|id
argument_list|,
literal|"id must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|isNew
operator|=
name|isNew
expr_stmt|;
name|this
operator|.
name|isDelete
operator|=
name|isDelete
expr_stmt|;
name|this
operator|.
name|changes
operator|=
name|checkNotNull
argument_list|(
name|changes
argument_list|)
expr_stmt|;
name|this
operator|.
name|conditions
operator|=
name|conditions
expr_stmt|;
block|}
specifier|static
name|UpdateOp
name|combine
parameter_list|(
name|String
name|id
parameter_list|,
name|Iterable
argument_list|<
name|UpdateOp
argument_list|>
name|ops
parameter_list|)
block|{
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|changes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|conditions
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|ops
control|)
block|{
name|changes
operator|.
name|putAll
argument_list|(
name|op
operator|.
name|getChanges
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|conditions
operator|!=
literal|null
condition|)
block|{
name|conditions
operator|.
name|putAll
argument_list|(
name|op
operator|.
name|conditions
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|conditions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|conditions
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|changes
argument_list|,
name|conditions
argument_list|)
return|;
block|}
comment|/**      * Creates an update operation for the document with the given id. The      * changes are shared with the this update operation.      *      * @param id the primary key.      */
specifier|public
name|UpdateOp
name|shallowCopy
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
name|isNew
argument_list|,
name|isDelete
argument_list|,
name|changes
argument_list|,
name|conditions
argument_list|)
return|;
block|}
comment|/**      * Creates a deep copy of this update operation. Changes to the returned      * {@code UpdateOp} do not affect this object.      *      * @return a copy of this operation.      */
specifier|public
name|UpdateOp
name|copy
parameter_list|()
block|{
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|conditionMap
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|conditions
operator|!=
literal|null
condition|)
block|{
name|conditionMap
operator|=
operator|new
name|HashMap
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
argument_list|(
name|conditions
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UpdateOp
argument_list|(
name|id
argument_list|,
name|isNew
argument_list|,
name|isDelete
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
argument_list|(
name|changes
argument_list|)
argument_list|,
name|conditionMap
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|isNew
return|;
block|}
specifier|public
name|void
name|setNew
parameter_list|(
name|boolean
name|isNew
parameter_list|)
block|{
name|this
operator|.
name|isNew
operator|=
name|isNew
expr_stmt|;
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
name|boolean
name|isDelete
parameter_list|()
block|{
return|return
name|isDelete
return|;
block|}
specifier|public
name|Map
argument_list|<
name|Key
argument_list|,
name|Operation
argument_list|>
name|getChanges
parameter_list|()
block|{
return|return
name|changes
return|;
block|}
specifier|public
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|getConditions
parameter_list|()
block|{
if|if
condition|(
name|conditions
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|conditions
return|;
block|}
block|}
comment|/**      * Checks if the UpdateOp has any change operation is registered with      * current update operation      *      * @return true if any change operation is created      */
specifier|public
name|boolean
name|hasChanges
parameter_list|()
block|{
return|return
operator|!
name|changes
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Add a new or update an existing map entry.      * The property is a map of revisions / values.      *      * @param property the property      * @param revision the revision      * @param value the value      */
name|void
name|setMapEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|(
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP_ENTRY
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|checkNotNull
argument_list|(
name|revision
argument_list|)
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Remove a map entry.      * The property is a map of revisions / values.      *      * @param property the property      * @param revision the revision      */
specifier|public
name|void
name|removeMapEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|(
name|Operation
operator|.
name|Type
operator|.
name|REMOVE_MAP_ENTRY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|checkNotNull
argument_list|(
name|revision
argument_list|)
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the property to the given long value.      *      * @param property the property name      * @param value the value      */
specifier|public
name|void
name|set
parameter_list|(
name|String
name|property
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|internalSet
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the property to the given boolean value.      *      * @param property the property name      * @param value the value      */
specifier|public
name|void
name|set
parameter_list|(
name|String
name|property
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|internalSet
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the property to the given String value.      *<p>      * Note that {@link Document#ID} must not be set using this method;      * it is sufficiently specified by the id parameter set in the constructor.      *      * @param property the property name      * @param value the value      * @throws IllegalArgumentException      *             if an attempt is made to set {@link Document#ID}.      */
specifier|public
name|void
name|set
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|internalSet
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the property to the given value if the new value is higher than the      * existing value. The property is also set to the given value if the      * property does not yet exist.      *<p>      * The result of a max operation with different types of values is      * undefined.      *      * @param property the name of the property to set.      * @param value the new value for the property.      */
parameter_list|<
name|T
parameter_list|>
name|void
name|max
parameter_list|(
name|String
name|property
parameter_list|,
name|Comparable
argument_list|<
name|T
argument_list|>
name|value
parameter_list|)
block|{
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|(
name|Operation
operator|.
name|Type
operator|.
name|MAX
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Do not set the property entry (after it has been set).      * The property is a map of revisions / values.      *      * @param property the property name      * @param revision the revision      */
name|void
name|unsetMapEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|changes
operator|.
name|remove
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|checkNotNull
argument_list|(
name|revision
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the named key exists or is absent in the MongoDB document. This      * method can be used to make a conditional update.      *      * @param property the property name      * @param revision the revision      */
name|void
name|containsMapEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
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
name|Condition
name|c
init|=
name|exists
condition|?
name|Condition
operator|.
name|EXISTS
else|:
name|Condition
operator|.
name|MISSING
decl_stmt|;
name|getOrCreateConditions
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|checkNotNull
argument_list|(
name|revision
argument_list|)
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the property is equal to the given value.      *      * @param property the name of the property or map.      * @param value the value to compare to ({@code null} checks both for non-existence and the value being null)      */
name|void
name|equals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
name|equals
argument_list|(
name|property
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the property or map entry is equal to the given value.      *      * @param property the name of the property or map.      * @param revision the revision within the map or {@code null} if this check      *                 is for a property.      * @param value the value to compare to ({@code null} checks both for non-existence and the value being null)      */
name|void
name|equals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nullable
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
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
literal|"Cannot perform equals check on new document"
argument_list|)
throw|;
block|}
name|getOrCreateConditions
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|revision
argument_list|)
argument_list|,
name|Condition
operator|.
name|newEqualsCondition
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the property does not exist or is not equal to the given value.      *      * @param property the name of the property or map.      * @param value the value to compare to.      */
name|void
name|notEquals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
name|notEquals
argument_list|(
name|property
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks if the property or map entry does not exist or is not equal to the given value.      *      * @param property the name of the property or map.      * @param revision the revision within the map or {@code null} if this check      *                 is for a property.      * @param value the value to compare to.      */
name|void
name|notEquals
parameter_list|(
annotation|@
name|Nonnull
name|String
name|property
parameter_list|,
annotation|@
name|Nullable
name|Revision
name|revision
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
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
literal|"Cannot perform notEquals check on new document"
argument_list|)
throw|;
block|}
name|getOrCreateConditions
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
name|revision
argument_list|)
argument_list|,
name|Condition
operator|.
name|newNotEqualsCondition
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increment the value.      *      * @param property the key      * @param value the increment      */
specifier|public
name|void
name|increment
parameter_list|(
annotation|@
name|Nonnull
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
argument_list|(
name|Operation
operator|.
name|Type
operator|.
name|INCREMENT
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
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
name|id
argument_list|,
name|isNew
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Key
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
literal|"key: "
operator|+
name|id
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
decl_stmt|;
if|if
condition|(
name|conditions
operator|!=
literal|null
condition|)
block|{
name|s
operator|+=
literal|" conditions "
operator|+
name|conditions
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|Condition
argument_list|>
name|getOrCreateConditions
parameter_list|()
block|{
if|if
condition|(
name|conditions
operator|==
literal|null
condition|)
block|{
name|conditions
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
return|return
name|conditions
return|;
block|}
specifier|private
name|void
name|internalSet
parameter_list|(
name|String
name|property
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|Document
operator|.
name|ID
operator|.
name|equals
argument_list|(
name|property
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"updateOp.id ("
operator|+
name|id
operator|+
literal|") must not set "
operator|+
name|Document
operator|.
name|ID
argument_list|)
throw|;
block|}
name|Operation
name|op
init|=
operator|new
name|Operation
argument_list|(
name|Operation
operator|.
name|Type
operator|.
name|SET
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|changes
operator|.
name|put
argument_list|(
operator|new
name|Key
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * A DocumentStore operation for a given key within a document.      */
specifier|public
specifier|static
specifier|final
class|class
name|Operation
block|{
comment|/**          * The DocumentStore operation type.          */
specifier|public
enum|enum
name|Type
block|{
comment|/**              * Set the value.              * The sub-key is not used.              */
name|SET
block|,
comment|/**              * Set the value if the new value is higher than the existing value.              * The new value is also considered higher, when there is no              * existing value.              * The sub-key is not used.              */
name|MAX
block|,
comment|/**              * Increment the Long value with the provided Long value.              * The sub-key is not used.              */
name|INCREMENT
block|,
comment|/**              * Add the sub-key / value pair.              * The value in the stored node is a map.              */
name|SET_MAP_ENTRY
block|,
comment|/**              * Remove the sub-key / value pair.              * The value in the stored node is a map.              */
name|REMOVE_MAP_ENTRY
block|}
comment|/**          * The operation type.          */
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
comment|/**          * The value, if any.          */
specifier|public
specifier|final
name|Object
name|value
decl_stmt|;
name|Operation
parameter_list|(
name|Type
name|type
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
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
argument_list|(
name|Type
operator|.
name|INCREMENT
argument_list|,
operator|-
operator|(
name|Long
operator|)
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|SET
case|:
case|case
name|MAX
case|:
case|case
name|REMOVE_MAP_ENTRY
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
argument_list|(
name|Type
operator|.
name|REMOVE_MAP_ENTRY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
name|reverse
return|;
block|}
block|}
comment|/**      * A condition to check before an update is applied.      */
specifier|public
specifier|static
specifier|final
class|class
name|Condition
block|{
comment|/**          * Check if a sub-key exists in a map.          */
specifier|public
specifier|static
specifier|final
name|Condition
name|EXISTS
init|=
operator|new
name|Condition
argument_list|(
name|Type
operator|.
name|EXISTS
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**          * Check if a sub-key is missing in a map.          */
specifier|public
specifier|static
specifier|final
name|Condition
name|MISSING
init|=
operator|new
name|Condition
argument_list|(
name|Type
operator|.
name|EXISTS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|public
enum|enum
name|Type
block|{
comment|/**              * Checks if the sub-key is present in a map or not.              */
name|EXISTS
block|,
comment|/**              * Checks if a map entry equals a given value.              */
name|EQUALS
block|,
comment|/**              * Checks if a map entry does not equal a given value.              */
name|NOTEQUALS
block|}
comment|/**          * The condition type.          */
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
comment|/**          * The value.          */
specifier|public
specifier|final
name|Object
name|value
decl_stmt|;
specifier|private
name|Condition
parameter_list|(
name|Type
name|type
parameter_list|,
name|Object
name|value
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
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**          * Creates a new equals condition with the given value.          *          * @param value the value to compare to.          * @return the equals condition.          */
specifier|public
specifier|static
name|Condition
name|newEqualsCondition
parameter_list|(
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
argument_list|(
name|Type
operator|.
name|EQUALS
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**          * Creates a new notEquals condition with the given value.          *          * @param value the value to compare to.          * @return the notEquals condition.          */
specifier|public
specifier|static
name|Condition
name|newNotEqualsCondition
parameter_list|(
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|Condition
argument_list|(
name|Type
operator|.
name|NOTEQUALS
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|}
comment|/**      * A key for an operation consists of a property name and an optional      * revision. The revision is only set if the value for the operation is      * set for a certain revision.      */
specifier|public
specifier|static
specifier|final
class|class
name|Key
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|revision
decl_stmt|;
specifier|public
name|Key
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Revision
name|revision
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|name
decl_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
condition|)
block|{
name|s
operator|+=
literal|"."
operator|+
name|revision
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|revision
operator|!=
literal|null
condition|)
block|{
name|hash
operator|^=
name|revision
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Key
condition|)
block|{
name|Key
name|other
init|=
operator|(
name|Key
operator|)
name|obj
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
operator|(
name|revision
operator|!=
literal|null
condition|?
name|revision
operator|.
name|equals
argument_list|(
name|other
operator|.
name|revision
argument_list|)
else|:
name|other
operator|.
name|revision
operator|==
literal|null
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

