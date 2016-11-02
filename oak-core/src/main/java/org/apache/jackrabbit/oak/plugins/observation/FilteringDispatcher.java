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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Observer
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
comment|/**  * Part of the FilteringObserver: the FilteringDispatcher is used  * to implement the skipping (filtering) of content changes  * which the FilteringDispatcher flags as NOOP_CHANGE.  * When the FilteringDispatcher notices a NOOP_CHANGE it does  * not forward the change but only updates the before NodeState.  */
end_comment

begin_class
specifier|public
class|class
name|FilteringDispatcher
implements|implements
name|Observer
block|{
specifier|private
specifier|final
name|FilteringAwareObserver
name|observer
decl_stmt|;
specifier|private
name|NodeState
name|before
decl_stmt|;
specifier|public
name|FilteringDispatcher
parameter_list|(
name|FilteringAwareObserver
name|observer
parameter_list|)
block|{
name|this
operator|.
name|observer
operator|=
name|checkNotNull
argument_list|(
name|observer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
name|FilteringObserver
operator|.
name|NOOP_CHANGE
condition|)
block|{
name|observer
operator|.
name|contentChanged
argument_list|(
name|before
argument_list|,
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
name|before
operator|=
name|root
expr_stmt|;
block|}
block|}
end_class

end_unit

