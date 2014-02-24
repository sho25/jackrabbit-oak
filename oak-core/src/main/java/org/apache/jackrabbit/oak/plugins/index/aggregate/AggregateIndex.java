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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|NoSuchElementException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextAnd
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
name|fulltext
operator|.
name|FullTextExpression
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
name|fulltext
operator|.
name|FullTextOr
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
name|fulltext
operator|.
name|FullTextTerm
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
name|fulltext
operator|.
name|FullTextVisitor
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
name|base
operator|.
name|Predicates
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
operator|-
literal|0.05
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
comment|// TODO OAK-828
comment|// FullTextExpression constraint = filter.getFullTextConstraint();
comment|// constraint = getFlatConstraint(constraint);
comment|// f.setFullTextConstraint(constraint);
return|return
name|f
return|;
block|}
specifier|static
name|FullTextExpression
name|getFlatConstraint
parameter_list|(
name|FullTextExpression
name|constraint
parameter_list|)
block|{
if|if
condition|(
name|constraint
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|AtomicReference
argument_list|<
name|FullTextExpression
argument_list|>
name|result
init|=
operator|new
name|AtomicReference
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
name|constraint
operator|.
name|accept
argument_list|(
operator|new
name|FullTextVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextTerm
name|term
parameter_list|)
block|{
name|String
name|p
init|=
name|term
operator|.
name|getPropertyName
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|p
argument_list|)
operator|>
literal|1
condition|)
block|{
comment|// remove indirection
name|String
name|name
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|term
operator|=
operator|new
name|FullTextTerm
argument_list|(
name|name
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|set
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextAnd
name|and
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FullTextExpression
name|e
range|:
name|and
operator|.
name|list
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getFlatConstraint
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|set
argument_list|(
operator|new
name|FullTextAnd
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextOr
name|or
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FullTextExpression
name|e
range|:
name|or
operator|.
name|list
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getFlatConstraint
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|set
argument_list|(
operator|new
name|FullTextOr
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|get
argument_list|()
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
comment|/**      * An aggregation aware cursor.      */
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
comment|/**          * current item of the cursor          */
specifier|private
name|String
name|item
decl_stmt|;
comment|/**          * all of the item's known aggregates          */
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|aggregates
decl_stmt|;
comment|/**          * should enforce uniqueness of the aggregated paths          */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|seenPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|filter
argument_list|(
name|Iterators
operator|.
name|concat
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|path
argument_list|)
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
argument_list|,
name|Predicates
operator|.
name|not
argument_list|(
name|Predicates
operator|.
name|in
argument_list|(
name|seenPaths
argument_list|)
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
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|seenPaths
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
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

