begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|javax
operator|.
name|jcr
operator|.
name|UnsupportedRepositoryOperationException
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
comment|/**  * Helper class for identifying partially implemented features and  * controlling their runtime behavior.  *  * @see<a href="https://issues.apache.org/jira/browse/OAK-193">OAK-193</a>  */
end_comment

begin_class
specifier|public
class|class
name|TODO
block|{
specifier|private
specifier|static
specifier|final
name|String
name|mode
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"todo"
argument_list|,
literal|"strict"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|strict
init|=
literal|"strict"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|log
init|=
literal|"log"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|TODO
name|unimplemented
parameter_list|()
block|{
return|return
operator|new
name|TODO
argument_list|(
literal|"unimplemented"
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|UnsupportedOperationException
name|exception
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|private
name|TODO
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
operator|new
name|UnsupportedOperationException
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|StackTraceElement
index|[]
name|trace
init|=
name|exception
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
operator|!=
literal|null
operator|&&
name|trace
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|String
name|className
init|=
name|trace
index|[
literal|2
index|]
operator|.
name|getClassName
argument_list|()
decl_stmt|;
name|String
name|methodName
init|=
name|trace
index|[
literal|2
index|]
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|this
operator|.
name|logger
operator|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
literal|"TODO: "
operator|+
name|className
operator|+
literal|"."
operator|+
name|methodName
operator|+
literal|"() - "
operator|+
name|message
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|logger
operator|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TODO
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
literal|"TODO: "
operator|+
name|message
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|doNothing
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
block|{
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
operator|new
name|UnsupportedRepositoryOperationException
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|log
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

