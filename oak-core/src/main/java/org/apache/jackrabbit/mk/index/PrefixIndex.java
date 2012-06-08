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
name|index
package|;
end_package

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
name|simple
operator|.
name|NodeImpl
import|;
end_import

begin_comment
comment|/**  * An index for all values with a given prefix.  */
end_comment

begin_class
specifier|public
class|class
name|PrefixIndex
implements|implements
name|Index
block|{
specifier|private
specifier|final
name|Indexer
name|indexer
decl_stmt|;
specifier|private
specifier|final
name|BTree
name|tree
decl_stmt|;
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
specifier|public
name|PrefixIndex
parameter_list|(
name|Indexer
name|indexer
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|tree
operator|=
operator|new
name|BTree
argument_list|(
name|indexer
argument_list|,
name|Indexer
operator|.
name|TYPE_PREFIX
operator|+
name|prefix
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setMinSize
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|PrefixIndex
name|fromNodeName
parameter_list|(
name|Indexer
name|indexer
parameter_list|,
name|String
name|nodeName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|startsWith
argument_list|(
name|Indexer
operator|.
name|TYPE_PREFIX
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|prefix
init|=
name|nodeName
operator|.
name|substring
argument_list|(
name|Indexer
operator|.
name|TYPE_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrefixIndex
argument_list|(
name|indexer
argument_list|,
name|prefix
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexNodeName
parameter_list|()
block|{
return|return
name|tree
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addOrRemoveNode
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|boolean
name|add
parameter_list|)
block|{
name|String
name|nodePath
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|node
operator|.
name|getPropertyCount
argument_list|()
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|propertyName
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|node
operator|.
name|getPropertyValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|addOrRemoveProperty
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|,
name|value
argument_list|,
name|add
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addOrRemoveProperty
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|add
parameter_list|)
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|String
name|v
init|=
name|t
operator|.
name|getToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|addOrRemove
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|,
name|v
argument_list|,
name|add
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'['
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|t
operator|.
name|matches
argument_list|(
literal|']'
argument_list|)
condition|)
block|{
do|do
block|{
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|String
name|v
init|=
name|t
operator|.
name|getToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|addOrRemove
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|,
name|v
argument_list|,
name|add
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|FALSE
argument_list|)
condition|)
block|{
comment|// ignore
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|TRUE
argument_list|)
condition|)
block|{
comment|// ignore
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|NULL
argument_list|)
condition|)
block|{
comment|// ignore
block|}
elseif|else
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
name|JsopReader
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
comment|// ignore
block|}
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|t
operator|.
name|read
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addOrRemove
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|add
parameter_list|)
block|{
name|String
name|v
init|=
name|value
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
name|tree
operator|.
name|add
argument_list|(
name|v
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tree
operator|.
name|remove
argument_list|(
name|v
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Get an iterator over the paths for the given property value.      *      * @param value the value (including the prefix)      * @param revision the revision      * @return an iterator of the paths (an empty iterator if not found)      * @throws IllegalArgumentException if the value doesn't start with the prefix      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|revision
parameter_list|)
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The value doesn't start with \""
operator|+
name|prefix
operator|+
literal|"\": "
operator|+
name|value
argument_list|)
throw|;
block|}
name|String
name|v
init|=
name|value
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|indexer
operator|.
name|updateUntil
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
name|v
argument_list|)
decl_stmt|;
return|return
operator|new
name|Cursor
operator|.
name|RangeIterator
argument_list|(
name|c
argument_list|,
name|v
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUnique
parameter_list|()
block|{
return|return
name|tree
operator|.
name|isUnique
argument_list|()
return|;
block|}
block|}
end_class

end_unit

