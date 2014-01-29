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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|MISSING_NODE
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|Predicate
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
name|NodeState
import|;
end_import

begin_comment
comment|/**  * An universal {@code Filter} implementation, which can be parametrised by  * a {@link Selector} and a {@code Predicate}. The selector maps a call back  * on this filter to a {@code NodeState}. That node state is in turn passed  * to the predicate for determining whether to include or to exclude the  * respective event.  */
end_comment

begin_class
specifier|public
class|class
name|UniversalFilter
implements|implements
name|EventFilter
block|{
specifier|private
specifier|final
name|NodeState
name|beforeState
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|afterState
decl_stmt|;
specifier|private
specifier|final
name|Selector
name|selector
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|predicate
decl_stmt|;
comment|/**      * Create a new instance of an universal filter rooted at the passed trees.      *      * @param before          before state      * @param after           after state      * @param selector        selector for selecting the tree to match the predicate against      * @param predicate       predicate for determining whether to include or to exclude an event      */
specifier|public
name|UniversalFilter
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|,
annotation|@
name|Nonnull
name|Selector
name|selector
parameter_list|,
annotation|@
name|Nonnull
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|predicate
parameter_list|)
block|{
name|this
operator|.
name|beforeState
operator|=
name|checkNotNull
argument_list|(
name|before
argument_list|)
expr_stmt|;
name|this
operator|.
name|afterState
operator|=
name|checkNotNull
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|this
operator|.
name|predicate
operator|=
name|checkNotNull
argument_list|(
name|predicate
argument_list|)
expr_stmt|;
name|this
operator|.
name|selector
operator|=
name|checkNotNull
argument_list|(
name|selector
argument_list|)
expr_stmt|;
block|}
comment|/**      * A selector instance maps call backs on {@code Filters} to {@code NodeState} instances,      * which should be used for determining inclusion or exclusion of the associated event.      */
specifier|public
interface|interface
name|Selector
block|{
comment|/**          * Map a property event.          * @param filter  filter instance on which respective call back occurred.          * @param before  before state or {@code null} for          *                {@link EventFilter#includeAdd(PropertyState)}          * @param after   after state or {@code null} for          *                {@link EventFilter#includeDelete(PropertyState)}          * @return a {@code NodeState} instance for basing the filtering criterion (predicate) upon          */
annotation|@
name|Nonnull
name|NodeState
name|select
parameter_list|(
annotation|@
name|Nonnull
name|UniversalFilter
name|filter
parameter_list|,
annotation|@
name|CheckForNull
name|PropertyState
name|before
parameter_list|,
annotation|@
name|CheckForNull
name|PropertyState
name|after
parameter_list|)
function_decl|;
comment|/**          * Map a node event.          * @param filter  filter instance on which respective call back occurred.          * @param name    name of the child node state          * @param before  before state or {@code null} for          *                {@link EventFilter#includeAdd(String, NodeState)}          * @param after   after state or {@code null} for          *                {@link EventFilter#includeDelete(String, NodeState)}          * @return a NodeState instance for basing the filtering criterion (predicate) upon          */
annotation|@
name|Nonnull
name|NodeState
name|select
parameter_list|(
annotation|@
name|Nonnull
name|UniversalFilter
name|filter
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|before
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|after
parameter_list|)
function_decl|;
block|}
comment|/**      * @return  before state for this filter      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getBeforeState
parameter_list|()
block|{
return|return
name|beforeState
return|;
block|}
comment|/**      * @return  after state for this filter      */
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getAfterState
parameter_list|()
block|{
return|return
name|afterState
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeChange
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|before
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeAdd
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|after
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeDelete
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|before
argument_list|,
name|MISSING_NODE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeMove
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|moved
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|moved
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|includeReorder
parameter_list|(
name|String
name|destName
parameter_list|,
name|String
name|name
parameter_list|,
name|NodeState
name|reordered
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|apply
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|MISSING_NODE
argument_list|,
name|reordered
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EventFilter
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
return|return
operator|new
name|UniversalFilter
argument_list|(
name|beforeState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|afterState
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|selector
argument_list|,
name|predicate
argument_list|)
return|;
block|}
block|}
end_class

end_unit

