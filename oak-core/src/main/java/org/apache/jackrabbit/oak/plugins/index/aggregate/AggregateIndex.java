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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|aggregate
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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|FilterImpl
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
name|Cursors
operator|.
name|AbstractCursor
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
operator|.
name|FulltextQueryIndex
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
name|collect
operator|.
name|ImmutableSet
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
name|Iterators
import|;
end_import

begin_comment
comment|/**  * A virtual full-text that can aggregate nodes based on aggregate definitions.  * Internally, it uses another full-text index.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateIndex
implements|implements
name|FulltextQueryIndex
block|{
specifier|private
specifier|final
name|FulltextQueryIndex
name|baseIndex
decl_stmt|;
specifier|public
name|AggregateIndex
parameter_list|(
name|FulltextQueryIndex
name|baseIndex
parameter_list|)
block|{
name|this
operator|.
name|baseIndex
operator|=
name|baseIndex
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
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
if|if
condition|(
name|baseIndex
operator|==
literal|null
condition|)
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
return|return
name|baseIndex
operator|.
name|getCost
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
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
name|NodeState
name|rootState
parameter_list|)
block|{
comment|// pass-through impl
if|if
condition|(
name|baseIndex
operator|.
name|getNodeAggregator
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|baseIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|rootState
argument_list|)
return|;
block|}
return|return
operator|new
name|AggregationCursor
argument_list|(
name|baseIndex
operator|.
name|query
argument_list|(
name|newAggregationFilter
argument_list|(
name|filter
argument_list|)
argument_list|,
name|rootState
argument_list|)
argument_list|,
name|baseIndex
operator|.
name|getNodeAggregator
argument_list|()
argument_list|,
name|rootState
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Filter
name|newAggregationFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
name|filter
argument_list|)
decl_stmt|;
comment|// disables node type checks for now
name|f
operator|.
name|setMatchesAllTypes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|f
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
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
if|if
condition|(
name|baseIndex
operator|==
literal|null
condition|)
block|{
return|return
literal|"aggregate no-index"
return|;
block|}
return|return
literal|"aggregate "
operator|+
name|baseIndex
operator|.
name|getPlan
argument_list|(
name|filter
argument_list|,
name|rootState
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
if|if
condition|(
name|baseIndex
operator|==
literal|null
condition|)
block|{
return|return
literal|"aggregate no-index"
return|;
block|}
return|return
literal|"aggregate "
operator|+
name|baseIndex
operator|.
name|getIndexName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
block|{
return|return
name|baseIndex
operator|.
name|getNodeAggregator
argument_list|()
return|;
block|}
comment|// ----- aggregation aware cursor
specifier|private
specifier|static
class|class
name|AggregationCursor
extends|extends
name|AbstractCursor
block|{
specifier|private
specifier|final
name|Cursor
name|cursor
decl_stmt|;
specifier|private
specifier|final
name|NodeAggregator
name|aggregator
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|rootState
decl_stmt|;
specifier|private
name|boolean
name|init
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|aggregates
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|item
init|=
literal|null
decl_stmt|;
specifier|public
name|AggregationCursor
parameter_list|(
name|Cursor
name|cursor
parameter_list|,
name|NodeAggregator
name|aggregator
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
name|this
operator|.
name|cursor
operator|=
name|cursor
expr_stmt|;
name|this
operator|.
name|aggregator
operator|=
name|aggregator
expr_stmt|;
name|this
operator|.
name|rootState
operator|=
name|rootState
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
operator|&&
operator|!
name|init
condition|)
block|{
name|fetchNext
argument_list|()
expr_stmt|;
name|init
operator|=
literal|true
expr_stmt|;
block|}
return|return
operator|!
name|closed
return|;
block|}
specifier|private
name|void
name|fetchNext
parameter_list|()
block|{
if|if
condition|(
name|aggregates
operator|!=
literal|null
operator|&&
name|aggregates
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|item
operator|=
name|aggregates
operator|.
name|next
argument_list|()
expr_stmt|;
name|init
operator|=
literal|true
expr_stmt|;
return|return;
block|}
name|aggregates
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|cursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|IndexRow
name|row
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|row
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|aggregates
operator|=
name|Iterators
operator|.
name|concat
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|path
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|aggregator
operator|.
name|getParents
argument_list|(
name|rootState
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|fetchNext
argument_list|()
expr_stmt|;
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|init
operator|=
literal|false
expr_stmt|;
return|return
operator|new
name|IndexRowImpl
argument_list|(
name|item
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

