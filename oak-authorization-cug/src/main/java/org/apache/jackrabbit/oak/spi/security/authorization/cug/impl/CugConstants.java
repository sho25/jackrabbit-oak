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
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

begin_comment
comment|/**  * Constants for the Closed User Group (CUG) feature.  */
end_comment

begin_interface
interface|interface
name|CugConstants
block|{
comment|/**      * The name of the mixin type that defines the CUG policy node.      */
name|String
name|MIX_REP_CUG_MIXIN
init|=
literal|"rep:CugMixin"
decl_stmt|;
comment|/**      * The primary node type name of the CUG policy node.      */
name|String
name|NT_REP_CUG_POLICY
init|=
literal|"rep:CugPolicy"
decl_stmt|;
comment|/**      * The name of the CUG policy node.      */
name|String
name|REP_CUG_POLICY
init|=
literal|"rep:cugPolicy"
decl_stmt|;
comment|/**      * The name of the hidden property that stores information about nested      * CUG policy nodes.      */
name|String
name|HIDDEN_NESTED_CUGS
init|=
literal|":nestedCugs"
decl_stmt|;
comment|/**      * The name of the hidden property that stores information about the number      * of CUG roots located close to the root node.      */
name|String
name|HIDDEN_TOP_CUG_CNT
init|=
literal|":topCugCnt"
decl_stmt|;
comment|/**      * The name of the property that stores the principal names that are allowed      * to access the restricted area defined by the CUG (closed user group).      */
name|String
name|REP_PRINCIPAL_NAMES
init|=
literal|"rep:principalNames"
decl_stmt|;
comment|/**      * Name of the configuration option that specifies the subtrees that allow      * to define closed user groups.      *      *<ul>      *<li>Value Type: String</li>      *<li>Default: -</li>      *<li>Multiple: true</li>      *</ul>      */
name|String
name|PARAM_CUG_SUPPORTED_PATHS
init|=
literal|"cugSupportedPaths"
decl_stmt|;
comment|/**      * Name of the configuration option that specifies if CUG content must      * be respected for permission evaluation.      *      *<ul>      *<li>Value Type: boolean</li>      *<li>Default: false</li>      *<li>Multiple: false</li>      *</ul>      */
name|String
name|PARAM_CUG_ENABLED
init|=
literal|"cugEnabled"
decl_stmt|;
block|}
end_interface

end_unit

