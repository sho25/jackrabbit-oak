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
name|run
operator|.
name|cli
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_interface
specifier|public
interface|interface
name|OptionsBean
block|{
name|void
name|configure
parameter_list|(
name|OptionSet
name|options
parameter_list|)
function_decl|;
comment|/**      * Title string for this set of options      */
name|String
name|title
parameter_list|()
function_decl|;
comment|/**      * Description string for this set of options      */
name|String
name|description
parameter_list|()
function_decl|;
comment|/**      * Used to sort the help output. Help for OptionsBean in descending order i.e.      * bean having highest order would be rendered first      */
name|int
name|order
parameter_list|()
function_decl|;
comment|/**      * Option names which are actually performing operation      */
name|Set
argument_list|<
name|String
argument_list|>
name|operationNames
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

