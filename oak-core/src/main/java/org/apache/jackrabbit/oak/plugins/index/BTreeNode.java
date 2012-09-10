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
name|index
package|;
end_package

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
name|util
operator|.
name|ArrayUtils
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

begin_comment
comment|/**  * An index node page.  */
end_comment

begin_class
specifier|public
class|class
name|BTreeNode
extends|extends
name|BTreePage
implements|implements
name|PropertyIndexConstants
block|{
specifier|private
name|String
index|[]
name|children
decl_stmt|;
specifier|public
name|BTreeNode
parameter_list|(
name|BTree
name|tree
parameter_list|,
name|BTreeNode
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
index|[]
name|keys
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|String
index|[]
name|children
parameter_list|)
block|{
name|super
argument_list|(
name|tree
argument_list|,
name|parent
argument_list|,
name|name
argument_list|,
name|keys
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|verify
argument_list|()
expr_stmt|;
block|}
name|String
name|getNextChildPath
parameter_list|()
block|{
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|children
control|)
block|{
name|int
name|x
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|x
expr_stmt|;
block|}
block|}
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|max
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
name|BTreeLeaf
name|firstLeaf
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getPage
argument_list|(
name|this
argument_list|,
name|children
index|[
literal|0
index|]
argument_list|)
operator|.
name|firstLeaf
argument_list|()
return|;
block|}
annotation|@
name|Override
name|void
name|split
parameter_list|(
name|BTreeNode
name|newParent
parameter_list|,
name|String
name|newName
parameter_list|,
name|int
name|pos
parameter_list|,
name|String
name|siblingName
parameter_list|)
block|{
name|setParent
argument_list|(
name|newParent
argument_list|,
name|newName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|k2
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|keys
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|keys
operator|.
name|length
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|String
index|[]
name|v2
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|values
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|values
operator|.
name|length
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|String
index|[]
name|c2
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|children
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|children
operator|.
name|length
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|BTreeNode
name|n2
init|=
operator|new
name|BTreeNode
argument_list|(
name|tree
argument_list|,
name|parent
argument_list|,
name|siblingName
argument_list|,
name|k2
argument_list|,
name|v2
argument_list|,
name|c2
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|c2
control|)
block|{
name|BTreePage
name|cp
init|=
name|tree
operator|.
name|getPageIfCached
argument_list|(
name|this
argument_list|,
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|cp
operator|!=
literal|null
condition|)
block|{
name|cp
operator|.
name|setParent
argument_list|(
name|n2
argument_list|,
name|c
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|keys
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|keys
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|values
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|children
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|children
argument_list|,
literal|0
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|verify
argument_list|()
expr_stmt|;
name|n2
operator|.
name|writeCreate
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|n2
operator|.
name|children
control|)
block|{
name|tree
operator|.
name|bufferMove
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|INDEX_CONTENT
argument_list|,
name|getPath
argument_list|()
argument_list|,
name|c
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|INDEX_CONTENT
argument_list|,
name|getParentPath
argument_list|()
argument_list|,
name|siblingName
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tree
operator|.
name|moveCache
argument_list|(
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|writeData
argument_list|()
expr_stmt|;
block|}
name|BTreeLeaf
name|next
parameter_list|(
name|BTreePage
name|child
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|childName
init|=
name|child
operator|.
name|name
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|children
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|children
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|childName
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|==
name|children
operator|.
name|length
operator|-
literal|1
condition|)
block|{
return|return
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
operator|.
name|next
argument_list|(
name|this
argument_list|)
return|;
block|}
return|return
name|tree
operator|.
name|getPage
argument_list|(
name|this
argument_list|,
name|children
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
operator|.
name|firstLeaf
argument_list|()
return|;
block|}
name|BTreePage
name|getChild
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|tree
operator|.
name|getPage
argument_list|(
name|this
argument_list|,
name|children
index|[
name|pos
index|]
argument_list|)
return|;
block|}
name|void
name|writeData
parameter_list|()
block|{
name|verify
argument_list|()
expr_stmt|;
name|tree
operator|.
name|modified
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tree
operator|.
name|bufferSetArray
argument_list|(
name|getPath
argument_list|()
argument_list|,
literal|"keys"
argument_list|,
name|keys
argument_list|)
expr_stmt|;
name|tree
operator|.
name|bufferSetArray
argument_list|(
name|getPath
argument_list|()
argument_list|,
literal|"values"
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|tree
operator|.
name|bufferSetArray
argument_list|(
name|getPath
argument_list|()
argument_list|,
literal|"children"
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|writeCreate
parameter_list|()
block|{
name|verify
argument_list|()
expr_stmt|;
name|tree
operator|.
name|modified
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tree
operator|.
name|buffer
argument_list|(
name|getJsop
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|delete
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|tree
operator|.
name|modified
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// empty parent
name|keys
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|keys
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|values
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|children
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|children
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|verify
argument_list|()
expr_stmt|;
block|}
name|void
name|insert
parameter_list|(
name|int
name|pos
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|tree
operator|.
name|modified
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|keys
operator|=
name|ArrayUtils
operator|.
name|arrayInsert
argument_list|(
name|keys
argument_list|,
name|pos
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|values
operator|=
name|ArrayUtils
operator|.
name|arrayInsert
argument_list|(
name|values
argument_list|,
name|pos
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|children
operator|=
name|ArrayUtils
operator|.
name|arrayInsert
argument_list|(
name|children
argument_list|,
name|pos
operator|+
literal|1
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|verify
argument_list|()
expr_stmt|;
block|}
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|children
operator|.
name|length
operator|==
literal|0
return|;
block|}
specifier|private
name|void
name|verify
parameter_list|()
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|!=
name|keys
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of values doesn't match number of keys: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|" "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|keys
argument_list|)
operator|+
literal|" "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|children
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|children
operator|.
name|length
operator|!=
name|keys
operator|.
name|length
operator|+
literal|1
condition|)
block|{
if|if
condition|(
name|children
operator|.
name|length
operator|!=
literal|0
operator|||
name|keys
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of children doesn't match number of keys + 1: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|values
argument_list|)
operator|+
literal|" "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|keys
argument_list|)
operator|+
literal|" "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|children
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|String
name|getJsop
parameter_list|()
block|{
name|JsopBuilder
name|jsop
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|jsop
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|,
name|INDEX_CONTENT
argument_list|,
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|object
argument_list|()
expr_stmt|;
name|jsop
operator|.
name|key
argument_list|(
literal|"keys"
argument_list|)
operator|.
name|array
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|keys
control|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|jsop
operator|.
name|key
argument_list|(
literal|"values"
argument_list|)
operator|.
name|array
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|endArray
argument_list|()
expr_stmt|;
comment|// could just use child node list, but then
comment|// new children need to be ordered at the right position,
comment|// and we would need a way to distinguish empty lists
comment|// from a leaf
name|jsop
operator|.
name|key
argument_list|(
literal|"children"
argument_list|)
operator|.
name|array
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|d
range|:
name|children
control|)
block|{
name|jsop
operator|.
name|value
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|jsop
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|jsop
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|jsop
operator|.
name|newline
argument_list|()
expr_stmt|;
return|return
name|jsop
operator|.
name|toString
argument_list|()
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
literal|"node: "
operator|+
name|getJsop
argument_list|()
return|;
block|}
block|}
end_class

end_unit

