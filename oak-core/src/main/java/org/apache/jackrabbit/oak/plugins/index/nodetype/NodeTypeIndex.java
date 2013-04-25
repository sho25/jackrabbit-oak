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
name|plugins
operator|.
name|index
operator|.
name|p2
operator|.
name|Property2IndexLookup
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
comment|/**  *<code>NodeTypeIndex</code> implements a {@link QueryIndex} using  * {@link Property2IndexLookup}s on<code>jcr:primaryType</code> and  *<code>jcr:mixinTypes</code> to evaluate a node type restriction on  * {@link Filter}. The cost for this index is the sum of the costs of the  * {@link Property2IndexLookup} for queries on<code>jcr:primaryType</code> and  *<code>jcr:mixinTypes</code>.  */
end_comment

begin_class
class|class
name|NodeTypeIndex
implements|implements
name|QueryIndex
implements|,
name|JcrConstants
block|{
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
name|NodeTypeIndexLookup
name|lookup
init|=
operator|new
name|NodeTypeIndexLookup
argument_list|(
name|root
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

