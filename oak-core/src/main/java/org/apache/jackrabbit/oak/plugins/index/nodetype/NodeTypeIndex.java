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
name|nodetype
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
name|INDEX_NAME_OPTION
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
name|JcrConstants
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
name|mount
operator|.
name|MountInfoProvider
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
name|index
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * {@code NodeTypeIndex} implements a {@link QueryIndex} using  * {@link org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexLookup}s  * on {@code jcr:primaryType} and {@code jcr:mixinTypes} to evaluate a node type  * restriction on {@link Filter}. The cost for this index is the sum of the costs  * of the {@link org.apache.jackrabbit.oak.plugins.index.property.PropertyIndexLookup}  * for queries on {@code jcr:primaryType} and {@code jcr:mixinTypes}.  */
end_comment

begin_class
class|class
name|NodeTypeIndex
implements|implements
name|QueryIndex
implements|,
name|JcrConstants
block|{
specifier|private
specifier|final
name|MountInfoProvider
name|mountInfoProvider
decl_stmt|;
specifier|public
name|NodeTypeIndex
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getMinimumCost
parameter_list|()
block|{
return|return
name|NodeTypeIndexLookup
operator|.
name|MINIMUM_COST
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
comment|// TODO don't call getCost for such queries
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
if|if
condition|(
operator|!
name|hasNodeTypeRestriction
argument_list|(
name|filter
argument_list|)
condition|)
block|{
comment|// this is not an appropriate index if the filter
comment|// doesn't have a node type restriction
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
name|PropertyRestriction
name|indexName
init|=
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|INDEX_NAME_OPTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|wrongIndexName
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
name|NodeTypeIndexLookup
name|lookup
init|=
operator|new
name|NodeTypeIndexLookup
argument_list|(
name|root
argument_list|,
name|mountInfoProvider
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookup
operator|.
name|isIndexed
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|,
name|filter
argument_list|)
condition|)
block|{
return|return
name|lookup
operator|.
name|getCost
argument_list|(
name|filter
argument_list|)
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
specifier|private
specifier|static
name|boolean
name|wrongIndexName
parameter_list|(
name|PropertyRestriction
name|indexName
parameter_list|)
block|{
if|if
condition|(
name|indexName
operator|==
literal|null
operator|||
name|indexName
operator|.
name|first
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
literal|"nodetype"
operator|.
name|equals
argument_list|(
name|indexName
operator|.
name|first
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
name|NodeTypeIndexLookup
name|lookup
init|=
operator|new
name|NodeTypeIndexLookup
argument_list|(
name|root
argument_list|,
name|mountInfoProvider
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasNodeTypeRestriction
argument_list|(
name|filter
argument_list|)
operator|||
operator|!
name|lookup
operator|.
name|isIndexed
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|,
name|filter
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NodeType index is used even when no index is available for filter "
operator|+
name|filter
argument_list|)
throw|;
block|}
return|return
name|Cursors
operator|.
name|newPathCursorDistinct
argument_list|(
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|)
argument_list|,
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
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
return|return
literal|"nodeType "
operator|+
name|filter
operator|.
name|toString
argument_list|()
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
literal|"nodeType"
return|;
block|}
comment|//----------------------------< internal>----------------------------------
specifier|private
specifier|static
name|boolean
name|hasNodeTypeRestriction
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
operator|!
name|filter
operator|.
name|matchesAllTypes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

