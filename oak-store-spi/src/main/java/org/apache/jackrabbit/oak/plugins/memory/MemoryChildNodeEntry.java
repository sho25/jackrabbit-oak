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
name|memory
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
name|checkNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Function
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
name|Iterables
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
name|AbstractChildNodeEntry
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
comment|/**  * Basic JavaBean implementation of a child node entry.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryChildNodeEntry
extends|extends
name|AbstractChildNodeEntry
block|{
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
parameter_list|>
name|Iterable
argument_list|<
name|ChildNodeEntry
argument_list|>
name|iterable
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|set
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|set
argument_list|,
operator|new
name|Function
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
argument_list|,
name|ChildNodeEntry
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ChildNodeEntry
name|apply
parameter_list|(
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|NodeState
argument_list|>
name|entry
parameter_list|)
block|{
return|return
operator|new
name|MemoryChildNodeEntry
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|node
decl_stmt|;
comment|/**      * Creates a child node entry with the given name and referenced      * child node state.      *      * @param name child node name      * @param node child node state      */
specifier|public
name|MemoryChildNodeEntry
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|node
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|checkNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getNodeState
parameter_list|()
block|{
return|return
name|node
return|;
block|}
block|}
end_class

end_unit

