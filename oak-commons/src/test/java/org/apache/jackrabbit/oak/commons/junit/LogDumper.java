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
name|commons
operator|.
name|junit
package|;
end_package

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
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|LoggerContext
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|PatternLayout
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|spi
operator|.
name|ILoggingEvent
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|core
operator|.
name|read
operator|.
name|CyclicBufferAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestWatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
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
comment|/**  * The LogDumper Rule collects logs which are generated due to execution of test  and dumps them  * locally upon test failure. This simplifies determining failure  * cause by providing all required data locally. This would be specially useful when running test  * in CI server where server logs gets cluttered with all other test executions  *<p/>  *<pre>  *     public class LoginTestIT {  *  *&#064;Rule  *     public TestRule logDumper = new LogDumper();  *  *&#064;Test  *     public void remoteLogin() {  *          //test stuff  *          assertEquals(&quot;testA&quot;, name.getMethodName());  *     }  *  *     }  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|LogDumper
extends|extends
name|TestWatcher
block|{
comment|/**      * Number of log entries to keep in memory      */
specifier|private
specifier|static
specifier|final
name|int
name|LOG_BUFFER_SIZE
init|=
literal|1000
decl_stmt|;
comment|/**      * Message pattern used to render logs      */
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PATTERN
init|=
literal|"%d{dd.MM.yyyy HH:mm:ss.SSS} *%level* [%thread] %logger %msg%n"
decl_stmt|;
specifier|private
name|CyclicBufferAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|appender
decl_stmt|;
specifier|private
specifier|final
name|int
name|logBufferSize
decl_stmt|;
comment|/**      * Creates a new LogDumper with default log buffer size (1000)      */
specifier|public
name|LogDumper
parameter_list|()
block|{
name|this
argument_list|(
name|LOG_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new LogDumper with the given log buffer size      * @param logBufferSize      */
specifier|public
name|LogDumper
parameter_list|(
name|int
name|logBufferSize
parameter_list|)
block|{
name|this
operator|.
name|logBufferSize
operator|=
name|logBufferSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|finished
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|deregisterAppender
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|starting
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|registerAppender
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|failed
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|Description
name|description
parameter_list|)
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|pw
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|printf
argument_list|(
literal|"=============== Logs for [%s#%s]===================%n"
argument_list|,
name|description
operator|.
name|getClassName
argument_list|()
argument_list|,
name|description
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|getLogs
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|"========================================================"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error occurred while fetching test logs"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getLogs
parameter_list|()
block|{
if|if
condition|(
name|appender
operator|==
literal|null
condition|)
block|{
return|return
literal|"<Logs cannot be determined>"
return|;
block|}
name|PatternLayout
name|layout
init|=
name|createLayout
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|appender
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|layout
operator|.
name|doLayout
argument_list|(
name|appender
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|registerAppender
parameter_list|()
block|{
name|appender
operator|=
operator|new
name|CyclicBufferAppender
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|()
expr_stmt|;
name|appender
operator|.
name|setMaxSize
argument_list|(
name|logBufferSize
argument_list|)
expr_stmt|;
name|appender
operator|.
name|setContext
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|appender
operator|.
name|setName
argument_list|(
literal|"TestLogCollector"
argument_list|)
expr_stmt|;
name|appender
operator|.
name|start
argument_list|()
expr_stmt|;
name|rootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|deregisterAppender
parameter_list|()
block|{
if|if
condition|(
name|appender
operator|!=
literal|null
condition|)
block|{
name|rootLogger
argument_list|()
operator|.
name|detachAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|appender
operator|.
name|stop
argument_list|()
expr_stmt|;
name|appender
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|PatternLayout
name|createLayout
parameter_list|()
block|{
name|PatternLayout
name|pl
init|=
operator|new
name|PatternLayout
argument_list|()
decl_stmt|;
name|pl
operator|.
name|setPattern
argument_list|(
name|DEFAULT_PATTERN
argument_list|)
expr_stmt|;
name|pl
operator|.
name|setOutputPatternAsHeader
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pl
operator|.
name|setContext
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|pl
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|pl
return|;
block|}
specifier|private
specifier|static
name|LoggerContext
name|getContext
parameter_list|()
block|{
return|return
operator|(
name|LoggerContext
operator|)
name|LoggerFactory
operator|.
name|getILoggerFactory
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
name|rootLogger
parameter_list|()
block|{
return|return
name|getContext
argument_list|()
operator|.
name|getLogger
argument_list|(
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Logger
operator|.
name|ROOT_LOGGER_NAME
argument_list|)
return|;
block|}
block|}
end_class

end_unit

