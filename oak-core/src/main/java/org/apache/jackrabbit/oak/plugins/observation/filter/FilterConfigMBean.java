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
name|observation
operator|.
name|filter
package|;
end_package

begin_interface
specifier|public
interface|interface
name|FilterConfigMBean
block|{
name|String
name|TYPE
init|=
literal|"FilterConfig"
decl_stmt|;
comment|/**      * A set of paths whose subtrees include all events of this filter.      * @return  list of paths      * @see org.apache.jackrabbit.oak.plugins.observation.filter.FilterBuilder#addSubTree(String)      */
name|String
index|[]
name|getSubTrees
parameter_list|()
function_decl|;
comment|/**      * Whether to include cluster local changes.      *      * @return true if cluster local changes need to be included      */
name|boolean
name|isIncludeClusterLocal
parameter_list|()
function_decl|;
comment|/**      * Whether to include cluster external changes.      *      * @return true if cluster external changes need to be included      */
name|boolean
name|isIncludeClusterExternal
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

