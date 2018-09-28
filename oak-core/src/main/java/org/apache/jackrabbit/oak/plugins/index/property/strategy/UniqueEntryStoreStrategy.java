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
operator|.
name|strategy
package|;
end_package

begin_import
import|import static
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
name|IndexConstants
operator|.
name|ENTRY_COUNT_PROPERTY_NAME
import|;
end_import

begin_import
import|import static
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
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|Supplier
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
name|plugins
operator|.
name|memory
operator|.
name|MultiStringPropertyState
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
name|NodeBuilder
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
name|plugins
operator|.
name|index
operator|.
name|counter
operator|.
name|ApproximateCounter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * An IndexStoreStrategy implementation that saves the unique node in a single property.<br>  * This should reduce the number of nodes in the repository, and speed up access.<br>  *<br>  * For example for a node that is under {@code /test/node}, the index  * structure will be {@code /oak:index/index/@key}:  */
end_comment

begin_class
specifier|public
class|class
name|UniqueEntryStoreStrategy
implements|implements
name|IndexStoreStrategy
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UniqueEntryStoreStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Consumer
argument_list|<
name|NodeBuilder
argument_list|>
name|NOOP
init|=
parameter_list|(
name|nb
parameter_list|)
lambda|->
block|{}
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexName
decl_stmt|;
specifier|private
specifier|final
name|Consumer
argument_list|<
name|NodeBuilder
argument_list|>
name|insertCallback
decl_stmt|;
specifier|public
name|UniqueEntryStoreStrategy
parameter_list|()
block|{
name|this
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UniqueEntryStoreStrategy
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|this
argument_list|(
name|indexName
argument_list|,
name|NOOP
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UniqueEntryStoreStrategy
parameter_list|(
name|String
name|indexName
parameter_list|,
annotation|@
name|NotNull
name|Consumer
argument_list|<
name|NodeBuilder
argument_list|>
name|insertCallback
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
name|this
operator|.
name|insertCallback
operator|=
name|insertCallback
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|indexName
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|NodeBuilder
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|beforeKeys
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|afterKeys
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|beforeKeys
control|)
block|{
name|remove
argument_list|(
name|index
operator|.
name|get
argument_list|()
argument_list|,
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|key
range|:
name|afterKeys
control|)
block|{
name|insert
argument_list|(
name|index
operator|.
name|get
argument_list|()
argument_list|,
name|key
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|remove
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ApproximateCounter
operator|.
name|adjustCountSync
argument_list|(
name|index
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// there could be (temporarily) multiple entries
comment|// we need to remove the right one
name|PropertyState
name|s
init|=
name|builder
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|count
argument_list|()
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|r
init|=
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
name|PropertyState
name|s2
init|=
name|MultiStringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|"entry"
argument_list|,
name|list
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|insert
parameter_list|(
name|NodeBuilder
name|index
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ApproximateCounter
operator|.
name|adjustCountSync
argument_list|(
name|index
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|NodeBuilder
name|k
init|=
name|index
operator|.
name|child
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|.
name|hasProperty
argument_list|(
literal|"entry"
argument_list|)
condition|)
block|{
comment|// duplicate key (to detect duplicate entries)
comment|// this is just set temporarily,
comment|// while trying to add a duplicate entry
name|PropertyState
name|s
init|=
name|k
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
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
name|s
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|r
init|=
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|list
operator|.
name|contains
argument_list|(
name|r
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|PropertyState
name|s2
init|=
name|MultiStringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|"entry"
argument_list|,
name|list
argument_list|)
decl_stmt|;
name|k
operator|.
name|setProperty
argument_list|(
name|s2
argument_list|)
expr_stmt|;
name|insertCallback
operator|.
name|accept
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|String
name|indexName
parameter_list|,
specifier|final
name|NodeState
name|indexMeta
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|query0
argument_list|(
name|filter
argument_list|,
name|indexName
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|,
operator|new
name|HitProducer
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|produce
parameter_list|(
name|NodeState
name|indexHit
parameter_list|,
name|String
name|pathName
parameter_list|)
block|{
name|PropertyState
name|s
init|=
name|indexHit
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|count
argument_list|()
operator|<=
literal|1
condition|)
block|{
return|return
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
literal|0
argument_list|)
return|;
block|}
else|else
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|s
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Search for a given set of values, returning {@linkplain IndexEntry} results      *       * @param filter the filter (can optionally be used for optimized query execution)      * @param indexName the name of the index (for logging)      * @param indexMeta the index metadata node (may not be null)      * @param values values to look for (null to check for property existence)      * @return an iterator of index entries      *       * @throws UnsupportedOperationException if the operation is not supported      */
specifier|public
name|Iterable
argument_list|<
name|IndexEntry
argument_list|>
name|queryEntries
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
return|return
name|query0
argument_list|(
name|filter
argument_list|,
name|indexName
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|,
operator|new
name|HitProducer
argument_list|<
name|IndexEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IndexEntry
name|produce
parameter_list|(
name|NodeState
name|indexHit
parameter_list|,
name|String
name|pathName
parameter_list|)
block|{
name|PropertyState
name|s
init|=
name|indexHit
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndexEntry
argument_list|(
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
literal|0
argument_list|)
argument_list|,
name|pathName
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|T
argument_list|>
name|query0
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|indexName
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|HitProducer
argument_list|<
name|T
argument_list|>
name|prod
parameter_list|)
block|{
specifier|final
name|NodeState
name|index
init|=
name|indexMeta
operator|.
name|getChildNode
argument_list|(
name|getIndexNodeName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|it
init|=
name|index
operator|.
name|getChildNodeEntries
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
name|ChildNodeEntry
name|indexEntry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|prod
operator|.
name|produce
argument_list|(
name|indexEntry
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|indexEntry
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|values
control|)
block|{
name|NodeState
name|key
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// we have an entry for this value, so use it
name|list
operator|.
name|add
argument_list|(
name|prod
operator|.
name|produce
argument_list|(
name|key
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|Supplier
argument_list|<
name|NodeBuilder
argument_list|>
name|index
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|index
operator|.
name|get
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|NodeState
name|index
init|=
name|indexMeta
operator|.
name|getChildNode
argument_list|(
name|getIndexNodeName
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|PropertyState
name|ec
init|=
name|indexMeta
operator|.
name|getProperty
argument_list|(
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|ec
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
name|ec
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>=
literal|0
condition|)
block|{
return|return
name|count
return|;
block|}
block|}
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|long
name|approxCount
init|=
name|ApproximateCounter
operator|.
name|getCountSync
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|approxCount
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|approxCount
return|;
block|}
block|}
name|count
operator|=
literal|1
operator|+
name|index
operator|.
name|getChildNodeCount
argument_list|(
name|max
argument_list|)
expr_stmt|;
comment|// "is not null" queries typically read more data
name|count
operator|*=
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|NodeState
name|k
init|=
name|index
operator|.
name|getChildNode
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|.
name|exists
argument_list|()
condition|)
block|{
name|count
operator|=
name|k
operator|.
name|getProperty
argument_list|(
literal|"entry"
argument_list|)
operator|.
name|count
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
name|count
operator|=
name|values
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|count
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|indexMeta
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|values
parameter_list|,
name|int
name|max
parameter_list|)
block|{
return|return
name|count
argument_list|(
name|root
argument_list|,
name|indexMeta
argument_list|,
name|values
argument_list|,
name|max
argument_list|)
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
name|indexName
return|;
block|}
comment|/**      * Creates a specific type of "hit" to return from the query methods      *       *<p>Use primarily to reduce duplication when the query algorithms execute mostly the same steps but return different objects.</p>      *       * @param<T> The type of Hit to produce      */
specifier|private
interface|interface
name|HitProducer
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**          * Invoked when a matching index entry is found           *           * @param indexHit the index node          * @param propertyValue the value of the property          * @return the value produced for the specific "hit"           */
name|T
name|produce
parameter_list|(
name|NodeState
name|indexHit
parameter_list|,
name|String
name|propertyValue
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

