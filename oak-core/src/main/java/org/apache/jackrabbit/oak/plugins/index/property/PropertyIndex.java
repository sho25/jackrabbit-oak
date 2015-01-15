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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|TYPE_PROPERTY_NAME
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
name|HashSet
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

begin_comment
comment|/**  * Provides a QueryIndex that does lookups against a property index  *  *<p>  * To define a property index on a subtree you have to add an<code>oak:index</code> node.  *<br>  * Next (as a child node) follows the index definition node that:  *<ul>  *<li>must be of type<code>oak:QueryIndexDefinition</code></li>  *<li>must have the<code>type</code> property set to<b><code>property</code></b></li>  *<li>contains the<code>propertyNames</code> property that indicates what property will be stored in the index</li>  *</ul>  *</p>  *<p>  * Optionally you can specify  *<ul>   *<li> a uniqueness constraint on a property index by setting the<code>unique</code> flag to<code>true</code></li>  *<li> that the property index only applies to a certain node type by setting the<code>declaringNodeTypes</code> property</li>  *</ul>  *</p>  *<p>  * Notes:  *<ul>  *<li><code>propertyNames</code> can be a list of properties, and it is optional.in case it is missing, the node name will be used as a property name reference value</li>  *<li><code>reindex</code> is a property that when set to<code>true</code>, triggers a full content reindex.</li>  *</ul>  *</p>  *   *<pre>  *<code>  * {  *     NodeBuilder index = root.child("oak:index");  *     index.child("uuid")  *         .setProperty("jcr:primaryType", "oak:QueryIndexDefinition", Type.NAME)  *         .setProperty("type", "property")  *         .setProperty("propertyNames", "jcr:uuid")  *         .setProperty("declaringNodeTypes", "mix:referenceable")  *         .setProperty("unique", true)  *         .setProperty("reindex", true);  * }  *</code>  *</pre>  *   * @see QueryIndex  * @see PropertyIndexLookup  */
end_comment

begin_class
class|class
name|PropertyIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|static
specifier|final
name|String
name|PROPERTY
init|=
literal|"property"
decl_stmt|;
comment|// TODO the max string length should be removed, or made configurable
specifier|private
specifier|static
specifier|final
name|int
name|MAX_STRING_LENGTH
init|=
literal|100
decl_stmt|;
comment|/**      * name used when the indexed value is an empty string      */
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_TOKEN
init|=
literal|":"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertyIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|encode
parameter_list|(
name|PropertyValue
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|HashSet
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
if|if
condition|(
name|v
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|v
operator|=
name|EMPTY_TOKEN
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
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
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|v
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
specifier|private
name|PropertyIndexPlan
name|plan
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|PropertyIndexPlan
name|bestPlan
init|=
literal|null
decl_stmt|;
comment|// TODO support indexes on a path
comment|// currently, only indexes on the root node are supported
name|NodeState
name|state
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
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
name|definition
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|PROPERTY
operator|.
name|equals
argument_list|(
name|definition
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
operator|&&
name|definition
operator|.
name|hasChildNode
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
condition|)
block|{
name|PropertyIndexPlan
name|plan
init|=
operator|new
name|PropertyIndexPlan
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|root
argument_list|,
name|definition
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|plan
operator|.
name|getCost
argument_list|()
operator|!=
name|Double
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"property cost for {} is {}"
argument_list|,
name|plan
operator|.
name|getName
argument_list|()
argument_list|,
name|plan
operator|.
name|getCost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|bestPlan
operator|==
literal|null
operator|||
name|plan
operator|.
name|getCost
argument_list|()
operator|<
name|bestPlan
operator|.
name|getCost
argument_list|()
condition|)
block|{
name|bestPlan
operator|=
name|plan
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|bestPlan
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
name|PROPERTY
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
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// not an appropriate index for full-text search
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
if|if
condition|(
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
condition|)
block|{
comment|// not an appropriate index for native search
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
name|PropertyIndexPlan
name|plan
init|=
name|plan
argument_list|(
name|root
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
return|return
name|plan
operator|.
name|getCost
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
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
name|PropertyIndexPlan
name|plan
init|=
name|plan
argument_list|(
name|root
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|plan
operator|!=
literal|null
argument_list|,
literal|"Property index is used even when no index"
operator|+
literal|" is available for filter "
operator|+
name|filter
argument_list|)
expr_stmt|;
return|return
name|plan
operator|.
name|execute
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
name|root
parameter_list|)
block|{
name|PropertyIndexPlan
name|plan
init|=
name|plan
argument_list|(
name|root
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
return|return
name|plan
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|"property index not applicable"
return|;
block|}
block|}
block|}
end_class

end_unit

