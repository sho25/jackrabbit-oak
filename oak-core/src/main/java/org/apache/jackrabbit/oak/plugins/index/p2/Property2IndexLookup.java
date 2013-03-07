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
name|p2
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
name|DECLARING_NODE_TYPES
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
name|INDEX_DEFINITIONS_NAME
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
name|PROPERTY_NAMES
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
name|TYPE_PROPERTY_NAME
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|plugins
operator|.
name|index
operator|.
name|p2
operator|.
name|strategy
operator|.
name|ContentMirrorStoreStrategy
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
name|p2
operator|.
name|strategy
operator|.
name|IndexStoreStrategy
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
name|NodeState
import|;
end_import

begin_comment
comment|/**  * Is responsible for querying the property index content.  *   *<p>  * This class can be used directly on a subtree where there is an index defined  * by supplying a {@link NodeState} root.  *</p>  *   *<pre>  *<code>  * {  *     NodeState state = ... // get a node state  *     Property2IndexLookup lookup = new Property2IndexLookup(state);  *     Set<String> hits = lookup.find("foo", PropertyValues.newString("xyz"));  * }  *</code>  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|Property2IndexLookup
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MAX_COST
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|IndexStoreStrategy
name|store
init|=
operator|new
name|ContentMirrorStoreStrategy
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|public
name|Property2IndexLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
comment|/**      * Checks whether the named property is indexed somewhere along the given      * path. Lookup starts at the current path (at the root of this object) and      * traverses down the path.      *       * @param propertyName property name      * @param path lookup path      * @return true if the property is indexed      */
specifier|public
name|boolean
name|isIndexed
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|path
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|getIndexDataNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
operator|!=
literal|null
return|;
block|}
name|NodeState
name|node
init|=
name|root
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|getIndexDataNode
argument_list|(
name|node
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|getIndexDataNode
argument_list|(
name|root
argument_list|,
name|propertyName
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No index for "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|Property2Index
operator|.
name|encode
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|state
argument_list|,
name|values
argument_list|)
return|;
block|}
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|name
parameter_list|,
name|PropertyValue
name|value
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|getIndexDataNode
argument_list|(
name|root
argument_list|,
name|name
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
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
name|List
argument_list|<
name|String
argument_list|>
name|it
init|=
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|Property2Index
operator|.
name|encode
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|count
argument_list|(
name|state
argument_list|,
name|it
argument_list|,
name|MAX_COST
argument_list|)
return|;
block|}
comment|/**      * Get the node with the index data for the given property, if there is an      * applicable index with data.      *       * @param propertyName the property name      * @param filter for the node type restriction      * @return the node where the index data is stored, or null if no index      *         definition or index data node was found      */
annotation|@
name|Nullable
specifier|private
specifier|static
name|NodeState
name|getIndexDataNode
parameter_list|(
name|NodeState
name|node
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|node
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|filterNodeType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|filterNodeType
operator|=
name|filter
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
comment|//keep a fallback to a matching index def that has *no* node type constraints
name|NodeState
name|fallback
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|ns
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|type
init|=
name|ns
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isArray
argument_list|()
operator|||
operator|!
name|Property2Index
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|containsValue
argument_list|(
name|ns
operator|.
name|getProperty
argument_list|(
name|PROPERTY_NAMES
argument_list|)
argument_list|,
name|propertyName
argument_list|)
condition|)
block|{
if|if
condition|(
name|filterNodeType
operator|==
literal|null
operator|||
name|containsValue
argument_list|(
name|ns
operator|.
name|getProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
argument_list|,
name|filterNodeType
argument_list|)
condition|)
block|{
return|return
name|ns
operator|.
name|getChildNode
argument_list|(
literal|":index"
argument_list|)
return|;
block|}
if|if
condition|(
name|ns
operator|.
name|getProperty
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
operator|==
literal|null
condition|)
block|{
name|fallback
operator|=
name|ns
operator|.
name|getChildNode
argument_list|(
literal|":index"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|fallback
return|;
block|}
specifier|private
specifier|static
name|boolean
name|containsValue
parameter_list|(
name|PropertyState
name|values
parameter_list|,
name|String
name|lookup
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|values
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|v
range|:
name|values
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
if|if
condition|(
name|lookup
operator|.
name|equals
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
return|return
name|lookup
operator|.
name|equals
argument_list|(
name|values
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

