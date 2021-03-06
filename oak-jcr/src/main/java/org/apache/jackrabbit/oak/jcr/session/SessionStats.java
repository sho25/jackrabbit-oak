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
name|SECONDS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

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
name|Date
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|api
operator|.
name|stats
operator|.
name|RepositoryStatistics
operator|.
name|Type
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
name|AuthInfo
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
name|jmx
operator|.
name|SessionMBean
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
name|delegate
operator|.
name|SessionDelegate
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|Clock
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
name|stats
operator|.
name|StatisticManager
import|;
end_import

begin_class
specifier|public
class|class
name|SessionStats
implements|implements
name|SessionMBean
block|{
comment|/**      * The threshold of active sessions from where on it should record the stack trace for new sessions. The reason why      * this is not enabled by default is because recording stack traces is rather expensive and can significantly      * slow down the code if sessions are created and thrown away in a loop.      *      * Once this threshold is exceeded, we assume that there is a session leak which should be fixed and start recording      * the stack traces to make it easier to find the cause of it.      *      * Configurable by the "oak.sessionStats.initStackTraceThreshold" system property. Set to "0" to record stack trace      * information on each session creation.      *      */
specifier|static
specifier|final
name|int
name|INIT_STACK_TRACE_THRESHOLD
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.sessionStats.initStackTraceThreshold"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Exception
name|initStackTrace
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|RepositoryException
argument_list|>
name|lastFailedSave
init|=
operator|new
name|AtomicReference
argument_list|<
name|RepositoryException
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Counters
name|counters
decl_stmt|;
specifier|private
specifier|final
name|String
name|sessionId
decl_stmt|;
specifier|private
specifier|final
name|AuthInfo
name|authInfo
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|RefreshStrategy
name|refreshStrategy
decl_stmt|;
specifier|private
specifier|volatile
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
specifier|public
name|SessionStats
parameter_list|(
name|String
name|sessionId
parameter_list|,
name|AuthInfo
name|authInfo
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|RefreshStrategy
name|refreshStrategy
parameter_list|,
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|StatisticManager
name|statisticManager
parameter_list|)
block|{
name|this
operator|.
name|counters
operator|=
operator|new
name|Counters
argument_list|(
name|clock
argument_list|)
expr_stmt|;
name|this
operator|.
name|sessionId
operator|=
name|sessionId
expr_stmt|;
name|this
operator|.
name|authInfo
operator|=
name|authInfo
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|refreshStrategy
operator|=
name|refreshStrategy
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|long
name|activeSessionCount
init|=
name|statisticManager
operator|.
name|getStatsCounter
argument_list|(
name|Type
operator|.
name|SESSION_COUNT
argument_list|)
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|initStackTrace
operator|=
operator|(
name|activeSessionCount
operator|>
name|INIT_STACK_TRACE_THRESHOLD
operator|)
condition|?
operator|new
name|Exception
argument_list|(
literal|"The session was opened here:"
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|sessionDelegate
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Counters
block|{
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|long
name|loginTime
decl_stmt|;
specifier|public
name|long
name|accessTime
decl_stmt|;
specifier|public
name|long
name|readTime
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|writeTime
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|refreshTime
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|saveTime
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|readCount
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|writeCount
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|refreshCount
init|=
literal|0
decl_stmt|;
specifier|public
name|long
name|saveCount
init|=
literal|0
decl_stmt|;
specifier|public
name|Counters
parameter_list|(
name|Clock
name|clock
parameter_list|)
block|{
name|long
name|time
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|loginTime
operator|=
name|time
expr_stmt|;
name|this
operator|.
name|accessTime
operator|=
name|time
expr_stmt|;
block|}
specifier|public
name|Date
name|getLoginTime
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|loginTime
argument_list|)
return|;
block|}
specifier|public
name|Date
name|getReadTime
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
name|readTime
argument_list|)
return|;
block|}
specifier|public
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|readCount
return|;
block|}
specifier|public
name|Date
name|getWriteTime
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
name|writeTime
argument_list|)
return|;
block|}
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
return|;
block|}
specifier|public
name|Date
name|getRefreshTime
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
name|refreshTime
argument_list|)
return|;
block|}
specifier|public
name|long
name|getRefreshCount
parameter_list|()
block|{
return|return
name|refreshCount
return|;
block|}
specifier|public
name|Date
name|getSaveTime
parameter_list|()
block|{
return|return
name|getTime
argument_list|(
name|saveTime
argument_list|)
return|;
block|}
specifier|public
name|long
name|getSaveCount
parameter_list|()
block|{
return|return
name|saveCount
return|;
block|}
specifier|public
name|long
name|getSecondsSinceLogin
parameter_list|()
block|{
return|return
name|SECONDS
operator|.
name|convert
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|loginTime
argument_list|,
name|MILLISECONDS
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Date
name|getTime
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
if|if
condition|(
name|timestamp
operator|!=
literal|0
condition|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
specifier|public
name|void
name|setAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
block|}
specifier|public
name|void
name|failedSave
parameter_list|(
name|RepositoryException
name|repositoryException
parameter_list|)
block|{
name|lastFailedSave
operator|.
name|set
argument_list|(
name|repositoryException
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getAuthInfo
argument_list|()
operator|.
name|getUserID
argument_list|()
operator|+
literal|'@'
operator|+
name|sessionId
operator|+
literal|'@'
operator|+
name|getLoginTimeStamp
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< SessionMBean>---
annotation|@
name|Override
specifier|public
name|String
name|getInitStackTrace
parameter_list|()
block|{
return|return
name|format
argument_list|(
name|initStackTrace
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AuthInfo
name|getAuthInfo
parameter_list|()
block|{
return|return
name|authInfo
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLoginTimeStamp
parameter_list|()
block|{
return|return
name|formatDate
argument_list|(
name|counters
operator|.
name|getLoginTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastReadAccess
parameter_list|()
block|{
return|return
name|formatDate
argument_list|(
name|counters
operator|.
name|getReadTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|counters
operator|.
name|getReadCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getReadRate
parameter_list|()
block|{
return|return
name|calculateRate
argument_list|(
name|getReadCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastWriteAccess
parameter_list|()
block|{
return|return
name|formatDate
argument_list|(
name|counters
operator|.
name|getWriteTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|counters
operator|.
name|getWriteCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getWriteRate
parameter_list|()
block|{
return|return
name|calculateRate
argument_list|(
name|getWriteCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastRefresh
parameter_list|()
block|{
return|return
name|formatDate
argument_list|(
name|counters
operator|.
name|getRefreshTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRefreshStrategy
parameter_list|()
block|{
return|return
name|refreshStrategy
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getRefreshPending
parameter_list|()
block|{
return|return
name|refreshStrategy
operator|.
name|needsRefresh
argument_list|(
name|SECONDS
operator|.
name|convert
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|-
name|counters
operator|.
name|accessTime
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getRefreshCount
parameter_list|()
block|{
return|return
name|counters
operator|.
name|getRefreshCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getRefreshRate
parameter_list|()
block|{
return|return
name|calculateRate
argument_list|(
name|getRefreshCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastSave
parameter_list|()
block|{
return|return
name|formatDate
argument_list|(
name|counters
operator|.
name|getSaveTime
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSaveCount
parameter_list|()
block|{
return|return
name|counters
operator|.
name|getSaveCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getSaveRate
parameter_list|()
block|{
return|return
name|calculateRate
argument_list|(
name|getSaveCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getSessionAttributes
parameter_list|()
block|{
name|String
index|[]
name|atts
init|=
operator|new
name|String
index|[
name|attributes
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attribute
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|atts
index|[
name|k
operator|++
index|]
operator|=
name|attribute
operator|.
name|getKey
argument_list|()
operator|+
literal|'='
operator|+
name|attribute
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
return|return
name|atts
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLastFailedSave
parameter_list|()
block|{
return|return
name|format
argument_list|(
name|lastFailedSave
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|refresh
parameter_list|()
block|{
specifier|final
name|SessionDelegate
name|sd
init|=
name|sessionDelegate
decl_stmt|;
if|if
condition|(
name|sd
operator|!=
literal|null
condition|)
block|{
name|sd
operator|.
name|safePerform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|"MBean initiated refresh"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
block|{
name|sd
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkPreconditions
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|sd
operator|.
name|checkAlive
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|sd
operator|.
name|refresh
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< internal>---
specifier|private
specifier|static
name|String
name|formatDate
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
return|return
name|date
operator|==
literal|null
condition|?
literal|""
else|:
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
else|else
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|private
name|double
name|calculateRate
parameter_list|(
name|long
name|count
parameter_list|)
block|{
name|double
name|dt
init|=
name|counters
operator|.
name|getSecondsSinceLogin
argument_list|()
decl_stmt|;
if|if
condition|(
name|dt
operator|>
literal|0
condition|)
block|{
return|return
name|count
operator|/
name|dt
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
block|}
block|}
end_class

end_unit

