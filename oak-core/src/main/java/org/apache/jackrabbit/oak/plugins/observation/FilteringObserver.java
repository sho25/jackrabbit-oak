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
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executor
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
name|spi
operator|.
name|commit
operator|.
name|BackgroundObserver
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
comment|/**  * An observer that implements filtering of content changes  * while at the same time supporting (wrapping) a BackgroundObserver  * underneath.  *<p>  * The FilteringObserver uses an explicit Filter to decide whether  * or not to forward a content change to the BackgroundObserver.  * If the Filter decides to include the change things happen as usual.  * If the Filter decides to exclude the change, this FilteringObserver  * does not forward the change, but remembers the fact that the last  * change was filtered. The first included change after excluded ones  * will cause a NOOP_CHANGE commitInfo to be passed along to the  * BackgroundObserver. That NOOP_CHANGE is then used by the  * FilteringDispatcher: if a CommitInfo is a NOOP_CHANGE then the  * FilteringDispatcher will not forward anything to the FilteringAwareObserver  * and only adjust the 'before' state accordingly (which it does also  * for a NOOP_CHANGE, to exactly achieve the skipping effect).  */
end_comment

begin_class
specifier|public
class|class
name|FilteringObserver
implements|implements
name|Observer
implements|,
name|Closeable
block|{
comment|/** package protected CommitInfo used between FilteringObserver and FilteringDispatcher **/
specifier|final
specifier|static
name|CommitInfo
name|NOOP_CHANGE
init|=
operator|new
name|CommitInfo
argument_list|(
name|CommitInfo
operator|.
name|OAK_UNKNOWN
argument_list|,
name|CommitInfo
operator|.
name|OAK_UNKNOWN
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BackgroundObserver
name|backgroundObserver
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
name|NodeState
name|lastNoop
decl_stmt|;
comment|/**      * Default constructor which creates a BackgroundObserver automatically, including      * creating a FilteringDispatcher.      * @param executor the executor that should be used for the BackgroundObserver      * @param queueLength the queue length of the BackgroundObserver      * @param filter the Filter to be used for filtering      * @param observer the FilteringAwareObserver to which content changes ultimately      * are delivered after going through a chain of       * FilteringObserver-&gt;BackgroundObserver-&gt;FilteringDispatcher.      */
specifier|public
name|FilteringObserver
parameter_list|(
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|,
name|int
name|queueLength
parameter_list|,
annotation|@
name|Nonnull
name|Filter
name|filter
parameter_list|,
annotation|@
name|Nonnull
name|FilteringAwareObserver
name|observer
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|BackgroundObserver
argument_list|(
operator|new
name|FilteringDispatcher
argument_list|(
name|checkNotNull
argument_list|(
name|observer
argument_list|)
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|executor
argument_list|)
argument_list|,
name|queueLength
argument_list|)
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
comment|/**      * Alternative constructor where the BackgroundObserver is created elsewhere      * @param backgroundObserver the BackgroundObserver to be used by this FilteringObserver      * @param filter the Filter to be used for filtering      */
specifier|public
name|FilteringObserver
parameter_list|(
annotation|@
name|Nonnull
name|BackgroundObserver
name|backgroundObserver
parameter_list|,
annotation|@
name|Nonnull
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|backgroundObserver
operator|=
name|backgroundObserver
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|checkNotNull
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BackgroundObserver
name|getBackgroundObserver
parameter_list|()
block|{
return|return
name|backgroundObserver
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|CommitInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|excludes
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
condition|)
block|{
name|lastNoop
operator|=
name|root
expr_stmt|;
return|return;
block|}
comment|// current change is not an noop
if|if
condition|(
name|lastNoop
operator|!=
literal|null
condition|)
block|{
comment|// report up to previous noop
name|backgroundObserver
operator|.
name|contentChanged
argument_list|(
name|lastNoop
argument_list|,
name|NOOP_CHANGE
argument_list|)
expr_stmt|;
name|lastNoop
operator|=
literal|null
expr_stmt|;
block|}
name|backgroundObserver
operator|.
name|contentChanged
argument_list|(
name|root
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|backgroundObserver
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

