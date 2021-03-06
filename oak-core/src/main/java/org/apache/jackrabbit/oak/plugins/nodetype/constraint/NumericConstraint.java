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
name|plugins
operator|.
name|nodetype
operator|.
name|constraint
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|core
operator|.
name|GuavaDeprecation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
specifier|abstract
class|class
name|NumericConstraint
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Predicate
argument_list|<
name|Value
argument_list|>
implements|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
argument_list|<
name|Value
argument_list|>
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
name|NumericConstraint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|invalid
decl_stmt|;
specifier|private
name|boolean
name|lowerInclusive
decl_stmt|;
specifier|private
name|T
name|lowerBound
decl_stmt|;
specifier|private
name|T
name|upperBound
decl_stmt|;
specifier|private
name|boolean
name|upperInclusive
decl_stmt|;
specifier|protected
name|NumericConstraint
parameter_list|(
name|String
name|definition
parameter_list|)
block|{
comment|// format: '(<min>,<max>)',  '[<min>,<max>]', '(,<max>)' etc.
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([\\(\\[])([^,]*),([^\\)\\]]*)([\\)\\]])"
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|definition
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
block|{
comment|// group 1 is lower bound inclusive/exclusive
name|lowerInclusive
operator|=
literal|"["
operator|.
name|equals
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// group 2 is lower, group 3 is upper bound
name|lowerBound
operator|=
name|getBound
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|upperBound
operator|=
name|getBound
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// group 4 is upper bound inclusive/exclusive
name|upperInclusive
operator|=
literal|"]"
operator|.
name|equals
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|invalid
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|invalid
argument_list|(
name|definition
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|invalid
parameter_list|(
name|String
name|definition
parameter_list|)
block|{
name|invalid
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|'\''
operator|+
name|definition
operator|+
literal|"' is not a valid value constraint format for numeric values"
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|T
name|getBound
parameter_list|(
name|String
name|bound
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
annotation|@
name|Nullable
name|Value
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|invalid
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|T
name|t
init|=
name|getValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|lowerBound
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|lowerInclusive
condition|)
block|{
if|if
condition|(
name|less
argument_list|(
name|t
argument_list|,
name|lowerBound
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|lessOrEqual
argument_list|(
name|t
argument_list|,
name|lowerBound
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
if|if
condition|(
name|upperBound
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|upperInclusive
condition|)
block|{
if|if
condition|(
name|greater
argument_list|(
name|t
argument_list|,
name|upperBound
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|greaterOrEqual
argument_list|(
name|t
argument_list|,
name|upperBound
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error checking numeric constraint "
operator|+
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**      * @deprecated use {@link #test(Value)} instead  (see<a href="https://issues.apache.org/jira/browse/OAK-8874">OAK-8874</a>)      */
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Value
name|value
parameter_list|)
block|{
name|GuavaDeprecation
operator|.
name|handleCall
argument_list|(
literal|"OAK-8874"
argument_list|)
expr_stmt|;
return|return
name|test
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|T
name|getValue
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|less
parameter_list|(
name|T
name|val
parameter_list|,
name|T
name|bound
parameter_list|)
function_decl|;
specifier|protected
name|boolean
name|greater
parameter_list|(
name|T
name|val
parameter_list|,
name|T
name|bound
parameter_list|)
block|{
return|return
name|less
argument_list|(
name|bound
argument_list|,
name|val
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|equals
parameter_list|(
name|T
name|val
parameter_list|,
name|T
name|bound
parameter_list|)
block|{
return|return
name|val
operator|.
name|equals
argument_list|(
name|bound
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|greaterOrEqual
parameter_list|(
name|T
name|val
parameter_list|,
name|T
name|bound
parameter_list|)
block|{
return|return
name|greater
argument_list|(
name|val
argument_list|,
name|bound
argument_list|)
operator|||
name|equals
argument_list|(
name|val
argument_list|,
name|bound
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|lessOrEqual
parameter_list|(
name|T
name|val
parameter_list|,
name|T
name|bound
parameter_list|)
block|{
return|return
name|less
argument_list|(
name|val
argument_list|,
name|bound
argument_list|)
operator|||
name|equals
argument_list|(
name|val
argument_list|,
name|bound
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|lowerInclusive
condition|?
literal|"["
else|:
literal|"("
operator|)
operator|+
operator|(
name|lowerBound
operator|==
literal|null
condition|?
literal|""
else|:
name|lowerBound
operator|)
operator|+
literal|", "
operator|+
operator|(
name|upperBound
operator|==
literal|null
condition|?
literal|""
else|:
name|upperBound
operator|)
operator|+
operator|(
name|upperInclusive
condition|?
literal|"]"
else|:
literal|")"
operator|)
return|;
block|}
block|}
end_class

end_unit

