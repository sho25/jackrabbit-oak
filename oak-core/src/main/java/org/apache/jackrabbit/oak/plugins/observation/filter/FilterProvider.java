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
name|commit
operator|.
name|CommitInfo
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Instance of this class provide a {@link EventFilter} for observation  * events and a filter for commits.  *<p>  * In order to support OAK-4908 a FilterProvider  * extends ChangeSetFilter  */
end_comment

begin_interface
specifier|public
interface|interface
name|FilterProvider
extends|extends
name|ChangeSetFilter
block|{
comment|/**      * Filter whole commits. Only commits for which this method returns      * {@code true} will be further processed to create individual events.      *      * @param sessionId id of the filtering (this) session      * @param info      commit info of the commit or {@code null} if not available      * @return {@code true} if observation events should be created from this      * commit, {@code false} otherwise.      * @see org.apache.jackrabbit.oak.spi.commit.Observer      */
name|boolean
name|includeCommit
parameter_list|(
annotation|@
name|NotNull
name|String
name|sessionId
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
function_decl|;
comment|/**      * Factory method for creating a {@code Filter} for the passed before and after      * states.      *      * @param before before state      * @param after  after state      * @return new {@code Filter} instance      */
annotation|@
name|NotNull
name|EventFilter
name|getFilter
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|before
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|after
parameter_list|)
function_decl|;
comment|/**      * A set of paths whose subtrees include all events of this filter.      * @return  list of paths      * @see org.apache.jackrabbit.oak.plugins.observation.filter.FilterBuilder#addSubTree(String)      */
annotation|@
name|NotNull
name|Iterable
argument_list|<
name|String
argument_list|>
name|getSubTrees
parameter_list|()
function_decl|;
name|FilterConfigMBean
name|getConfigMBean
parameter_list|()
function_decl|;
comment|/**      * Allows providers to supply an optional EventAggregator that      * is used to adjust (aggregate) the event identifier before event      * creation (ie after event filtering).      */
annotation|@
name|Nullable
name|EventAggregator
name|getEventAggregator
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

