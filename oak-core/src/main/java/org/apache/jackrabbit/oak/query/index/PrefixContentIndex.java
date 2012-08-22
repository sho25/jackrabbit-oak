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
name|query
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|kernel
operator|.
name|CoreValueMapper
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
name|PrefixIndex
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

begin_comment
comment|/**  * An index that stores the index data in a {@code MicroKernel}.  */
end_comment

begin_class
specifier|public
class|class
name|PrefixContentIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|final
name|PrefixIndex
name|index
decl_stmt|;
specifier|public
name|PrefixContentIndex
parameter_list|(
name|PrefixIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
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
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
name|restriction
operator|==
literal|null
condition|)
block|{
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
block|}
if|if
condition|(
name|restriction
operator|.
name|first
operator|!=
name|restriction
operator|.
name|last
condition|)
block|{
comment|// only support equality matches (for now)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
block|}
if|if
condition|(
name|restriction
operator|.
name|propertyType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
block|}
name|String
name|hint
init|=
name|CoreValueMapper
operator|.
name|getHintForType
argument_list|(
name|restriction
operator|.
name|propertyType
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
name|hint
operator|+
literal|":"
decl_stmt|;
if|if
condition|(
operator|!
name|prefix
operator|.
name|equals
argument_list|(
name|index
operator|.
name|getPrefix
argument_list|()
argument_list|)
condition|)
block|{
comment|// wrong prefix (wrong property type)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
block|}
return|return
literal|100
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
name|restriction
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No restriction for *"
argument_list|)
throw|;
block|}
comment|// TODO need to use correct json representation
name|String
name|v
init|=
name|restriction
operator|.
name|first
operator|.
name|getString
argument_list|()
decl_stmt|;
name|v
operator|=
name|index
operator|.
name|getPrefix
argument_list|()
operator|+
name|v
expr_stmt|;
return|return
literal|"prefixIndex \""
operator|+
name|v
operator|+
literal|'"'
return|;
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
name|String
name|revisionId
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
name|restriction
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No restriction for *"
argument_list|)
throw|;
block|}
comment|// TODO need to use correct json representation
name|String
name|v
init|=
name|restriction
operator|.
name|first
operator|.
name|getString
argument_list|()
decl_stmt|;
name|v
operator|=
name|index
operator|.
name|getPrefix
argument_list|()
operator|+
name|v
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|index
operator|.
name|getPaths
argument_list|(
name|v
argument_list|,
name|revisionId
argument_list|)
decl_stmt|;
return|return
operator|new
name|ContentCursor
argument_list|(
name|it
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|index
operator|.
name|getDefinition
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**      * The cursor to for this index.      */
specifier|static
class|class
name|ContentCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
decl_stmt|;
specifier|private
name|String
name|currentPath
decl_stmt|;
specifier|public
name|ContentCursor
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
name|this
operator|.
name|it
operator|=
name|it
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|currentRow
parameter_list|()
block|{
return|return
operator|new
name|IndexRowImpl
argument_list|(
name|currentPath
argument_list|)
return|;
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
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|pathAndProperty
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|currentPath
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|pathAndProperty
argument_list|)
expr_stmt|;
return|return
literal|true
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

