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
name|jcr
operator|.
name|session
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|jcr
operator|.
name|session
operator|.
name|operation
operator|.
name|SessionOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Instances of this class determine whether a session needs to be refreshed base on  * the current {@link SessionOperation operation} to be performed and past  * {@link #refreshed() refreshes} and {@link #saved() saves}.  *<p>  * Before an operation is performed a session calls {@link #needsRefresh(SessionOperation)},  * to determine whether the session needs to be refreshed first. To maintain a session strategy's  * state sessions call {@link #refreshed()} right after each refresh operation and  * {@link #saved()} right after each save operation.  *<p>  * {@code RefreshStrategy} is a composite of zero or more {@code RefreshStrategy} instances,  * each of which covers a certain strategy.  * @see Default  * @see Once  * @see Timed  * @see LogOnce  * @see ThreadSynchronising  */
end_comment

begin_class
specifier|public
class|class
name|RefreshStrategy
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RefreshStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|RefreshStrategy
index|[]
name|refreshStrategies
decl_stmt|;
comment|/**      * Create a new instance consisting of the composite of the passed {@code RefreshStrategy}      * instances.      * @param refreshStrategies  individual refresh strategies      */
specifier|public
name|RefreshStrategy
parameter_list|(
name|RefreshStrategy
modifier|...
name|refreshStrategies
parameter_list|)
block|{
name|this
operator|.
name|refreshStrategies
operator|=
name|refreshStrategies
expr_stmt|;
block|}
comment|/**      * Determine whether the session needs to refresh before {@code sessionOperation} is performed.      *<p>      * This implementation return {@code false} if either {@code sessionsOperation} is an refresh      * operation or a save operation. Otherwise it returns {@code true} if and only if any of the      * individual refresh strategies passed to the constructor returns {@code true}.      * @param sessionOperation  operation about to be performed      * @return  {@code true} if and only if the session needs to refresh.      */
specifier|public
name|boolean
name|needsRefresh
parameter_list|(
name|SessionOperation
argument_list|<
name|?
argument_list|>
name|sessionOperation
parameter_list|)
block|{
comment|// Don't refresh if this operation is a refresh operation itself or
comment|// a save operation, which does an implicit refresh
if|if
condition|(
name|sessionOperation
operator|.
name|isRefresh
argument_list|()
operator|||
name|sessionOperation
operator|.
name|isSave
argument_list|()
operator|||
name|sessionOperation
operator|.
name|isLogout
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
comment|// Don't shortcut here since the individual strategies rely on side effects of this call
for|for
control|(
name|RefreshStrategy
name|r
range|:
name|refreshStrategies
control|)
block|{
name|refresh
operator||=
name|r
operator|.
name|needsRefresh
argument_list|(
name|sessionOperation
argument_list|)
expr_stmt|;
block|}
return|return
name|refresh
return|;
block|}
comment|/**      * Called whenever a session has been refreshed.      *<p>      * This implementation forwards to the {@code refresh} method of the individual refresh      * strategies passed to the constructor.      */
specifier|public
name|void
name|refreshed
parameter_list|()
block|{
for|for
control|(
name|RefreshStrategy
name|r
range|:
name|refreshStrategies
control|)
block|{
name|r
operator|.
name|refreshed
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Called whenever a session has been saved.      *<p>      * This implementation forwards to the {@code save} method of the individual refresh      * strategies passed to the constructor.      */
specifier|public
name|void
name|saved
parameter_list|()
block|{
for|for
control|(
name|RefreshStrategy
name|r
range|:
name|refreshStrategies
control|)
block|{
name|r
operator|.
name|saved
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Accept the passed visitor.      *<p>      * This implementation forwards to the {@code accept} method of the individual refresh      * strategies passed to the constructor.      */
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
for|for
control|(
name|RefreshStrategy
name|r
range|:
name|refreshStrategies
control|)
block|{
name|r
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Visitor for traversing the composite.      */
specifier|public
specifier|static
class|class
name|Visitor
block|{
specifier|public
name|void
name|visit
parameter_list|(
name|Default
name|strategy
parameter_list|)
block|{}
specifier|public
name|void
name|visit
parameter_list|(
name|Once
name|strategy
parameter_list|)
block|{}
specifier|public
name|void
name|visit
parameter_list|(
name|Timed
name|strategy
parameter_list|)
block|{}
specifier|public
name|void
name|visit
parameter_list|(
name|LogOnce
name|strategy
parameter_list|)
block|{}
specifier|public
name|void
name|visit
parameter_list|(
name|ThreadSynchronising
name|strategy
parameter_list|)
block|{}
block|}
comment|/**      * This refresh strategy does wither always or never refresh depending of the value of the      * {@code refresh} argument passed to its constructor.      *<p>      */
specifier|public
specifier|static
class|class
name|Default
extends|extends
name|RefreshStrategy
block|{
comment|/** A refresh strategy that always refreshed */
specifier|public
specifier|static
name|RefreshStrategy
name|ALWAYS
init|=
operator|new
name|Default
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/** A refresh strategy that never refreshed */
specifier|public
specifier|static
name|RefreshStrategy
name|NEVER
init|=
operator|new
name|Default
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/** Value returned from {@code needsRefresh} */
specifier|protected
name|boolean
name|refresh
decl_stmt|;
comment|/**          * @param refresh  value returned from {@code needsRefresh}          */
specifier|public
name|Default
parameter_list|(
name|boolean
name|refresh
parameter_list|)
block|{
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
block|}
comment|/**          * @return {@link #refresh}          */
annotation|@
name|Override
specifier|public
name|boolean
name|needsRefresh
parameter_list|(
name|SessionOperation
argument_list|<
name|?
argument_list|>
name|sessionOperation
parameter_list|)
block|{
return|return
name|refresh
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshed
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|saved
parameter_list|()
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This refresh strategy refreshed exactly once when enabled. Calling      * {@link #reset()} enables the strategy.      */
specifier|public
specifier|static
class|class
name|Once
extends|extends
name|Default
block|{
comment|/** Visitor for resetting this refresh strategy */
specifier|public
specifier|static
specifier|final
name|Visitor
name|RESETTING_VISITOR
init|=
operator|new
name|Visitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|Once
name|strategy
parameter_list|)
block|{
name|strategy
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**          * @param enabled  whether this refresh strategy is initially enabled          */
specifier|public
name|Once
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|super
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**          * Enable this refresh strategy          */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|refresh
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshed
parameter_list|()
block|{
name|refresh
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|saved
parameter_list|()
block|{
name|refresh
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This refresh strategy refreshes after a given timeout of inactivity.      */
specifier|public
specifier|static
class|class
name|Timed
extends|extends
name|RefreshStrategy
block|{
specifier|private
specifier|final
name|long
name|interval
decl_stmt|;
specifier|private
name|long
name|lastAccessed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/**          * @param interval  Interval in seconds after which a session should refresh if there was no          *                  activity.          */
specifier|public
name|Timed
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|interval
argument_list|,
name|SECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**          * Called whenever {@code needsRefresh} determines that the time out interval was exceeded.          * This default implementation always returns {@code true}. Descendants may override this          * method to provide more refined behaviour.          * @param timeElapsed  the time that elapsed since the session was last accessed.          * @return {@code true}          */
specifier|protected
name|boolean
name|timeOut
parameter_list|(
name|long
name|timeElapsed
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRefresh
parameter_list|(
name|SessionOperation
argument_list|<
name|?
argument_list|>
name|sessionOperation
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|timeElapsed
init|=
name|now
operator|-
name|lastAccessed
decl_stmt|;
name|lastAccessed
operator|=
name|now
expr_stmt|;
return|return
name|timeElapsed
operator|>
name|interval
operator|&&
name|timeOut
argument_list|(
name|timeElapsed
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshed
parameter_list|()
block|{
name|lastAccessed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|saved
parameter_list|()
block|{
name|lastAccessed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This refresh strategy never refreshed the session but logs a warning if a session has been      * idle for more than a given time.      *      * TODO replace logging with JMX monitoring. See OAK-941      */
specifier|public
specifier|static
class|class
name|LogOnce
extends|extends
name|Timed
block|{
specifier|private
specifier|final
name|Exception
name|initStackTrace
init|=
operator|new
name|Exception
argument_list|(
literal|"The session was created here:"
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|warnIfIdle
init|=
literal|true
decl_stmt|;
comment|/**          * @param interval  Interval in seconds after which a warning is logged if there was no          *                  activity.          */
specifier|public
name|LogOnce
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|super
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
comment|/**          * Log once          * @param timeElapsed  the time that elapsed since the session was last accessed.          * @return  {@code false}          */
annotation|@
name|Override
specifier|protected
name|boolean
name|timeOut
parameter_list|(
name|long
name|timeElapsed
parameter_list|)
block|{
if|if
condition|(
name|warnIfIdle
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"This session has been idle for "
operator|+
name|MINUTES
operator|.
name|convert
argument_list|(
name|timeElapsed
argument_list|,
name|MILLISECONDS
argument_list|)
operator|+
literal|" minutes and might be out of date. "
operator|+
literal|"Consider using a fresh session or explicitly refresh the session."
argument_list|,
name|initStackTrace
argument_list|)
expr_stmt|;
name|warnIfIdle
operator|=
literal|false
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This refresh strategy synchronises session states across accesses within the same thread.      */
specifier|public
specifier|static
class|class
name|ThreadSynchronising
extends|extends
name|RefreshStrategy
block|{
comment|/**          * ThreadLocal instance to keep track of the save operations performed in the thread so far          * This is is then used to determine if the current session needs to be refreshed to see the          * changes done by another session in current thread.          *<p>          *<b>Note</b> - This thread local is never cleared. However, we only store          * java.lang.Integer and do not derive from ThreadLocal such that (class loader)          * leaks typically associated with thread locals do not occur.          */
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|threadSaveCount
decl_stmt|;
specifier|private
name|long
name|sessionSaveCount
decl_stmt|;
comment|/**          * @param threadSaveCount  thread local for tracking thread local state.          */
specifier|public
name|ThreadSynchronising
parameter_list|(
name|ThreadLocal
argument_list|<
name|Long
argument_list|>
name|threadSaveCount
parameter_list|)
block|{
name|this
operator|.
name|threadSaveCount
operator|=
name|threadSaveCount
expr_stmt|;
name|sessionSaveCount
operator|=
name|getThreadSaveCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsRefresh
parameter_list|(
name|SessionOperation
argument_list|<
name|?
argument_list|>
name|sessionOperation
parameter_list|)
block|{
comment|// If the threadLocal counter differs from our seen sessionSaveCount so far then
comment|// some other session would have done a commit. If that is the case a refresh would
comment|// be required
return|return
name|getThreadSaveCount
argument_list|()
operator|!=
name|sessionSaveCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshed
parameter_list|()
block|{
comment|// Avoid further refreshing if refreshed already
name|sessionSaveCount
operator|=
name|getThreadSaveCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|saved
parameter_list|()
block|{
comment|// Force refreshing on access through other sessions on the same thread
name|threadSaveCount
operator|.
name|set
argument_list|(
name|sessionSaveCount
operator|=
operator|(
name|getThreadSaveCount
argument_list|()
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|getThreadSaveCount
parameter_list|()
block|{
name|Long
name|c
init|=
name|threadSaveCount
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|c
operator|==
literal|null
condition|?
literal|0
else|:
name|c
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

