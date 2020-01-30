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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|BooleanConstraint
implements|implements
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
name|BooleanConstraint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Boolean
name|requiredValue
decl_stmt|;
specifier|public
name|BooleanConstraint
parameter_list|(
name|String
name|definition
parameter_list|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|definition
argument_list|)
condition|)
block|{
name|requiredValue
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|definition
argument_list|)
condition|)
block|{
name|requiredValue
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|requiredValue
operator|=
literal|null
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|'\''
operator|+
name|definition
operator|+
literal|"' is not a valid value constraint format for boolean values"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|value
operator|!=
literal|null
operator|&&
name|requiredValue
operator|!=
literal|null
operator|&&
name|value
operator|.
name|getBoolean
argument_list|()
operator|==
name|requiredValue
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
literal|"Error checking boolean constraint "
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"'"
operator|+
name|requiredValue
operator|+
literal|'\''
return|;
block|}
block|}
end_class

end_unit

