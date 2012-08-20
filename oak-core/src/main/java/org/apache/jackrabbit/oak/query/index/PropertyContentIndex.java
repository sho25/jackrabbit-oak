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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|index
operator|.
name|PropertyIndex
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
name|PropertyContentIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|final
name|PropertyIndex
name|index
decl_stmt|;
specifier|public
name|PropertyContentIndex
parameter_list|(
name|PropertyIndex
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
name|String
name|propertyName
init|=
name|index
operator|.
name|getPropertyName
argument_list|()
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
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
name|boolean
name|unique
init|=
name|index
operator|.
name|isUnique
argument_list|()
decl_stmt|;
return|return
name|unique
condition|?
literal|2
else|:
literal|20
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
name|String
name|propertyName
init|=
name|index
operator|.
name|getPropertyName
argument_list|()
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
return|return
literal|"propertyIndex \""
operator|+
name|restriction
operator|.
name|propertyName
operator|+
literal|" "
operator|+
name|restriction
operator|.
name|toString
argument_list|()
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
name|String
name|propertyName
init|=
name|index
operator|.
name|getPropertyName
argument_list|()
decl_stmt|;
name|Filter
operator|.
name|PropertyRestriction
name|restriction
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
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
literal|"No restriction for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
name|CoreValue
name|first
init|=
name|restriction
operator|.
name|first
decl_stmt|;
name|String
name|f
init|=
name|first
operator|==
literal|null
condition|?
literal|null
else|:
name|first
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|f
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
name|getIndexNodeName
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
name|currentPath
operator|=
name|it
operator|.
name|next
argument_list|()
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

