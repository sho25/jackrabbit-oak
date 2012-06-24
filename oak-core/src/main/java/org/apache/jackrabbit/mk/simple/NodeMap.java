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
name|mk
operator|.
name|simple
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|ExceptionFactory
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
name|simple
operator|.
name|NodeImpl
operator|.
name|ChildVisitor
import|;
end_import

begin_class
specifier|public
class|class
name|NodeMap
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MAX_MEMORY_CHILDREN
init|=
literal|"maxMemoryChildren"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESCENDANT_COUNT
init|=
literal|"descendantCount"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESCENDANT_INLINE_COUNT
init|=
literal|"descendantInlineCount"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HASH
init|=
literal|"hash"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NODE_VERSION
init|=
literal|"nodeVersion"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_MEMORY_CHILDREN
init|=
literal|2000
decl_stmt|;
specifier|protected
name|boolean
name|descendantCount
decl_stmt|;
specifier|protected
name|int
name|descendantInlineCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|boolean
name|hash
decl_stmt|;
specifier|protected
name|boolean
name|nodeVersion
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Long
argument_list|,
name|NodeImpl
argument_list|>
name|temp
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|NodeImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|NodeImpl
argument_list|>
name|nodes
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|NodeImpl
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|AtomicLong
name|nextId
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
name|NodeId
name|rootId
decl_stmt|;
specifier|private
name|int
name|maxMemoryChildren
init|=
name|DEFAULT_MAX_MEMORY_CHILDREN
decl_stmt|;
specifier|public
name|NodeId
name|addNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
return|return
name|addNode
argument_list|(
name|node
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|NodeId
name|addNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|boolean
name|allowInline
parameter_list|)
block|{
name|NodeId
name|x
init|=
name|node
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|allowInline
operator|&&
name|node
operator|.
name|getDescendantInlineCount
argument_list|()
operator|<
name|descendantInlineCount
condition|)
block|{
name|x
operator|=
name|NodeId
operator|.
name|getInline
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|x
operator|=
name|NodeId
operator|.
name|get
argument_list|(
name|nextId
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|x
operator|.
name|getLong
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
condition|)
block|{
name|temp
operator|.
name|put
argument_list|(
name|x
operator|.
name|getLong
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|node
operator|.
name|setId
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
specifier|public
name|NodeImpl
name|getNode
parameter_list|(
name|long
name|x
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|get
argument_list|(
name|x
argument_list|)
return|;
block|}
specifier|public
name|void
name|setSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|MAX_MEMORY_CHILDREN
argument_list|)
condition|)
block|{
name|maxMemoryChildren
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DESCENDANT_COUNT
argument_list|)
condition|)
block|{
name|descendantCount
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DESCENDANT_INLINE_COUNT
argument_list|)
condition|)
block|{
name|descendantInlineCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|HASH
argument_list|)
condition|)
block|{
name|hash
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|NODE_VERSION
argument_list|)
condition|)
block|{
name|nodeVersion
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Unknown setting: "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setMaxMemoryChildren
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|maxMemoryChildren
operator|=
name|max
expr_stmt|;
block|}
specifier|public
name|int
name|getMaxMemoryChildren
parameter_list|()
block|{
return|return
name|maxMemoryChildren
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// ignore
block|}
specifier|public
name|NodeId
name|getId
parameter_list|(
name|NodeId
name|id
parameter_list|)
block|{
return|return
name|id
return|;
block|}
specifier|public
name|NodeId
name|commit
parameter_list|(
name|NodeImpl
name|root
parameter_list|)
block|{
name|rootId
operator|=
name|addNode
argument_list|(
name|root
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
condition|)
block|{
specifier|final
name|NodeMap
name|map
init|=
name|this
decl_stmt|;
name|root
operator|.
name|visit
argument_list|(
operator|new
name|ChildVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|NodeId
name|childId
parameter_list|)
block|{
if|if
condition|(
name|childId
operator|.
name|isInline
argument_list|()
condition|)
block|{
name|NodeImpl
name|t
init|=
name|childId
operator|.
name|getNode
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
condition|)
block|{
name|t
operator|.
name|getHash
argument_list|()
expr_stmt|;
block|}
name|t
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NodeImpl
name|t
init|=
name|temp
operator|.
name|get
argument_list|(
name|childId
operator|.
name|getLong
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|hash
condition|)
block|{
name|t
operator|.
name|getHash
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|temp
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|rootId
return|;
block|}
specifier|public
name|NodeId
name|getRootId
parameter_list|()
block|{
return|return
name|rootId
return|;
block|}
specifier|public
name|String
name|formatId
parameter_list|(
name|NodeId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|isInline
argument_list|()
condition|)
block|{
return|return
name|id
operator|.
name|getNode
argument_list|(
name|this
argument_list|)
operator|.
name|asString
argument_list|()
return|;
block|}
return|return
literal|"n"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|id
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|NodeId
name|parseId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
return|return
name|parseInline
argument_list|(
name|id
argument_list|)
return|;
block|}
return|return
name|NodeId
operator|.
name|get
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|id
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|NodeId
name|parseInline
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|NodeImpl
name|inline
init|=
name|NodeImpl
operator|.
name|fromString
argument_list|(
name|this
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|NodeId
name|inlineId
init|=
name|NodeId
operator|.
name|getInline
argument_list|(
name|inline
argument_list|)
decl_stmt|;
name|inline
operator|.
name|setId
argument_list|(
name|inlineId
argument_list|)
expr_stmt|;
return|return
name|inlineId
return|;
block|}
specifier|public
name|boolean
name|isId
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|startsWith
argument_list|(
literal|"n"
argument_list|)
operator|||
name|value
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
return|;
block|}
specifier|public
name|NodeImpl
name|getInfo
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|NodeImpl
name|n
init|=
operator|new
name|NodeImpl
argument_list|(
name|this
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"nodes"
argument_list|,
literal|""
operator|+
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"root"
argument_list|,
literal|""
operator|+
name|rootId
argument_list|)
expr_stmt|;
return|return
name|n
return|;
block|}
specifier|public
name|boolean
name|getDescendantCount
parameter_list|()
block|{
return|return
name|descendantCount
return|;
block|}
specifier|public
name|void
name|setDescendantCount
parameter_list|(
name|boolean
name|descendantCount
parameter_list|)
block|{
name|this
operator|.
name|descendantCount
operator|=
name|descendantCount
expr_stmt|;
block|}
specifier|public
name|void
name|setDescendantInlineCount
parameter_list|(
name|int
name|descendantInlineCount
parameter_list|)
block|{
name|this
operator|.
name|descendantInlineCount
operator|=
name|descendantInlineCount
expr_stmt|;
block|}
specifier|public
name|boolean
name|getHash
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
specifier|public
name|boolean
name|getNodeVersion
parameter_list|()
block|{
return|return
name|nodeVersion
return|;
block|}
block|}
end_class

end_unit

