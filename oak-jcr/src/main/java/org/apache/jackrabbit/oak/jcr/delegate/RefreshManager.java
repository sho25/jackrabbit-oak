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
name|delegate
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
name|operation
operator|.
name|SessionOperation
import|;
end_import

begin_comment
comment|/**  * This class contains the auto refresh logic for sessions, which is done to enhance backwards  * compatibility with Jackrabbit 2.  *<p>  * A sessions is automatically refreshed when  *<ul>  *<li>it has not been accessed for the number of seconds specified by the  *         {@code refreshInterval} parameter,</li>  *<li>an observation event has been delivered to a listener registered from within this  *         session,</li>  *<li>an updated occurred through a different session from<em>within the same  *         thread.</em></li>  *</ul>  * TODO: refactor this using the strategy pattern composing the different refresh behaviours.  * See OAK-960  */
end_comment

begin_class
specifier|public
class|class
name|RefreshManager
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
specifier|final
name|long
name|refreshInterval
decl_stmt|;
comment|/**      * ThreadLocal instance to keep track of the save operations performed in the thread so far      * This is is then used to determine if the current session needs to be refreshed to see the      * changes done by another session in current thread.      *<p>      *<b>Note</b> - This thread local is never cleared. However, we only store      * java.lang.Integer and do not derive from ThreadLocal such that (class loader)      * leaks typically associated with thread locals do not occur.      */
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
name|threadSaveCount
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
specifier|private
name|boolean
name|warnIfIdle
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|refreshAtNextAccess
decl_stmt|;
specifier|private
name|int
name|sessionSaveCount
decl_stmt|;
specifier|public
name|RefreshManager
parameter_list|(
name|long
name|refreshInterval
parameter_list|,
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
name|threadSaveCount
parameter_list|)
block|{
name|this
operator|.
name|refreshInterval
operator|=
name|refreshInterval
expr_stmt|;
name|this
operator|.
name|threadSaveCount
operator|=
name|threadSaveCount
expr_stmt|;
name|sessionSaveCount
operator|=
name|getOr0
argument_list|(
name|threadSaveCount
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called before the passed {@code sessionOperation} is performed. This method      * determines whether a session needs to be refreshed according to the rules      * given in the class comment.      *      * @param sessionOperation  the operation to be executed      * @return  {@code true} if a refreshed, {@code false} otherwise.      */
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
comment|// Don't refresh if this operation is a refresh operation itself or
comment|// a save operation, which does an implicit refresh
if|if
condition|(
operator|!
name|sessionOperation
operator|.
name|isRefresh
argument_list|()
operator|&&
operator|!
name|sessionOperation
operator|.
name|isSave
argument_list|()
condition|)
block|{
if|if
condition|(
name|warnIfIdle
operator|&&
operator|!
name|refreshAtNextAccess
operator|&&
operator|!
name|hasInThreadCommit
argument_list|()
operator|&&
name|timeElapsed
operator|>
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|1
argument_list|,
name|MINUTES
argument_list|)
condition|)
block|{
comment|// TODO replace logging with JMX monitoring. See OAK-941
comment|// Warn once if this session has been idle too long
name|SessionDelegate
operator|.
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
literal|" minutes and might be out of date. Consider using a fresh session or explicitly"
operator|+
literal|" refresh the session."
argument_list|,
name|initStackTrace
argument_list|)
expr_stmt|;
name|warnIfIdle
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|refreshAtNextAccess
operator|||
name|hasInThreadCommit
argument_list|()
operator|||
name|timeElapsed
operator|>=
name|refreshInterval
condition|)
block|{
comment|// Refresh if forced or if the session has been idle too long
name|refreshAtNextAccess
operator|=
literal|false
expr_stmt|;
name|sessionSaveCount
operator|=
name|getOr0
argument_list|(
name|threadSaveCount
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|sessionOperation
operator|.
name|isSave
argument_list|()
condition|)
block|{
name|threadSaveCount
operator|.
name|set
argument_list|(
name|sessionSaveCount
operator|=
operator|(
name|getOr0
argument_list|(
name|threadSaveCount
argument_list|)
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
name|void
name|refreshAtNextAccess
parameter_list|()
block|{
name|refreshAtNextAccess
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|boolean
name|hasInThreadCommit
parameter_list|()
block|{
comment|// If the threadLocal counter differs from our seen sessionSaveCount so far then
comment|// some other session would have done a commit. If that is the case a refresh would
comment|// be required
return|return
name|getOr0
argument_list|(
name|threadSaveCount
argument_list|)
operator|!=
name|sessionSaveCount
return|;
block|}
specifier|private
specifier|static
name|int
name|getOr0
parameter_list|(
name|ThreadLocal
argument_list|<
name|Integer
argument_list|>
name|threadLocal
parameter_list|)
block|{
name|Integer
name|c
init|=
name|threadLocal
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
block|}
end_class

end_unit

