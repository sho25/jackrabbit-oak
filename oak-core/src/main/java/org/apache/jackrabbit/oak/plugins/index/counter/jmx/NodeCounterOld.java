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
name|plugins
operator|.
name|index
operator|.
name|counter
operator|.
name|jmx
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|commons
operator|.
name|jmx
operator|.
name|AnnotatedStandardMBean
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
name|IndexConstants
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
name|NodeCounterEditor
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
name|NodeStore
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

begin_comment
comment|/**  * A mechanism to retrieve node counter data.  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|NodeCounterOld
extends|extends
name|AnnotatedStandardMBean
implements|implements
name|NodeCounterMBean
block|{
comment|/**      * Approximate count using the hashed name (deterministically, so that after      * adding a removing all nodes the count goes back to zero).      */
specifier|public
specifier|final
specifier|static
name|boolean
name|COUNT_HASH
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.countHashed"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|public
name|NodeCounterOld
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|NodeCounterMBean
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeState
name|child
parameter_list|(
name|NodeState
name|n
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
name|child
argument_list|(
name|n
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|child
parameter_list|(
name|NodeState
name|n
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|p
range|:
name|path
control|)
block|{
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|p
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|getChildNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getEstimatedNodeCount
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|getEstimatedNodeCount
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Get the estimated number of nodes for a given path.      *      * @param root the root      * @param path the path      * @param max whether to get the maximum expected number of nodes (the      *            stored value plus the resolution)      * @return -1 if unknown, 0 if the node does not exist (or, if max is false,      *         if there are probably not many descendant nodes), or the      *         (maximum) estimated number of descendant nodes      */
specifier|public
specifier|static
name|long
name|getEstimatedNodeCount
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|max
parameter_list|)
block|{
comment|// check if there is a property in the node itself
comment|// (for property index nodes)
name|NodeState
name|s
init|=
name|child
argument_list|(
name|root
argument_list|,
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// node not found
return|return
literal|0
return|;
block|}
if|if
condition|(
operator|!
name|max
condition|)
block|{
name|long
name|syncCount
init|=
name|ApproximateCounter
operator|.
name|getCountSync
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|syncCount
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|syncCount
return|;
block|}
block|}
if|if
condition|(
name|COUNT_HASH
condition|)
block|{
return|return
name|getCombinedCount
argument_list|(
name|root
argument_list|,
name|path
argument_list|,
name|s
argument_list|,
name|max
argument_list|)
return|;
block|}
return|return
name|getEstimatedNodeCountOld
argument_list|(
name|root
argument_list|,
name|s
argument_list|,
name|path
argument_list|,
name|max
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|long
name|getEstimatedNodeCountOld
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|NodeState
name|s
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|max
parameter_list|)
block|{
comment|// old code from here
name|PropertyState
name|p
init|=
name|s
operator|.
name|getProperty
argument_list|(
name|NodeCounterEditor
operator|.
name|COUNT_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|long
name|x
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
if|if
condition|(
name|max
condition|)
block|{
comment|// in the node itself, we just add the resolution
name|x
operator|+=
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
comment|// check in the counter index (if it exists)
name|s
operator|=
name|child
argument_list|(
name|root
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"counter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// no index
return|return
operator|-
literal|1
return|;
block|}
name|s
operator|=
name|child
argument_list|(
name|s
argument_list|,
name|NodeCounterEditor
operator|.
name|DATA_NODE_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// no index data (not yet indexed, or very few nodes)
return|return
operator|-
literal|1
return|;
block|}
name|s
operator|=
name|child
argument_list|(
name|s
argument_list|,
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// we have an index, but no data
name|long
name|x
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|max
condition|)
block|{
comment|// in the index, the resolution is lower
name|x
operator|+=
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
operator|*
literal|20
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
name|p
operator|=
name|s
operator|.
name|getProperty
argument_list|(
name|NodeCounterEditor
operator|.
name|COUNT_PROPERTY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
comment|// we have an index, but no data
name|long
name|x
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|max
condition|)
block|{
comment|// in the index, the resolution is lower
name|x
operator|+=
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
operator|*
literal|20
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
name|long
name|x
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
if|if
condition|(
name|max
condition|)
block|{
name|x
operator|+=
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
expr_stmt|;
block|}
return|return
name|x
return|;
block|}
specifier|private
specifier|static
name|long
name|getCombinedCount
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|path
parameter_list|,
name|NodeState
name|s
parameter_list|,
name|boolean
name|max
parameter_list|)
block|{
name|Long
name|value
init|=
name|getCombinedCountIfAvailable
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
operator|+
operator|(
name|max
condition|?
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
else|:
literal|0
operator|)
return|;
block|}
comment|// check in the counter index (if it exists)
name|s
operator|=
name|child
argument_list|(
name|root
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
literal|"counter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// no index
return|return
operator|-
literal|1
return|;
block|}
name|s
operator|=
name|child
argument_list|(
name|s
argument_list|,
name|NodeCounterEditor
operator|.
name|DATA_NODE_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// no index data (not yet indexed, or very few nodes)
return|return
operator|-
literal|1
return|;
block|}
name|s
operator|=
name|child
argument_list|(
name|s
argument_list|,
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
name|value
operator|=
name|getCombinedCountIfAvailable
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
operator|+
operator|(
name|max
condition|?
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
else|:
literal|0
operator|)
return|;
block|}
block|}
comment|// we have an index, but no data
return|return
name|max
condition|?
name|ApproximateCounter
operator|.
name|COUNT_RESOLUTION
operator|*
literal|20
else|:
literal|0
return|;
block|}
specifier|private
specifier|static
name|Long
name|getCombinedCountIfAvailable
parameter_list|(
name|NodeState
name|s
parameter_list|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|long
name|x
init|=
literal|0
decl_stmt|;
name|PropertyState
name|p
init|=
name|s
operator|.
name|getProperty
argument_list|(
name|NodeCounterEditor
operator|.
name|COUNT_HASH_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|x
operator|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
name|p
operator|=
name|s
operator|.
name|getProperty
argument_list|(
name|NodeCounterEditor
operator|.
name|COUNT_PROPERTY_NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|x
operator|+=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
return|return
name|found
condition|?
name|x
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getEstimatedChildNodeCounts
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|collectCounts
argument_list|(
name|buff
argument_list|,
name|path
argument_list|,
name|level
argument_list|)
expr_stmt|;
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|collectCounts
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|long
name|count
init|=
name|getEstimatedNodeCount
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|buff
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|",\n"
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|level
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
name|NodeState
name|s
init|=
name|child
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
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
name|ChildNodeEntry
name|c
range|:
name|s
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|cn
range|:
name|names
control|)
block|{
name|s
operator|.
name|getChildNode
argument_list|(
name|cn
argument_list|)
expr_stmt|;
name|String
name|child
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|cn
argument_list|)
decl_stmt|;
name|collectCounts
argument_list|(
name|buff
argument_list|,
name|child
argument_list|,
name|level
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

