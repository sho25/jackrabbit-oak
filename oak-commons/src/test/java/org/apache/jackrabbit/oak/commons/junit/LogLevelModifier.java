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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Level
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
name|Logger
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
name|filter
operator|.
name|ThresholdFilter
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
name|Appender
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
name|ConsoleAppender
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
name|filter
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * The LogLevelModifier Rule can be used to fine-tune log levels during a particular  * test. This could be used together with LogDumper to have enough details  * in case of test failure without setting the global log level to DEBUG for example.  *<p/>  *<pre>  *     public class LoginTestIT {  *  *&#064;Rule  *     public TestRule logDumper = new LogLevelModifier()  *                                         .addAppenderFilter("console", "warn")  *                                         .setLoggerLevel("org.apache.jackrabbit.oak", "debug");  *  *&#064;Test  *     public void remoteLogin() {  *          //test stuff  *          assertEquals(&quot;testA&quot;, name.getMethodName());  *     }  *  *     }  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|LogLevelModifier
extends|extends
name|TestWatcher
block|{
class|class
name|AppenderFilter
block|{
specifier|private
specifier|final
name|Appender
argument_list|<
name|ILoggingEvent
argument_list|>
name|appender
decl_stmt|;
specifier|private
specifier|final
name|String
name|level
decl_stmt|;
specifier|private
name|ThresholdFilter
name|thFilter
decl_stmt|;
name|AppenderFilter
parameter_list|(
name|String
name|appenderName
parameter_list|,
name|String
name|level
parameter_list|)
block|{
specifier|final
name|Appender
argument_list|<
name|ILoggingEvent
argument_list|>
name|appender
init|=
name|rootLogger
argument_list|()
operator|.
name|getAppender
argument_list|(
name|appenderName
argument_list|)
decl_stmt|;
if|if
condition|(
name|appender
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"no appender found with name "
operator|+
name|appenderName
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|appender
operator|=
name|appender
expr_stmt|;
name|Level
name|l
init|=
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"unknown level: "
operator|+
name|level
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|level
operator|=
name|l
operator|.
name|levelStr
expr_stmt|;
block|}
specifier|public
name|void
name|starting
parameter_list|()
block|{
name|thFilter
operator|=
operator|new
name|ThresholdFilter
argument_list|()
expr_stmt|;
name|thFilter
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|thFilter
operator|.
name|start
argument_list|()
expr_stmt|;
name|appender
operator|.
name|addFilter
argument_list|(
name|thFilter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|finished
parameter_list|()
block|{
if|if
condition|(
name|thFilter
operator|==
literal|null
condition|)
block|{
comment|// then we did not add it
return|return;
block|}
name|List
argument_list|<
name|Filter
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|>
name|filterList
init|=
name|appender
operator|.
name|getCopyOfAttachedFiltersList
argument_list|()
decl_stmt|;
name|appender
operator|.
name|clearAllFilters
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Filter
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|>
name|it
init|=
name|filterList
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Filter
argument_list|<
name|ILoggingEvent
argument_list|>
name|filter
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
name|thFilter
condition|)
block|{
name|appender
operator|.
name|addFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
class|class
name|LoggerLevel
block|{
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
specifier|private
specifier|final
name|Level
name|previousLevel
decl_stmt|;
specifier|private
name|Level
name|level
decl_stmt|;
name|LoggerLevel
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|String
name|level
parameter_list|)
block|{
specifier|final
name|LoggerContext
name|c
init|=
name|getContext
argument_list|()
decl_stmt|;
name|Logger
name|existing
init|=
name|c
operator|.
name|exists
argument_list|(
name|loggerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|logger
operator|=
name|existing
expr_stmt|;
name|previousLevel
operator|=
name|existing
operator|.
name|getLevel
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|=
name|c
operator|.
name|getLogger
argument_list|(
name|loggerName
argument_list|)
expr_stmt|;
name|previousLevel
operator|=
literal|null
expr_stmt|;
block|}
name|Level
name|l
init|=
name|Level
operator|.
name|toLevel
argument_list|(
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"unknown level: "
operator|+
name|level
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|level
operator|=
name|l
expr_stmt|;
block|}
specifier|public
name|void
name|starting
parameter_list|()
block|{
name|logger
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|finished
parameter_list|()
block|{
name|logger
operator|.
name|setLevel
argument_list|(
name|previousLevel
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|List
argument_list|<
name|AppenderFilter
argument_list|>
name|appenderFilters
init|=
operator|new
name|LinkedList
argument_list|<
name|AppenderFilter
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|final
name|List
argument_list|<
name|Appender
argument_list|>
name|newAppenders
init|=
operator|new
name|LinkedList
argument_list|<
name|Appender
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|LoggerLevel
argument_list|>
name|loggerLevels
init|=
operator|new
name|LinkedList
argument_list|<
name|LoggerLevel
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|LogLevelModifier
name|newConsoleAppender
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ConsoleAppender
argument_list|<
name|ILoggingEvent
argument_list|>
name|c
init|=
operator|new
name|ConsoleAppender
argument_list|<
name|ILoggingEvent
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|c
operator|.
name|setContext
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|start
argument_list|()
expr_stmt|;
name|rootLogger
argument_list|()
operator|.
name|addAppender
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|newAppenders
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**       * Adds a ThresholdFilter with the given level to an existing appender during the test.      *<p>      * Note that unless you filter existing appenders, changing the log level via setLoggerLevel      * will, as a side-effect, also apply and influence existing appenders. So the      * idea is to do eg addAppenderFilter("console", "warn") to make sure nothing      * gets logged on console, when changing a log level.      */
specifier|public
name|LogLevelModifier
name|addAppenderFilter
parameter_list|(
name|String
name|appenderName
parameter_list|,
name|String
name|level
parameter_list|)
block|{
name|appenderFilters
operator|.
name|add
argument_list|(
operator|new
name|AppenderFilter
argument_list|(
name|appenderName
argument_list|,
name|level
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Change the log level of a particular logger during the test **/
specifier|public
name|LogLevelModifier
name|setLoggerLevel
parameter_list|(
name|String
name|loggerName
parameter_list|,
name|String
name|level
parameter_list|)
block|{
name|loggerLevels
operator|.
name|add
argument_list|(
operator|new
name|LoggerLevel
argument_list|(
name|loggerName
argument_list|,
name|level
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
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
for|for
control|(
name|Iterator
argument_list|<
name|AppenderFilter
argument_list|>
name|it
init|=
name|appenderFilters
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AppenderFilter
name|appenderFilter
init|=
operator|(
name|AppenderFilter
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|appenderFilter
operator|.
name|starting
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|LoggerLevel
argument_list|>
name|it
init|=
name|loggerLevels
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|LoggerLevel
name|loggerLevel
init|=
operator|(
name|LoggerLevel
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|loggerLevel
operator|.
name|starting
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
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
for|for
control|(
name|Iterator
argument_list|<
name|AppenderFilter
argument_list|>
name|it
init|=
name|appenderFilters
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AppenderFilter
name|appenderFilter
init|=
operator|(
name|AppenderFilter
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|appenderFilter
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|LoggerLevel
argument_list|>
name|it
init|=
name|loggerLevels
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|LoggerLevel
name|loggerLevel
init|=
operator|(
name|LoggerLevel
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|loggerLevel
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Appender
argument_list|>
name|it
init|=
name|newAppenders
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Appender
name|appender
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|rootLogger
argument_list|()
operator|.
name|detachAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
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
comment|// nothing to do here
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

