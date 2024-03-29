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
operator|.
name|xml
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
comment|/**  * Utility class defining specific, configurable import behavior. A given  * implementation of the {@link ProtectedItemImporter} may support this  * as part of the overall import configuration.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ImportBehavior
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
name|ImportBehavior
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * If a value cannot be set due to constraints      * enforced by the API implementation, the failure is logged as      * warning and the value is ignored.      */
specifier|public
specifier|static
specifier|final
name|int
name|IGNORE
init|=
literal|1
decl_stmt|;
comment|/**      * Same as {@link #IGNORE} but in addition tries to circumvent the      * problem. This option should only be used with validated and trusted      * XML passed to the {@code Session} and {@code Workspace} import API.      */
specifier|public
specifier|static
specifier|final
name|int
name|BESTEFFORT
init|=
literal|2
decl_stmt|;
comment|/**      * Aborts the import as soon as invalid values are detected throwing      * a {@code ConstraintViolationException}.      */
specifier|public
specifier|static
specifier|final
name|int
name|ABORT
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAME_IGNORE
init|=
literal|"ignore"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAME_BESTEFFORT
init|=
literal|"besteffort"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAME_ABORT
init|=
literal|"abort"
decl_stmt|;
comment|/**      * Private constructor to avoid instantiation.      */
specifier|private
name|ImportBehavior
parameter_list|()
block|{}
specifier|public
specifier|static
name|int
name|valueFromString
parameter_list|(
name|String
name|behaviorString
parameter_list|)
block|{
if|if
condition|(
name|NAME_IGNORE
operator|.
name|equalsIgnoreCase
argument_list|(
name|behaviorString
argument_list|)
condition|)
block|{
return|return
name|IGNORE
return|;
block|}
elseif|else
if|if
condition|(
name|NAME_BESTEFFORT
operator|.
name|equalsIgnoreCase
argument_list|(
name|behaviorString
argument_list|)
condition|)
block|{
return|return
name|BESTEFFORT
return|;
block|}
elseif|else
if|if
condition|(
name|NAME_ABORT
operator|.
name|equalsIgnoreCase
argument_list|(
name|behaviorString
argument_list|)
condition|)
block|{
return|return
name|ABORT
return|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid behavior {} -> Using default: ABORT."
argument_list|,
name|behaviorString
argument_list|)
expr_stmt|;
return|return
name|ABORT
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|nameFromValue
parameter_list|(
name|int
name|importBehavior
parameter_list|)
block|{
switch|switch
condition|(
name|importBehavior
condition|)
block|{
case|case
name|ImportBehavior
operator|.
name|IGNORE
case|:
return|return
name|NAME_IGNORE
return|;
case|case
name|ImportBehavior
operator|.
name|ABORT
case|:
return|return
name|NAME_ABORT
return|;
case|case
name|ImportBehavior
operator|.
name|BESTEFFORT
case|:
return|return
name|NAME_BESTEFFORT
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid import behavior: "
operator|+
name|importBehavior
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

