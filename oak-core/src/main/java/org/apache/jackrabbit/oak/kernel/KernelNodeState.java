begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|kernel
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
name|api
operator|.
name|MicroKernel
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
name|api
operator|.
name|MicroKernelException
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
name|JsopReader
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
name|JsopTokenizer
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
name|model
operator|.
name|AbstractNodeState
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
name|model
operator|.
name|ChildNodeEntry
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
name|model
operator|.
name|NodeState
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
name|model
operator|.
name|PropertyState
import|;
end_import

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  * Basic {@link NodeState} implementation based on the {@link MicroKernel}  * interface. This class makes an attempt to load data lazily.  */
end_comment

begin_class
specifier|public
class|class
name|KernelNodeState
extends|extends
name|AbstractNodeState
block|{
comment|// fixme make package private
comment|/**      * Maximum number of child nodes kept in memory.      */
specifier|static
specifier|final
name|int
name|MAX_CHILD_NODE_NAMES
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|kernel
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
decl_stmt|;
specifier|private
name|long
name|childNodeCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|childNodes
decl_stmt|;
comment|// TODO: WeakReference?
specifier|public
name|KernelNodeState
parameter_list|(
name|MicroKernel
name|kernel
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|revision
parameter_list|)
block|{
name|this
operator|.
name|kernel
operator|=
name|kernel
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|String
name|json
init|=
name|kernel
operator|.
name|getNodes
argument_list|(
name|path
argument_list|,
name|revision
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|MAX_CHILD_NODE_NAMES
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|properties
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
argument_list|()
expr_stmt|;
name|childNodes
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
argument_list|()
expr_stmt|;
do|do
block|{
name|String
name|name
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
literal|":childNodeCount"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|childNodeCount
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|reader
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|String
name|childPath
init|=
name|path
operator|+
literal|'/'
operator|+
name|name
decl_stmt|;
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|childPath
operator|=
literal|'/'
operator|+
name|name
expr_stmt|;
block|}
name|childNodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|childPath
argument_list|,
name|revision
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
name|reader
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
literal|'"'
operator|+
name|reader
operator|.
name|getToken
argument_list|()
operator|+
literal|'"'
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|TRUE
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|FALSE
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|KernelPropertyState
argument_list|(
name|name
argument_list|,
name|readArray
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token: "
operator|+
name|reader
operator|.
name|getToken
argument_list|()
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|END
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|properties
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|properties
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|childNodeCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|NodeState
name|child
init|=
name|childNodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
operator|&&
name|childNodeCount
operator|>
name|MAX_CHILD_NODE_NAMES
condition|)
block|{
name|String
name|childPath
init|=
name|getChildPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|kernel
operator|.
name|getNodes
argument_list|(
name|childPath
argument_list|,
name|revision
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|child
operator|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|childPath
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
comment|// FIXME: Better way to determine whether a child node exists
block|}
block|}
return|return
name|child
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|(
name|long
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|==
operator|-
literal|1
condition|)
block|{
name|count
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
if|if
condition|(
name|childNodeCount
operator|>
name|count
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Too many child nodes"
argument_list|)
throw|;
block|}
block|}
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|offset
operator|<
name|childNodes
operator|.
name|size
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
argument_list|>
name|iterator
init|=
name|childNodes
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|offset
operator|--
expr_stmt|;
block|}
while|while
condition|(
name|count
operator|>
literal|0
operator|&&
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|KernelChildNodeEntry
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
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
block|}
name|offset
operator|=
name|childNodes
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|>
literal|0
operator|&&
name|childNodeCount
operator|>
name|MAX_CHILD_NODE_NAMES
condition|)
block|{
name|String
name|json
init|=
name|kernel
operator|.
name|getNodes
argument_list|(
name|path
argument_list|,
name|revision
argument_list|,
literal|0
argument_list|,
name|offset
argument_list|,
name|count
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
do|do
block|{
name|String
name|name
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|String
name|childPath
init|=
name|getChildPath
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|NodeState
name|child
init|=
operator|new
name|KernelNodeState
argument_list|(
name|kernel
argument_list|,
name|childPath
argument_list|,
name|revision
argument_list|)
decl_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|KernelChildNodeEntry
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reader
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|JsopTokenizer
operator|.
name|END
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
return|;
block|}
specifier|private
name|String
name|getChildPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|'/'
operator|+
name|name
return|;
block|}
else|else
block|{
return|return
name|path
operator|+
literal|'/'
operator|+
name|name
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|readArray
parameter_list|(
name|JsopReader
name|reader
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
name|String
name|sep
init|=
literal|""
decl_stmt|;
while|while
condition|(
operator|!
name|reader
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
name|String
name|v
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|v
operator|=
name|reader
operator|.
name|getToken
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|v
operator|=
literal|'"'
operator|+
name|reader
operator|.
name|getToken
argument_list|()
operator|+
literal|'"'
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|TRUE
argument_list|)
condition|)
block|{
name|v
operator|=
literal|"true"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reader
operator|.
name|matches
argument_list|(
name|JsopTokenizer
operator|.
name|FALSE
argument_list|)
condition|)
block|{
name|v
operator|=
literal|"false"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token: "
operator|+
name|reader
operator|.
name|getToken
argument_list|()
argument_list|)
throw|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
name|sep
operator|=
literal|","
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

