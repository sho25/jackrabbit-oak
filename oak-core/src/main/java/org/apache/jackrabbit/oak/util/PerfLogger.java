begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * PerfLogger is a simpler wrapper around a slf4j Logger which   * comes with the capability to issue log statements containing  * the measurement between start() and end() methods.  *<p>  * Usage:  *<ul>  *<li>final long start = perflogger.start();</li>  *<li>.. some code ..  *<li>perflogger.end(start, 1, "myMethodName: param1={}", param1);</li>  *</ul>  *<p>  * The above will do nothing if the log level for the logger passed  * to PerfLogger at construction time is not DEBUG or TRACE - otherwise  * start() will return the current time in milliseconds and end will  * issue a log statement if the time between start and end was bigger  * than 1 ms, and it will pass the parameters to the log statement.  * The idea is to keep up performance at max possible if the log   * level is INFO or higher - but to allow some meaningful logging  * if at DEBUG or TRACe. The difference between DEBUG and TRACE is  * that TRACE will log start too (if a log message is passed to start)  * and it will always log the end - whereas in case of DEBUG the start  * will never be logged and the end will only be logged if the time  * is bigger than what's passed to end.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|PerfLogger
block|{
comment|/** The logger to which the log statements are emitted **/
specifier|private
specifier|final
name|Logger
name|delegate
decl_stmt|;
comment|/** Create a new PerfLogger that shall use the given Logger object for logging **/
specifier|public
name|PerfLogger
parameter_list|(
name|Logger
name|delegate
parameter_list|)
block|{
if|if
condition|(
name|delegate
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"delegate must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
comment|/**      * Returns quickly if log level is not DEBUG or TRACE - otherwise just      * returns the current time in millis.      *       * @return the current time if level is DEBUG or TRACE, -1 otherwise      */
specifier|public
specifier|final
name|long
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// if log level is not at least DEBUG, then return fast, no-op
return|return
operator|-
literal|1
return|;
block|}
return|return
name|start
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns quickly if log level is not DEBUG or TRACE - if it is DEBUG, then      * just returns the current time in millis, if it is TRACE, then log the      * given message and also return the current time in millis.      *       * @param traceMsgOrNull      *            the message to log if log level is TRACE - or null if no      *            message should be logged (even on TRACE level)      * @return the current time if level is DEBUG or TRACE, -1 otherwise      */
specifier|public
specifier|final
name|long
name|start
parameter_list|(
name|String
name|traceMsgOrNull
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// if log level is not at least DEBUG, then return fast, no-op
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|traceMsgOrNull
operator|!=
literal|null
operator|&&
name|delegate
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|delegate
operator|.
name|trace
argument_list|(
name|traceMsgOrNull
argument_list|)
expr_stmt|;
block|}
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
return|;
block|}
comment|/**      * Returns quickly if log level is not DEBUG or TRACE - if it is DEBUG      * and the difference between now and the provided 'start' is bigger (slower)      * than 'logAtDebugIfSlowerThanMs', then a log.debug is emitted, if at      * TRACE then a log.trace is always emitted (independent of time measured).      *<p>      * Note that this method exists for performance optimization only (compared      * to the other end() method with a vararg.      * @param start the start time with which 'now' should be compared      * @param logAtDebugIfSlowerThanMs the number of milliseconds that must      * be surpassed to issue a log.debug (if log level is DEBUG)      * @param logMessagePrefix the log message 'prefix' - to which the given      * argument will be passed, plus the measured time difference in the format      * '[took x ms']      * @param arg1 the argument which is to be passed to the log statement      */
specifier|public
specifier|final
name|void
name|end
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|logAtDebugIfSlowerThanMs
parameter_list|,
name|String
name|logMessagePrefix
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|end
argument_list|(
name|start
argument_list|,
name|logAtDebugIfSlowerThanMs
argument_list|,
name|logMessagePrefix
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns quickly if log level is not DEBUG or TRACE - if it is DEBUG      * and the difference between now and the provided 'start' is bigger (slower)      * than 'logAtDebugIfSlowerThanMs', then a log.debug is emitted, if at      * TRACE then a log.trace is always emitted (independent of time measured).      *<p>      * Note that this method exists for performance optimization only (compared      * to the other end() method with a vararg.      * @param start the start time with which 'now' should be compared      * @param logAtDebugIfSlowerThanMs the number of milliseconds that must      * be surpassed to issue a log.debug (if log level is DEBUG)      * @param logMessagePrefix the log message 'prefix' - to which the given      * arguments will be passed, plus the measured time difference in the format      * '[took x ms']      * @param arg1 the first argument which is to be passed to the log statement      * @param arg2 the second argument which is to be passed to the log statement      */
specifier|public
specifier|final
name|void
name|end
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|logAtDebugIfSlowerThanMs
parameter_list|,
name|String
name|logMessagePrefix
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|end
argument_list|(
name|start
argument_list|,
name|logAtDebugIfSlowerThanMs
argument_list|,
name|logMessagePrefix
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg1
block|,
name|arg2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns quickly if log level is not DEBUG or TRACE - if it is DEBUG      * and the difference between now and the provided 'start' is bigger (slower)      * than 'logAtDebugIfSlowerThanMs', then a log.debug is emitted, if at      * TRACE then a log.trace is always emitted (independent of time measured).      * @param start the start time with which 'now' should be compared      * @param logAtDebugIfSlowerThanMs the number of milliseconds that must      * be surpassed to issue a log.debug (if log level is DEBUG)      * @param logMessagePrefix the log message 'prefix' - to which the given      * arguments will be passed, plus the measured time difference in the format      * '[took x ms']      * @param arguments the arguments which is to be passed to the log statement      */
specifier|public
name|void
name|end
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|logAtDebugIfSlowerThanMs
parameter_list|,
name|String
name|logMessagePrefix
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delegate
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// if log level is not at least DEBUG, then return fast, no-op
return|return;
block|}
if|if
condition|(
name|start
operator|==
operator|-
literal|1
condition|)
block|{
comment|// start was never set
comment|// -> then log at trace as we have no diff available
name|delegate
operator|.
name|trace
argument_list|(
name|logMessagePrefix
operator|+
literal|" [start not set]"
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|arguments
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|diff
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|delegate
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// if log level is TRACE, then always log - and do that on TRACE
comment|// then:
name|delegate
operator|.
name|trace
argument_list|(
name|logMessagePrefix
operator|+
literal|" [took "
operator|+
name|diff
operator|+
literal|"ms]"
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|arguments
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|logAtDebugIfSlowerThanMs
operator|<
literal|0
operator|)
operator|||
operator|(
name|diff
operator|>
name|logAtDebugIfSlowerThanMs
operator|)
condition|)
block|{
comment|// otherwise (log level is DEBUG) only log if
comment|// logDebugIfSlowerThanMs is set to -1 (or negative)
comment|// OR the measured diff is larger than logDebugIfSlowerThanMs -
comment|// and then do that on DEBUG:
name|delegate
operator|.
name|debug
argument_list|(
name|logMessagePrefix
operator|+
literal|" [took "
operator|+
name|diff
operator|+
literal|"ms]"
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|arguments
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Whether or not the delegate has log level DEBUG configured **/
specifier|public
specifier|final
name|boolean
name|isDebugEnabled
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isDebugEnabled
argument_list|()
return|;
block|}
comment|/** Whether or not the delegate has log level TRACE configured **/
specifier|public
specifier|final
name|boolean
name|isTraceEnabled
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isTraceEnabled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

