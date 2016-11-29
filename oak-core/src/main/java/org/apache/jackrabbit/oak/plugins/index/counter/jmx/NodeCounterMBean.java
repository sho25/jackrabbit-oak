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
name|counter
operator|.
name|jmx
package|;
end_package

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
name|jmx
operator|.
name|Description
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
name|jmx
operator|.
name|Name
import|;
end_import

begin_comment
comment|/**  * An MBean that provides an approximate node count for a given path.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeCounterMBean
block|{
name|String
name|TYPE
init|=
literal|"NodeCounter"
decl_stmt|;
comment|/**      * Get the estimated number of nodes below a given path.      *       * @param path the path      * @return the estimated number of nodes, or -1 if unknown (if not index is      *         available)      */
annotation|@
name|Description
argument_list|(
literal|"Get the estimated number of nodes below a given path."
argument_list|)
name|long
name|getEstimatedNodeCount
parameter_list|(
annotation|@
name|Description
argument_list|(
literal|"the path"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"path"
argument_list|)
name|String
name|path
parameter_list|)
function_decl|;
comment|/**      * Get the estimated number of nodes for the child nodes of a given path.      *       * @param path the path      * @param level the depth of the child nodes to list      * @return a comma separated list of child nodes with the respective      *         estimated counts      */
annotation|@
name|Description
argument_list|(
literal|"Get the estimated number of nodes below a given path."
argument_list|)
name|String
name|getEstimatedChildNodeCounts
parameter_list|(
annotation|@
name|Description
argument_list|(
literal|"the path"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"path"
argument_list|)
name|String
name|path
parameter_list|,
annotation|@
name|Description
argument_list|(
literal|"the depth of the child nodes to list (the higher the number, the slower)"
argument_list|)
annotation|@
name|Name
argument_list|(
literal|"level"
argument_list|)
name|int
name|level
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

