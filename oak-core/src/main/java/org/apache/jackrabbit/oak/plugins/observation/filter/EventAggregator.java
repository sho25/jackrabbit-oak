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
name|observation
operator|.
name|filter
package|;
end_package

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
comment|/**  * An EventAggregator can be provided via a FilterProvider  * and is then used to 'aggregate' an event at creation time  * (ie after filtering).  *<p>  * Aggregation in this context means to have the event identifier  * not be the path (as usual) but one of its parents.  * This allows to have client code use an aggregating filter  * and ignore the event paths but only inspect the event  * identifier which is then the aggregation parent node.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EventAggregator
block|{
comment|/**      * Aggregates a property change      * @return 0 or negative for no aggregation, positive indicating      * how many levels to aggregate upwards the tree.      */
name|int
name|aggregate
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|parents
parameter_list|,
name|PropertyState
name|propertyState
parameter_list|)
function_decl|;
comment|/**      * Aggregates a node change      * @return 0 or negative for no aggregation, positive indicating      * how many levels to aggregate upwards the tree.      */
name|int
name|aggregate
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|List
argument_list|<
name|ChildNodeEntry
argument_list|>
name|parents
parameter_list|,
name|ChildNodeEntry
name|childNodeState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

