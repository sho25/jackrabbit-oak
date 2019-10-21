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
name|spi
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_class
specifier|public
class|class
name|GuavaDeprecation
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GuavaDeprecation
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT
init|=
literal|"debug"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TLOGLEVEL
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|GuavaDeprecation
operator|.
name|class
operator|+
literal|".LOGLEVEL"
argument_list|,
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|LOGLEVEL
decl_stmt|;
static|static
block|{
name|String
name|t
decl_stmt|;
switch|switch
condition|(
name|TLOGLEVEL
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
condition|)
block|{
case|case
literal|"error"
case|:
case|case
literal|"warn"
case|:
case|case
literal|"info"
case|:
case|case
literal|"debug"
case|:
name|t
operator|=
name|TLOGLEVEL
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
break|break;
default|default:
name|t
operator|=
name|DEFAULT
expr_stmt|;
break|break;
block|}
name|LOGLEVEL
operator|=
name|t
expr_stmt|;
block|}
specifier|private
name|GuavaDeprecation
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|handleCall
parameter_list|(
name|String
name|ticket
parameter_list|)
throws|throws
name|UnsupportedOperationException
block|{
name|handleCall
argument_list|(
name|ticket
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|handleCall
parameter_list|(
name|String
name|ticket
parameter_list|,
name|String
name|className
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|allowed
parameter_list|)
throws|throws
name|UnsupportedOperationException
block|{
name|String
name|message
init|=
literal|"use of deprecated Guava-related API - this method is going to be removed in future Oak releases - see %s for details"
decl_stmt|;
switch|switch
condition|(
name|LOGLEVEL
condition|)
block|{
case|case
literal|"error"
case|:
if|if
condition|(
name|LOG
operator|.
name|isErrorEnabled
argument_list|()
condition|)
block|{
name|Exception
name|ex
init|=
operator|new
name|Exception
argument_list|(
literal|"call stack"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deprecatedCaller
argument_list|(
name|ex
argument_list|,
name|className
argument_list|,
name|allowed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|ticket
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|"warn"
case|:
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|Exception
name|ex
init|=
operator|new
name|Exception
argument_list|(
literal|"call stack"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deprecatedCaller
argument_list|(
name|ex
argument_list|,
name|className
argument_list|,
name|allowed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|ticket
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|"info"
case|:
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|Exception
name|ex
init|=
operator|new
name|Exception
argument_list|(
literal|"call stack"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deprecatedCaller
argument_list|(
name|ex
argument_list|,
name|className
argument_list|,
name|allowed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|ticket
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|"debug"
case|:
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|Exception
name|ex
init|=
operator|new
name|Exception
argument_list|(
literal|"call stack"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deprecatedCaller
argument_list|(
name|ex
argument_list|,
name|className
argument_list|,
name|allowed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|ticket
argument_list|)
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|deprecatedCaller
parameter_list|(
name|Exception
name|ex
parameter_list|,
name|String
name|className
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|allowed
parameter_list|)
block|{
if|if
condition|(
name|allowed
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|boolean
name|classFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|el
range|:
name|ex
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|String
name|cn
init|=
name|el
operator|.
name|getClassName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|classFound
condition|)
block|{
comment|// still looking for the entry
name|classFound
operator|=
name|cn
operator|.
name|equals
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// still in class checked for?
if|if
condition|(
name|cn
operator|.
name|equals
argument_list|(
name|className
argument_list|)
condition|)
block|{
comment|// go one
block|}
else|else
block|{
comment|// check caller
for|for
control|(
name|String
name|a
range|:
name|allowed
control|)
block|{
if|if
condition|(
name|cn
operator|.
name|startsWith
argument_list|(
name|a
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

