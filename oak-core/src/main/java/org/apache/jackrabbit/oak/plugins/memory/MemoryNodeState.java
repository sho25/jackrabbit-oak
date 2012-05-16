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
name|memory
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Map
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
name|spi
operator|.
name|state
operator|.
name|AbstractNodeState
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
comment|/**  * Basic in-memory node state implementation.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryNodeState
extends|extends
name|AbstractNodeState
block|{
comment|/**      * Singleton instance of an empty node state, i.e. one with neither      * properties nor child nodes.      */
specifier|public
specifier|static
specifier|final
name|NodeState
name|EMPTY_NODE
init|=
operator|new
name|MemoryNodeState
argument_list|(
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|PropertyState
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|NodeState
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
decl_stmt|;
comment|/**      * Creates a new node state with the given properties and child nodes.      * The given maps are stored as references, so their contents and      * iteration order must remain unmodified at least for as long as this      * node state instance is in use.      *      * @param properties properties      * @param nodes child nodes      */
specifier|public
name|MemoryNodeState
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyState
argument_list|>
name|properties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|nodes
parameter_list|)
block|{
assert|assert
name|Collections
operator|.
name|disjoint
argument_list|(
name|properties
operator|.
name|keySet
argument_list|()
argument_list|,
name|nodes
operator|.
name|keySet
argument_list|()
argument_list|)
assert|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|properties
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|properties
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|properties
operator|.
name|values
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|nodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|nodes
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
return|return
name|MemoryChildNodeEntry
operator|.
name|iterable
argument_list|(
name|nodes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

