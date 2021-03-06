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
name|commons
operator|.
name|PathUtils
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
class|class
name|PathConstraint
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
name|PathConstraint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|requiredValue
decl_stmt|;
specifier|public
name|PathConstraint
parameter_list|(
name|String
name|definition
parameter_list|)
block|{
name|this
operator|.
name|requiredValue
operator|=
name|definition
expr_stmt|;
block|}
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
try|try
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|requiredValue
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|requiredValue
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|actual
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
name|String
name|required
init|=
name|requiredValue
decl_stmt|;
if|if
condition|(
name|required
operator|.
name|endsWith
argument_list|(
literal|"/*"
argument_list|)
condition|)
block|{
name|required
operator|=
name|required
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|required
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|required
argument_list|,
name|actual
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
name|required
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
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
literal|"Error checking path constraint "
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|'\''
operator|+
name|requiredValue
operator|+
literal|'\''
return|;
block|}
block|}
end_class

end_unit

