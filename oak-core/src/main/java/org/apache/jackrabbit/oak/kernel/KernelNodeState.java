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
name|oak
operator|.
name|api
operator|.
name|CoreValue
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
name|api
operator|.
name|PropertyState
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryChildNodeEntry
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
name|spi
operator|.
name|state
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
name|oak
operator|.
name|spi
operator|.
name|state
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
name|oak
operator|.
name|spi
operator|.
name|state
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStateDiff
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
name|Iterators
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
name|PagedIterator
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

begin_comment
comment|/**  * Basic {@link NodeState} implementation based on the {@link MicroKernel}  * interface. This class makes an attempt to load data lazily.  */
end_comment

begin_class
specifier|final
class|class
name|KernelNodeState
extends|extends
name|AbstractNodeState
block|{
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
name|String
name|hash
init|=
literal|null
decl_stmt|;
comment|// TODO: WeakReference?
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|childNodes
decl_stmt|;
comment|/**      * Create a new instance of this class representing the node at the      * given {@code path} and {@code revision}. It is an error if the      * underlying Microkernel does not contain such a node.      *      * @param kernel      * @param valueFactory      * @param path      * @param revision      */
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
assert|assert
name|kernel
operator|!=
literal|null
assert|;
assert|assert
name|path
operator|!=
literal|null
assert|;
assert|assert
name|revision
operator|!=
literal|null
assert|;
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
literal|"{\"properties\":[\"*\",\":hash\"]}"
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
name|JsopReader
operator|.
name|NUMBER
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|":hash"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|hash
operator|=
name|reader
operator|.
name|read
argument_list|(
name|JsopReader
operator|.
name|STRING
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
name|PropertyStateImpl
argument_list|(
name|name
argument_list|,
name|CoreValueMapper
operator|.
name|listFromJsopReader
argument_list|(
name|reader
argument_list|,
name|kernel
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CoreValue
name|cv
init|=
name|CoreValueMapper
operator|.
name|fromJsopReader
argument_list|(
name|reader
argument_list|,
name|kernel
argument_list|)
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|PropertyStateImpl
argument_list|(
name|name
argument_list|,
name|cv
argument_list|)
argument_list|)
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
name|JsopReader
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
if|if
condition|(
name|kernel
operator|.
name|nodeExists
argument_list|(
name|childPath
argument_list|,
name|revision
argument_list|)
condition|)
block|{
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
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ChildNodeEntry
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|flatten
argument_list|(
operator|new
name|PagedIterator
argument_list|<
name|ChildNodeEntry
argument_list|>
argument_list|(
name|MAX_CHILD_NODE_NAMES
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getPage
parameter_list|(
name|long
name|pos
parameter_list|,
name|int
name|size
parameter_list|)
block|{
return|return
name|getChildNodeEntries
argument_list|(
name|pos
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Optimised comparison method that can avoid traversing all properties      * and child nodes if both this and the given base node state come from      * the same MicroKernel and either have the same content hash (when      * available) or are located at the same path in different revisions.      *      * @see<a href="https://issues.apache.org/jira/browse/OAK-175">OAK-175</a>      */
annotation|@
name|Override
specifier|public
name|void
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|base
condition|)
block|{
return|return;
comment|// no differences
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|KernelNodeState
condition|)
block|{
name|KernelNodeState
name|kbase
init|=
operator|(
name|KernelNodeState
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|kernel
operator|.
name|equals
argument_list|(
name|kbase
operator|.
name|kernel
argument_list|)
condition|)
block|{
if|if
condition|(
name|revision
operator|.
name|equals
argument_list|(
name|kbase
operator|.
name|revision
argument_list|)
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|kbase
operator|.
name|path
argument_list|)
condition|)
block|{
return|return;
comment|// no differences
block|}
else|else
block|{
name|init
argument_list|()
expr_stmt|;
name|kbase
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|hash
operator|!=
literal|null
operator|&&
name|hash
operator|.
name|equals
argument_list|(
name|kbase
operator|.
name|hash
argument_list|)
condition|)
block|{
return|return;
comment|// no differences
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|kbase
operator|.
name|path
argument_list|)
condition|)
block|{
comment|// TODO: Parse the JSON diff returned by the kernel
comment|// kernel.diff(kbase.revision, revision, path);
block|}
block|}
block|}
block|}
comment|// fall back to the generic node state diff algorithm
name|super
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Object>--
comment|/**      * Optimised equality check that can avoid a full tree comparison if      * both instances come from the same MicroKernel and have either      * the same revision and path or the same content hash (when available).      * Otherwise we fall back to the default tree comparison algorithm.      *      * @see<a href="https://issues.apache.org/jira/browse/OAK-172">OAK-172</a>      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|KernelNodeState
condition|)
block|{
name|KernelNodeState
name|that
init|=
operator|(
name|KernelNodeState
operator|)
name|object
decl_stmt|;
if|if
condition|(
name|kernel
operator|.
name|equals
argument_list|(
name|that
operator|.
name|kernel
argument_list|)
condition|)
block|{
if|if
condition|(
name|revision
operator|.
name|equals
argument_list|(
name|that
operator|.
name|revision
argument_list|)
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|that
operator|.
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|this
operator|.
name|init
argument_list|()
expr_stmt|;
name|that
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|hash
operator|!=
literal|null
operator|&&
name|that
operator|.
name|hash
operator|!=
literal|null
condition|)
block|{
return|return
name|hash
operator|.
name|equals
argument_list|(
name|that
operator|.
name|hash
argument_list|)
return|;
block|}
block|}
block|}
block|}
comment|// fall back to the generic tree equality comparison algorithm
return|return
name|super
operator|.
name|equals
argument_list|(
name|object
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< internal>---
annotation|@
name|Nonnull
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
annotation|@
name|Nonnull
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|Iterator
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
name|boolean
name|all
decl_stmt|;
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
name|all
operator|=
literal|true
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
else|else
block|{
name|all
operator|=
literal|false
expr_stmt|;
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
name|entries
operator|.
name|add
argument_list|(
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|iterator
operator|.
name|next
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
name|all
condition|?
operator|-
literal|1
else|:
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
name|MemoryChildNodeEntry
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
name|JsopReader
operator|.
name|END
argument_list|)
expr_stmt|;
block|}
return|return
name|entries
operator|.
name|iterator
argument_list|()
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
block|}
end_class

end_unit

