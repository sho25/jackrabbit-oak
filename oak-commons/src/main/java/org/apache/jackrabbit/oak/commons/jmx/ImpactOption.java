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
name|jmx
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanOperationInfo
import|;
end_import

begin_enum
specifier|public
enum|enum
name|ImpactOption
block|{
comment|/**      * Indicates that the operation is a write-like,      * and would modify the MBean in some way, typically by writing some value      * or changing a configuration.      */
name|ACTION
parameter_list|(
name|MBeanOperationInfo
operator|.
name|ACTION
parameter_list|)
operator|,
comment|/**      * Indicates that the operation is both read-like and write-like.      */
constructor|ACTION_INFO(MBeanOperationInfo.ACTION_INFO
block|)
enum|,
comment|/**      * Indicates that the operation is read-like,      * it basically returns information.      */
name|INFO
argument_list|(
name|MBeanOperationInfo
operator|.
name|INFO
argument_list|)
operator|,
comment|/**      * Indicates that the operation has an "unknown" nature.      */
name|UNKNOWN
argument_list|(
name|MBeanOperationInfo
operator|.
name|UNKNOWN
argument_list|)
enum|;
end_enum

begin_function
specifier|public
name|int
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
end_function

begin_decl_stmt
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
end_decl_stmt

begin_constructor
specifier|private
name|ImpactOption
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
end_constructor

unit|}
end_unit

