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
operator|.
name|tree
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

begin_comment
comment|/**  * JSOP Diff Builder  */
end_comment

begin_class
specifier|public
class|class
name|DiffBuilder
block|{
specifier|private
specifier|final
name|NodeState
name|before
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|after
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathFilter
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|public
name|DiffBuilder
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|String
name|path
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|String
name|pathFilter
parameter_list|)
block|{
name|this
operator|.
name|before
operator|=
name|before
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|pathFilter
operator|=
operator|(
name|pathFilter
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|pathFilter
argument_list|)
operator|)
condition|?
literal|"/"
else|:
name|pathFilter
expr_stmt|;
block|}
specifier|public
name|String
name|build
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
comment|// maps (key: id of target node, value: path/to/target)
comment|// for tracking added/removed nodes; this allows us
comment|// to detect 'move' operations
specifier|final
name|HashMap
argument_list|<
name|NodeState
argument_list|,
name|String
argument_list|>
name|addedNodes
init|=
operator|new
name|HashMap
argument_list|<
name|NodeState
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|NodeState
argument_list|,
name|String
argument_list|>
name|removedNodes
init|=
operator|new
name|HashMap
argument_list|<
name|NodeState
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|path
argument_list|,
name|pathFilter
argument_list|)
operator|&&
operator|!
name|path
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
return|return
literal|""
return|;
block|}
if|if
condition|(
name|before
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|after
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|path
argument_list|)
operator|.
name|object
argument_list|()
expr_stmt|;
name|toJson
argument_list|(
name|buff
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
comment|// path doesn't exist in the specified revisions
return|return
literal|""
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|value
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|newline
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
name|TraversingNodeDiffHandler
name|diffHandler
init|=
operator|new
name|TraversingNodeDiffHandler
argument_list|(
name|store
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|after
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|after
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
comment|// since property and node deletions can't be distinguished
comment|// using the "-<path>" notation we're representing
comment|// property deletions as "^<path>:null"
name|buff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|addedNodes
operator|.
name|put
argument_list|(
name|after
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|object
argument_list|()
expr_stmt|;
name|toJson
argument_list|(
name|buff
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|buff
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|removedNodes
operator|.
name|put
argument_list|(
name|before
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|buff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|value
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|buff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|p
argument_list|,
name|pathFilter
argument_list|)
operator|||
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|super
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|diffHandler
operator|.
name|start
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// check if this commit includes 'move' operations
comment|// by building intersection of added and removed nodes
name|addedNodes
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|removedNodes
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|addedNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// this commit includes 'move' operations
name|removedNodes
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|addedNodes
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
comment|// addedNodes& removedNodes now only contain information about moved nodes
comment|// re-build the diff in a 2nd pass, this time representing moves correctly
name|buff
operator|.
name|resetWriter
argument_list|()
expr_stmt|;
comment|// TODO refactor code, avoid duplication
name|diffHandler
operator|=
operator|new
name|TraversingNodeDiffHandler
argument_list|(
name|store
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|after
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|after
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|after
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|before
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
comment|// since property and node deletions can't be distinguished
comment|// using the "-<path>" notation we're representing
comment|// property deletions as "^<path>:null"
name|buff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|addedNodes
operator|.
name|containsKey
argument_list|(
name|after
argument_list|)
condition|)
block|{
comment|// moved node, will be processed separately
return|return;
block|}
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|p
argument_list|)
operator|.
name|object
argument_list|()
expr_stmt|;
name|toJson
argument_list|(
name|buff
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|buff
operator|.
name|endObject
argument_list|()
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|addedNodes
operator|.
name|containsKey
argument_list|(
name|before
argument_list|)
condition|)
block|{
comment|// moved node, will be processed separately
return|return;
block|}
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buff
operator|.
name|value
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|buff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|String
name|p
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|getCurrentPath
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|p
argument_list|,
name|pathFilter
argument_list|)
operator|||
name|p
operator|.
name|startsWith
argument_list|(
name|pathFilter
argument_list|)
condition|)
block|{
name|super
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|diffHandler
operator|.
name|start
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// finally process moved nodes
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|NodeState
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|addedNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
operator|.
comment|// path/to/deleted/node
name|key
argument_list|(
name|removedNodes
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
comment|// path/to/added/node
name|value
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|toJson
parameter_list|(
name|JsopBuilder
name|builder
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
for|for
control|(
name|PropertyState
name|property
range|:
name|node
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|builder
operator|.
name|key
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|encodedValue
argument_list|(
name|property
operator|.
name|getEncodedValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ChildNode
name|entry
range|:
name|node
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|builder
operator|.
name|key
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|object
argument_list|()
expr_stmt|;
name|toJson
argument_list|(
name|builder
argument_list|,
name|entry
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

