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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|core
operator|.
name|ImmutableTree
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
name|observation
operator|.
name|filter
operator|.
name|EventGenerator
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
name|commit
operator|.
name|CommitInfo
import|;
end_import

begin_comment
comment|/**  * Instance of this class provide a {@link Filter} for observation  * events and a filter for commits.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FilterProvider
block|{
comment|/**      * Filter whole commits. Only commits for which this method returns      * {@code true} will be further processed to create individual events.      *      * @param sessionId  id of the filtering (this) session      * @param info       commit info of the commit or {@code null} if not available      * @return           {@code true} if observation events should be created from this      *                   commit, {@code false} otherwise.      *      * @see org.apache.jackrabbit.oak.spi.commit.Observer      */
name|boolean
name|includeCommit
parameter_list|(
annotation|@
name|Nonnull
name|String
name|sessionId
parameter_list|,
annotation|@
name|CheckForNull
name|CommitInfo
name|info
parameter_list|)
function_decl|;
comment|/**      * Factory method for creating a {@code Filter} for the passed before and after      * states and the given tree permissions of the reading session.      *      * @param beforeTree  before state      * @param afterTree   after state      * @return new {@code Filter} instance      */
annotation|@
name|Nonnull
name|Filter
name|getFilter
parameter_list|(
annotation|@
name|Nonnull
name|ImmutableTree
name|beforeTree
parameter_list|,
annotation|@
name|Nonnull
name|ImmutableTree
name|afterTree
parameter_list|)
function_decl|;
comment|/**      * Path of the subtree to which the the filter returned by      * {@link #getFilter(ImmutableTree, ImmutableTree)} applies.      * @return path to which the filter applies.      */
annotation|@
name|Nonnull
name|String
name|getPath
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

