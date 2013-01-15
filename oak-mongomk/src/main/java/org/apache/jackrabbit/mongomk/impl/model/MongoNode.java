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
operator|.
name|impl
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|json
operator|.
name|JsopBuilder
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
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_comment
comment|/**  * The {@code MongoDB} representation of a node.  */
end_comment

begin_class
specifier|public
class|class
name|MongoNode
extends|extends
name|BasicDBObject
block|{
specifier|public
specifier|static
specifier|final
name|String
name|KEY_CHILDREN
init|=
literal|"children"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_DELETED
init|=
literal|"deleted"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PATH
init|=
literal|"path"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PROPERTIES
init|=
literal|"props"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_REVISION_ID
init|=
literal|"revId"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_BRANCH_ID
init|=
literal|"branchId"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3153393934945155106L
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|addedChildren
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|addedProps
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|removedChildren
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|removedProps
decl_stmt|;
specifier|public
specifier|static
name|NodeImpl
name|toNode
parameter_list|(
name|MongoNode
name|nodeMongo
parameter_list|)
block|{
name|String
name|path
init|=
name|nodeMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|NodeImpl
name|nodeImpl
init|=
operator|new
name|NodeImpl
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|nodeMongo
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|childNames
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
name|String
name|childPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|childName
argument_list|)
decl_stmt|;
name|NodeImpl
name|child
init|=
operator|new
name|NodeImpl
argument_list|(
name|childPath
argument_list|)
decl_stmt|;
name|nodeImpl
operator|.
name|addChildNodeEntry
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|nodeImpl
operator|.
name|setRevisionId
argument_list|(
name|nodeMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|nodeMongo
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|nodeImpl
operator|.
name|addProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertObjectValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeImpl
return|;
block|}
specifier|private
specifier|static
name|String
name|convertObjectValue
parameter_list|(
name|Object
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
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|JsopBuilder
operator|.
name|encode
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//
comment|// These properties are persisted to MongoDB
comment|//
comment|//--------------------------------------------------------------------------
specifier|public
name|String
name|getBranchId
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|KEY_BRANCH_ID
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBranchId
parameter_list|(
name|String
name|branchId
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_BRANCH_ID
argument_list|,
name|branchId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|get
argument_list|(
name|KEY_CHILDREN
argument_list|)
return|;
block|}
specifier|public
name|void
name|setChildren
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|children
parameter_list|)
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
name|put
argument_list|(
name|KEY_CHILDREN
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeField
argument_list|(
name|KEY_CHILDREN
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
name|KEY_DELETED
argument_list|)
return|;
block|}
specifier|public
name|void
name|setDeleted
parameter_list|(
name|boolean
name|deleted
parameter_list|)
block|{
if|if
condition|(
name|deleted
condition|)
block|{
name|put
argument_list|(
name|KEY_DELETED
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remove
argument_list|(
name|KEY_DELETED
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|getString
argument_list|(
name|KEY_PATH
argument_list|)
return|;
block|}
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_PATH
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getProperties
parameter_list|()
block|{
name|Object
name|properties
init|=
name|get
argument_list|(
name|KEY_PROPERTIES
argument_list|)
decl_stmt|;
return|return
name|properties
operator|!=
literal|null
condition|?
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|properties
else|:
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
return|;
block|}
specifier|public
name|void
name|setProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|!=
literal|null
operator|&&
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|put
argument_list|(
name|KEY_PROPERTIES
argument_list|,
name|properties
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeField
argument_list|(
name|KEY_PROPERTIES
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Long
name|getRevisionId
parameter_list|()
block|{
return|return
name|getLong
argument_list|(
name|KEY_REVISION_ID
argument_list|)
return|;
block|}
specifier|public
name|void
name|setRevisionId
parameter_list|(
name|long
name|revisionId
parameter_list|)
block|{
name|put
argument_list|(
name|KEY_REVISION_ID
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MongoNode
name|copy
parameter_list|()
block|{
name|MongoNode
name|copy
init|=
operator|new
name|MongoNode
argument_list|()
decl_stmt|;
name|copy
operator|.
name|putAll
argument_list|(
operator|(
name|Map
operator|)
name|super
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//
comment|// These properties are used to keep track of changes but not persisted
comment|//
comment|//--------------------------------------------------------------------------
specifier|public
name|void
name|addChild
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|removedChildren
operator|!=
literal|null
operator|&&
name|removedChildren
operator|.
name|remove
argument_list|(
name|childName
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|addedChildren
operator|==
literal|null
condition|)
block|{
name|addedChildren
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|addedChildren
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|addedChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAddedChildren
parameter_list|()
block|{
return|return
name|addedChildren
return|;
block|}
specifier|public
name|void
name|removeChild
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
if|if
condition|(
name|addedChildren
operator|!=
literal|null
operator|&&
name|addedChildren
operator|.
name|remove
argument_list|(
name|childName
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|removedChildren
operator|==
literal|null
condition|)
block|{
name|removedChildren
operator|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|removedChildren
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
condition|)
block|{
name|removedChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getRemovedChildren
parameter_list|()
block|{
return|return
name|removedChildren
return|;
block|}
specifier|public
name|void
name|addProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|addedProps
operator|==
literal|null
condition|)
block|{
name|addedProps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|addedProps
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getAddedProps
parameter_list|()
block|{
return|return
name|addedProps
return|;
block|}
specifier|public
name|void
name|removeProp
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|removedProps
operator|==
literal|null
condition|)
block|{
name|removedProps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|removedProps
operator|.
name|put
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getRemovedProps
parameter_list|()
block|{
return|return
name|removedProps
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|//
comment|// Other methods
comment|//
comment|//--------------------------------------------------------------------------
specifier|public
name|boolean
name|childExists
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
operator|!
name|children
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|children
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
operator|&&
operator|!
name|childExistsInRemovedChildren
argument_list|(
name|childName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|childExistsInAddedChildren
argument_list|(
name|childName
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|childExistsInAddedChildren
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
return|return
name|addedChildren
operator|!=
literal|null
operator|&&
operator|!
name|addedChildren
operator|.
name|isEmpty
argument_list|()
condition|?
name|addedChildren
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
else|:
literal|false
return|;
block|}
specifier|private
name|boolean
name|childExistsInRemovedChildren
parameter_list|(
name|String
name|childName
parameter_list|)
block|{
return|return
name|removedChildren
operator|!=
literal|null
operator|&&
operator|!
name|removedChildren
operator|.
name|isEmpty
argument_list|()
condition|?
name|removedChildren
operator|.
name|contains
argument_list|(
name|childName
argument_list|)
else|:
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|addedChildren
operator|!=
literal|null
operator|&&
operator|!
name|addedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", addedChildren : "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|addedChildren
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|removedChildren
operator|!=
literal|null
operator|&&
operator|!
name|removedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", removedChildren : "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|removedChildren
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|addedProps
operator|!=
literal|null
operator|&&
operator|!
name|addedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", addedProps : "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|addedProps
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|removedProps
operator|!=
literal|null
operator|&&
operator|!
name|removedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", removedProps : "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|removedProps
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|hasPendingChanges
parameter_list|()
block|{
if|if
condition|(
name|addedChildren
operator|!=
literal|null
operator|&&
operator|!
name|addedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|removedChildren
operator|!=
literal|null
operator|&&
operator|!
name|removedChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|addedProps
operator|!=
literal|null
operator|&&
operator|!
name|addedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|removedProps
operator|!=
literal|null
operator|&&
operator|!
name|removedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

