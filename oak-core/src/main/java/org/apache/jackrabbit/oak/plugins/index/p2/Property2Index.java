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

begin_comment
comment|/**  * Provides a QueryIndex that does lookups against a property index  *   *<p>  * To define a property index on a subtree you have to add an<code>oak:index</code> node.  *   * Under it follows the index definition node that:  *<ul>  *<li>must be of type<code>oak:queryIndexDefinition</code></li>  *<li>must have the<code>type</code> property set to<b><code>p2</code></b></li>  *<li>contains the<code>propertyNames</code> property that indicates what property will be stored in the index</li>  *</ul>  *</p>  *<p>  * Optionally you can specify the uniqueness constraint on a property index by  * setting the<code>unique</code> flag to<code>true</code>.  *</p>  *   *<p>  * Note:<code>propertyNames</code> can be a list of properties, and it is optional.in case it is missing, the node name will be used as a property name reference value  *</p>  *   *<p>  * Note:<code>reindex</code> is a property that when set to<code>true</code>, triggers a full content reindex.  *</p>  *   *<pre>  *<code>  * {  *     NodeBuilder index = root.child("oak:index");  *     index.child("uuid")  *         .setProperty("jcr:primaryType", "oak:queryIndexDefinition", Type.NAME)  *         .setProperty("type", "p2")  *         .setProperty("propertyNames", "jcr:uuid")  *         .setProperty("unique", true)  *         .setProperty("reindex", true);  * }  *</code>  *</pre>  *   * @see QueryIndex  * @see Property2IndexLookup  */
end_comment

begin_class
class|class
name|Property2Index
implements|implements
name|QueryIndex
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"p2"
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
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
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
comment|// TODO support indexes on a path
comment|// currently, only indexes on the root node are supported
if|if
condition|(
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
operator|!=
literal|null
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
condition|)
block|{
comment|// "[property] = $value"
return|return
name|lookup
operator|.
name|getCost
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
name|pr
operator|.
name|first
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|==
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
condition|)
block|{
comment|// "[property] is not null"
return|return
name|lookup
operator|.
name|getCost
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
comment|// not an appropriate index
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
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
name|Property2IndexLookup
name|lookup
init|=
operator|new
name|Property2IndexLookup
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
comment|// TODO support indexes on a path
comment|// currently, only indexes on the root node are supported
if|if
condition|(
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
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
literal|null
decl_stmt|;
comment|// equality
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
operator|!=
literal|null
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
condition|)
block|{
comment|// "[property] = $value"
comment|// TODO don't load all entries in memory
name|set
operator|=
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
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|==
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
condition|)
block|{
comment|// "[property] is not null"
comment|// TODO don't load all entries in memory
name|set
operator|=
name|lookup
operator|.
name|find
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
operator|(
name|PropertyValue
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// only keep the intersection
comment|// TODO this requires all paths are loaded in memory
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Property index is used even when no index is available for filter "
operator|+
name|filter
argument_list|)
throw|;
block|}
return|return
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|paths
argument_list|)
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
comment|// TODO the index should return better query plans
return|return
literal|"oak:index"
return|;
block|}
block|}
end_class

end_unit

