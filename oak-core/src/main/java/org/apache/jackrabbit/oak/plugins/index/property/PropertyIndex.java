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
name|property
package|;
end_package

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
name|net
operator|.
name|URLEncoder
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
name|Collection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|PropertyValue
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
name|Type
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
name|query
operator|.
name|index
operator|.
name|IndexRowImpl
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
name|query
operator|.
name|index
operator|.
name|TraversingCursor
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
name|query
operator|.
name|Cursor
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|Filter
operator|.
name|PropertyRestriction
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
name|query
operator|.
name|IndexRow
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
name|query
operator|.
name|QueryIndex
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|Sets
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MAX_STRING_LENGTH
init|=
literal|100
decl_stmt|;
comment|// TODO: configurable
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|encode
parameter_list|(
name|PropertyValue
name|value
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|value
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
try|try
block|{
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>
name|MAX_STRING_LENGTH
condition|)
block|{
name|v
operator|=
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|MAX_STRING_LENGTH
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|v
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"UTF-8 is unsupported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|values
return|;
block|}
comment|//--------------------------------------------------------< QueryIndex>--
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"oak:index"
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
literal|1.0
return|;
comment|// FIXME: proper cost calculation
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
literal|null
decl_stmt|;
name|PropertyIndexLookup
name|lookup
init|=
operator|new
name|PropertyIndexLookup
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyRestriction
name|pr
range|:
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|firstIncluding
operator|&&
name|pr
operator|.
name|lastIncluding
operator|&&
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
comment|// TODO: range queries
operator|&&
name|lookup
operator|.
name|isIndexed
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
literal|"/"
argument_list|)
condition|)
block|{
comment|// TODO: path
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
name|lookup
operator|.
name|find
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
name|pr
operator|.
name|first
argument_list|)
decl_stmt|;
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
name|paths
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|paths
operator|.
name|retainAll
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|paths
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|PathCursor
argument_list|(
name|paths
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TraversingCursor
argument_list|(
name|filter
argument_list|,
name|root
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
return|return
literal|"oak:index"
return|;
comment|// TODO: better plans
block|}
specifier|private
specifier|static
class|class
name|PathCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|public
name|PathCursor
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|paths
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|path
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|path
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|currentRow
parameter_list|()
block|{
comment|// TODO support jcr:score and possibly rep:exceprt
return|return
operator|new
name|IndexRowImpl
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

