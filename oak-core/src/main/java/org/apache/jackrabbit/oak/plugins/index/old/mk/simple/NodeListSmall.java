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
operator|.
name|old
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|mk
operator|.
name|json
operator|.
name|JsopWriter
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
name|index
operator|.
name|old
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|old
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
name|index
operator|.
name|old
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
name|mk
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * A list of child nodes that fits in memory.  */
end_comment

begin_class
specifier|public
class|class
name|NodeListSmall
implements|implements
name|NodeList
block|{
comment|/**      * The number of (direct) child nodes.      */
name|int
name|size
decl_stmt|;
comment|/**      * The child node names, in order they were added.      */
name|String
index|[]
name|names
decl_stmt|;
comment|/**      * The child node ids.      */
specifier|private
name|NodeId
index|[]
name|children
decl_stmt|;
comment|/**      * The sort index.      */
specifier|private
name|int
index|[]
name|sort
decl_stmt|;
comment|/**      * The index of the last child node name lookup (to speed up name lookups).      */
specifier|private
name|int
name|lastNameIndexCache
decl_stmt|;
name|NodeListSmall
parameter_list|()
block|{
name|this
argument_list|(
name|ArrayUtils
operator|.
name|EMPTY_STRING_ARRAY
argument_list|,
name|NodeId
operator|.
name|EMPTY_ARRAY
argument_list|,
name|ArrayUtils
operator|.
name|EMPTY_INTEGER_ARRAY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeListSmall
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|NodeId
index|[]
name|children
parameter_list|,
name|int
index|[]
name|sort
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|name
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|private
name|int
name|find
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// copy, to avoid concurrency issues
name|int
name|last
init|=
name|lastNameIndexCache
decl_stmt|;
if|if
condition|(
name|last
operator|<
name|size
operator|&&
name|names
index|[
name|sort
index|[
name|last
index|]
index|]
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|last
return|;
block|}
name|int
name|min
init|=
literal|0
decl_stmt|,
name|max
init|=
name|size
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|min
operator|<=
name|max
condition|)
block|{
name|int
name|test
init|=
operator|(
name|min
operator|+
name|max
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|compare
init|=
name|names
index|[
name|sort
index|[
name|test
index|]
index|]
operator|.
name|compareTo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
name|lastNameIndexCache
operator|=
name|test
expr_stmt|;
return|return
name|test
return|;
block|}
if|if
condition|(
name|compare
operator|>
literal|0
condition|)
block|{
name|max
operator|=
name|test
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
name|min
operator|=
name|test
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|// not found: return negative insertion point
return|return
operator|-
operator|(
name|min
operator|+
literal|1
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeId
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|index
init|=
name|find
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|children
index|[
name|sort
index|[
name|index
index|]
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeId
name|x
parameter_list|)
block|{
name|int
name|index
init|=
name|find
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Node already exists: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|index
operator|=
operator|-
name|index
operator|-
literal|1
expr_stmt|;
name|name
operator|=
name|StringCache
operator|.
name|cache
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|names
operator|=
name|ArrayUtils
operator|.
name|arrayInsert
argument_list|(
name|names
argument_list|,
name|size
argument_list|,
name|name
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
name|size
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|sort
operator|=
name|ArrayUtils
operator|.
name|arrayInsert
argument_list|(
name|sort
argument_list|,
name|index
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|replace
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeId
name|x
parameter_list|)
block|{
name|int
name|index
init|=
name|find
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Node not found: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|children
operator|=
name|ArrayUtils
operator|.
name|arrayReplace
argument_list|(
name|children
argument_list|,
name|sort
index|[
name|index
index|]
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>=
name|names
operator|.
name|length
condition|?
literal|null
else|:
name|names
index|[
operator|(
name|int
operator|)
name|pos
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|int
name|maxCount
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
name|int
name|pos
init|=
operator|(
name|int
operator|)
name|offset
decl_stmt|;
name|int
name|remaining
init|=
name|maxCount
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
argument_list|<
name|size
operator|&&
name|remaining
argument_list|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|remaining
operator|--
expr_stmt|;
return|return
name|names
index|[
name|pos
operator|++
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeId
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|index
init|=
name|find
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
name|ExceptionFactory
operator|.
name|get
argument_list|(
literal|"Node not found: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|int
name|s
init|=
name|sort
index|[
name|index
index|]
decl_stmt|;
name|NodeId
name|result
init|=
name|children
index|[
name|s
index|]
decl_stmt|;
name|names
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|names
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|children
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|children
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|sort
operator|=
name|ArrayUtils
operator|.
name|arrayRemove
argument_list|(
name|sort
argument_list|,
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
name|size
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sort
index|[
name|i
index|]
operator|>=
name|s
condition|)
block|{
name|sort
index|[
name|i
index|]
operator|--
expr_stmt|;
block|}
block|}
block|}
name|size
operator|--
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|JsopWriter
name|json
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|json
operator|.
name|object
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|json
operator|.
name|key
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
operator|.
name|value
argument_list|(
name|children
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|json
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeList
name|createClone
parameter_list|(
name|NodeMap
name|map
parameter_list|,
name|long
name|revId
parameter_list|)
block|{
name|NodeList
name|result
init|=
operator|new
name|NodeListSmall
argument_list|(
name|names
argument_list|,
name|children
argument_list|,
name|sort
argument_list|,
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|map
operator|.
name|getMaxMemoryChildren
argument_list|()
condition|)
block|{
return|return
operator|new
name|NodeListTrie
argument_list|(
name|map
argument_list|,
name|result
argument_list|,
name|size
argument_list|,
name|revId
argument_list|)
return|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|ChildVisitor
name|v
parameter_list|)
block|{
for|for
control|(
name|NodeId
name|c
range|:
name|children
control|)
block|{
name|v
operator|.
name|accept
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|append
parameter_list|(
name|JsopWriter
name|json
parameter_list|,
name|NodeMap
name|map
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|json
operator|.
name|key
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|NodeId
name|x
init|=
name|children
index|[
name|i
index|]
decl_stmt|;
name|NodeId
name|y
init|=
name|map
operator|.
name|getId
argument_list|(
name|x
argument_list|)
decl_stmt|;
if|if
condition|(
name|x
operator|!=
name|y
condition|)
block|{
name|children
index|[
name|i
index|]
operator|=
name|y
expr_stmt|;
block|}
name|json
operator|.
name|encodedValue
argument_list|(
name|map
operator|.
name|formatId
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
name|int
name|memory
init|=
literal|100
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|memory
operator|+=
name|names
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|*
literal|2
operator|+
literal|8
expr_stmt|;
block|}
return|return
name|memory
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|names
argument_list|)
operator|^
name|Arrays
operator|.
name|hashCode
argument_list|(
name|children
argument_list|)
operator|^
name|Arrays
operator|.
name|hashCode
argument_list|(
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|NodeListSmall
condition|)
block|{
name|NodeListSmall
name|o
init|=
operator|(
name|NodeListSmall
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|size
operator|==
name|o
operator|.
name|size
condition|)
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|sort
argument_list|,
name|o
operator|.
name|sort
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|children
argument_list|,
name|o
operator|.
name|children
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|names
argument_list|,
name|o
operator|.
name|names
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateHash
parameter_list|(
name|NodeMap
name|map
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
try|try
block|{
for|for
control|(
name|int
name|s
range|:
name|sort
control|)
block|{
name|String
name|n
init|=
name|names
index|[
name|s
index|]
decl_stmt|;
name|IOUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|NodeId
name|c
init|=
name|children
index|[
name|s
index|]
decl_stmt|;
name|byte
index|[]
name|hash
init|=
name|c
operator|.
name|getHash
argument_list|()
decl_stmt|;
if|if
condition|(
name|hash
operator|==
literal|null
condition|)
block|{
name|hash
operator|=
name|map
operator|.
name|getNode
argument_list|(
name|c
operator|.
name|getLong
argument_list|()
argument_list|)
operator|.
name|getHash
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|hash
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

