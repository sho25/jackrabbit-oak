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
name|plugins
operator|.
name|index
operator|.
name|progress
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

begin_interface
specifier|public
interface|interface
name|NodeCountEstimator
block|{
name|NodeCountEstimator
name|NOOP
init|=
parameter_list|(
name|basePath
parameter_list|,
name|indexPaths
parameter_list|)
lambda|->
operator|-
literal|1
decl_stmt|;
comment|/**      * Provides an estimate of the sub tree node count at given path      * @param path path under which count is requested      * @return estimated count or -1 if unknown      */
name|long
name|getEstimatedNodeCount
parameter_list|(
name|String
name|basePath
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|indexPaths
parameter_list|)
function_decl|;
block|}
end_interface

end_unit
